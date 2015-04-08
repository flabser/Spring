package com.linuxense.javadbf;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class DBFReader extends DataInputStream{
    private DBFHeader header;
    boolean isClosed = true;
    protected String characterSetName = "8859_1";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DBFReader(InputStream in) throws DBFException {
        super(in);
        this.isClosed = false;
        this.header = new DBFHeader();
        try {

            this.header.read(this);

            int e = this.header.headerLength - (32 + 32 * this.header.fieldArray.length) - 1;
            if(e > 0) {
                this.skip((long)e);
            }

        } catch (IOException var3) {
            throw new DBFException(var3.getMessage());
        }
    }

    public short getHeaderLength(){
        return header.headerLength != 0 ? header.headerLength : header.findHeaderLength();
    }

    public short getRecordLength(){
        return header.findRecordLength();
    }

    public void setCharactersetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.header.year + "/" + this.header.month + "/" + this.header.day + "\n" + "Total records: " + this.header.numberOfRecords + "\nHEader length: " + this.header.headerLength + "");

        for(int i = 0; i < this.header.fieldArray.length; ++i) {
            sb.append(this.header.fieldArray[i].getName());
            sb.append("\n");
        }

        return sb.toString();
    }

    public int getRecordCount() {
        return this.header.numberOfRecords;
    }

    public DBFField getField(int index) throws DBFException {
        if(this.isClosed) {
            throw new DBFException("Source is not open");
        } else {
            return this.header.fieldArray[index];
        }
    }

    public int getFieldCount() throws DBFException {
        if(this.isClosed) {
            throw new DBFException("Source is not open");
        } else {
            return this.header.fieldArray != null?this.header.fieldArray.length:-1;
        }
    }

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String[] nextRecord() throws DBFException {
        if(this.isClosed) {
            throw new DBFException("Source is not open");
        } else {
            String[] recordObjects = new String[this.header.fieldArray.length];

            try {
                boolean e = false;

                do {
                    if(e) {
                        this.skip((long) (this.header.recordLength - 1));
                    }

                    byte e1 = this.readByte();
                    if(e1 == 26) {
                        return null;
                    }

                    e = e1 == 42;
                } while(e);

                for(int var14 = 0; var14 < this.header.fieldArray.length; ++var14) {
                    byte[] t_logical;
                    switch(this.header.fieldArray[var14].getDataType()) {
                        case 67:
                            byte[] b_array = new byte[this.header.fieldArray[var14].getFieldLength()];
                            this.read(b_array);
                            String s = new String(b_array, this.characterSetName).trim();
                            recordObjects[var14] = s.length() > 0 ? s.replaceAll("['`]+", "\"").replaceAll("[\t\n]", "").replaceAll("\\\\", "/").toUpperCase(): "\\N";
                            break;
                        case 68:
                            byte[] t_byte_year = new byte[4];
                            this.read(t_byte_year);
                            byte[] t_byte_month = new byte[2];
                            this.read(t_byte_month);
                            byte[] t_byte_day = new byte[2];
                            this.read(t_byte_day);

                            try {

                                GregorianCalendar var16 = new GregorianCalendar(Integer.parseInt(new String(t_byte_year)), Integer.parseInt(new String(t_byte_month)) - 1, Integer.parseInt(new String(t_byte_day)));
                                recordObjects[var14] = DF.format(var16.getTime()); // var16.getTime();
                            } catch (NumberFormatException var9) {
                                recordObjects[var14] = "\\N";
                            }
                            break;
                        case 69:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        default:
                            recordObjects[var14] = "\\N";
                            break;
                        case 70:
                            try {
                                t_logical = new byte[this.header.fieldArray[var14].getFieldLength()];
                                this.read(t_logical);
                                t_logical = Utils.trimLeftSpaces(t_logical);
                                if(t_logical.length > 0 && !Utils.contains(t_logical, (byte)63)) {
                                    recordObjects[var14] = new Float(new String(t_logical)).toString();
                                } else {
                                    recordObjects[var14] = "\\N";
                                }
                                break;
                            } catch (NumberFormatException var11) {
//                                throw new DBFException("Failed to parse Float: " + var11.getMessage());
                                recordObjects[var14] = "\\N";
                            }
                        case 76:
                            byte var15 = this.readByte();
                            switch (var15){
                                case 89:
                                case 116:
                                case 84:
                                    recordObjects[var14] = "t";
                                    break;
                                default:
                                    recordObjects[var14] = "f";
                                    break;

                            }
//                            if(var15 != 89 && var15 != 116 && var15 != 84 && var15 != 116) {
//                                recordObjects[var14] = "false"; //Boolean.FALSE;
//                            } else {
//                                recordObjects[var14] = "true"; //Boolean.TRUE;
//                            }
                            break;
                        case 77:
                            recordObjects[var14] = "\\N";
                            break;
                        case 78:
                            try {
                                t_logical = new byte[this.header.fieldArray[var14].getFieldLength()];
                                this.read(t_logical);
                                t_logical = Utils.trimLeftSpaces(t_logical);
                                if(t_logical.length > 0 && !Utils.contains(t_logical, (byte)63)) {
                                    recordObjects[var14] = new Double(new String(t_logical)).toString();
                                } else {
                                    recordObjects[var14] = "\\N";
                                }
                            } catch (NumberFormatException var10) {
//                                throw new DBFException("Failed to parse Number: " + var10.getMessage());
                                recordObjects[var14] = "\\N";
                            }
                    }
                }

                return recordObjects;
            } catch (EOFException var12) {
                return null;
            } catch (IOException var13) {
                throw new DBFException(var13.getMessage());
            }
        }
    }
}
