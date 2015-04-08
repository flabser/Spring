//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.linuxense.javadbf;

import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class DBFWriter{
    DBFHeader header;
    Vector v_records = new Vector();
    int recordCount = 0;
    RandomAccessFile raf = null;
    protected String characterSetName = "8859_1";
    boolean appendMode = false;

    public DBFWriter() {
        this.header = new DBFHeader();
    }

    public DBFWriter(File dbfFile) throws DBFException {
        try {
            this.raf = new RandomAccessFile(dbfFile, "rw");
            if(!dbfFile.exists() || dbfFile.length() == 0L) {
                this.header = new DBFHeader();
                return;
            }

            this.header = new DBFHeader();
            this.header.read(this.raf);
            this.raf.seek(this.raf.length() - 1L);
        } catch (FileNotFoundException var4) {
            throw new DBFException("Specified file is not found. " + var4.getMessage());
        } catch (IOException var5) {
            throw new DBFException(var5.getMessage() + " while reading header");
        }

        this.recordCount = this.header.numberOfRecords;
    }

    public void setCharactersetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public void setFields(DBFField[] fields) throws DBFException {
        if(this.header.fieldArray != null) {
            throw new DBFException("Fields has already been set");
        } else if(fields != null && fields.length != 0) {
            for(int i = 0; i < fields.length; ++i) {
                if(fields[i] == null) {
                    throw new DBFException("Field " + (i + 1) + " is null");
                }
            }

            this.header.fieldArray = fields;

            try {
                if(this.raf != null && this.raf.length() == 0L) {
                    this.header.write(this.raf);
                }

            } catch (IOException var4) {
                throw new DBFException("Error accesing file");
            }
        } else {
            throw new DBFException("Should have at least one field");
        }
    }

    public void addRecord(Object[] values) throws DBFException {
        if(this.header.fieldArray == null) {
            throw new DBFException("Fields should be set before adding records");
        } else if(values == null) {
            throw new DBFException("Null cannot be added as row");
        } else if(values.length != this.header.fieldArray.length) {
            throw new DBFException("Invalid record. Invalid number of fields in row");
        } else {
            for(int i = 0; i < this.header.fieldArray.length; ++i) {
                if(values[i] != null) {
                    switch(this.header.fieldArray[i].getDataType()) {
                        case 67:
                            if(!(values[i] instanceof String)) {
                                throw new DBFException("Invalid value for field " + i);
                            }
                            break;
                        case 68:
                            if(!(values[i] instanceof Date)) {
                                throw new DBFException("Invalid value for field " + i);
                            }
                        case 69:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 77:
                        default:
                            break;
                        case 70:
                            if(!(values[i] instanceof Double)) {
                                throw new DBFException("Invalid value for field " + i);
                            }
                            break;
                        case 76:
                            if(!(values[i] instanceof Boolean)) {
                                throw new DBFException("Invalid value for field " + i);
                            }
                            break;
                        case 78:
                            if(!(values[i] instanceof Double)) {
                                throw new DBFException("Invalid value for field " + i);
                            }
                    }
                }
            }

            if(this.raf == null) {
                this.v_records.addElement(values);
            } else {
                try {
                    this.writeRecord(this.raf, values);
                    ++this.recordCount;
                } catch (IOException var4) {
                    throw new DBFException("Error occured while writing record. " + var4.getMessage());
                }
            }

        }
    }

    public void write(OutputStream out) throws DBFException {
        try {
            if(this.raf == null) {
                DataOutputStream e = new DataOutputStream(out);
                this.header.numberOfRecords = this.v_records.size();
                this.header.write(e);
                int t_recCount = this.v_records.size();

                for(int i = 0; i < t_recCount; ++i) {
                    Object[] t_values = (Object[])this.v_records.elementAt(i);
                    this.writeRecord(e, t_values);
                }

                e.write(26);
                e.flush();
            } else {
                this.header.numberOfRecords = this.recordCount;
                this.raf.seek(0L);
                this.header.write(this.raf);
                this.raf.seek(this.raf.length());
                this.raf.writeByte(26);
                this.raf.close();
            }

        } catch (IOException var6) {
            throw new DBFException(var6.getMessage());
        }
    }

    public void write() throws DBFException {
        this.write((OutputStream)null);
    }

    private void writeRecord(DataOutput dataOutput, Object[] objectArray) throws IOException {
        dataOutput.write(32);

        for(int j = 0; j < this.header.fieldArray.length; ++j) {
            switch(this.header.fieldArray[j].getDataType()) {
                case 67:
                    if(objectArray[j] != null) {
                        String var6 = objectArray[j].toString();
                        dataOutput.write(Utils.textPadding(var6, this.characterSetName, this.header.fieldArray[j].getFieldLength()));
                    } else {
                        dataOutput.write(Utils.textPadding("", this.characterSetName, this.header.fieldArray[j].getFieldLength()));
                    }
                    break;
                case 68:
                    if(objectArray[j] != null) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime((Date)objectArray[j]);
                        new StringBuffer();
                        dataOutput.write(String.valueOf(calendar.get(1)).getBytes());
                        dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(2) + 1), this.characterSetName, 2, 12, (byte)48));
                        dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(5)), this.characterSetName, 2, 12, (byte)48));
                    } else {
                        dataOutput.write("        ".getBytes());
                    }
                    break;
                case 69:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                default:
                    throw new DBFException("Unknown field type " + this.header.fieldArray[j].getDataType());
                case 70:
                    if(objectArray[j] != null) {
                        dataOutput.write(Utils.doubleFormating((Double)objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
                    } else {
                        dataOutput.write(Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), 12));
                    }
                    break;
                case 76:
                    if(objectArray[j] != null) {
                        if((Boolean)objectArray[j] == Boolean.TRUE) {
                            dataOutput.write(84);
                        } else {
                            dataOutput.write(70);
                        }
                    } else {
                        dataOutput.write(63);
                    }
                case 77:
                    break;
                case 78:
                    if(objectArray[j] != null) {
                        dataOutput.write(Utils.doubleFormating((Double)objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
                    } else {
                        dataOutput.write(Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), 12));
                    }
            }
        }

    }
}
