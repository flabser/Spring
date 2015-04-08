package com.linuxense.javadbf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Vector;

class DBFHeader {
    static final byte SIG_DBASE_III = 3;
    byte signature = 3;
    byte year;
    byte month;
    byte day;
    int numberOfRecords;
    short headerLength;
    short recordLength;
    short reserv1;
    byte incompleteTransaction;
    byte encryptionFlag;
    int freeRecordThread;
    int reserv2;
    int reserv3;
    byte mdxFlag;
    byte languageDriver;
    short reserv4;
    DBFField[] fieldArray;
    byte terminator1 = 13;

    DBFHeader() {
    }

    void read(DataInput dataInput) throws IOException {
        this.signature = dataInput.readByte();
        this.year = dataInput.readByte();
        this.month = dataInput.readByte();
        this.day = dataInput.readByte();
        this.numberOfRecords = Utils.readLittleEndianInt(dataInput);
        this.headerLength = Utils.readLittleEndianShort(dataInput);
        this.recordLength = Utils.readLittleEndianShort(dataInput);
        this.reserv1 = Utils.readLittleEndianShort(dataInput);
        this.incompleteTransaction = dataInput.readByte();
        this.encryptionFlag = dataInput.readByte();
        this.freeRecordThread = Utils.readLittleEndianInt(dataInput);
        this.reserv2 = dataInput.readInt();
        this.reserv3 = dataInput.readInt();
        this.mdxFlag = dataInput.readByte();
        this.languageDriver = dataInput.readByte();
        this.reserv4 = Utils.readLittleEndianShort(dataInput);
        Vector v_fields = new Vector();

        for(DBFField field = DBFField.createField(dataInput); field != null; field = DBFField.createField(dataInput)) {
            v_fields.addElement(field);
        }

        this.fieldArray = new DBFField[v_fields.size()];

        for(int i = 0; i < this.fieldArray.length; ++i) {
            this.fieldArray[i] = (DBFField)v_fields.elementAt(i);
        }

    }

    void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(this.signature);
        GregorianCalendar calendar = new GregorianCalendar();
        this.year = (byte)(calendar.get(1) - 1900);
        this.month = (byte)(calendar.get(2) + 1);
        this.day = (byte)calendar.get(5);
        dataOutput.writeByte(this.year);
        dataOutput.writeByte(this.month);
        dataOutput.writeByte(this.day);
        this.numberOfRecords = Utils.littleEndian(this.numberOfRecords);
        dataOutput.writeInt(this.numberOfRecords);
        this.headerLength = this.findHeaderLength();
        dataOutput.writeShort(Utils.littleEndian(this.headerLength));
        this.recordLength = this.findRecordLength();
        dataOutput.writeShort(Utils.littleEndian(this.recordLength));
        dataOutput.writeShort(Utils.littleEndian(this.reserv1));
        dataOutput.writeByte(this.incompleteTransaction);
        dataOutput.writeByte(this.encryptionFlag);
        dataOutput.writeInt(Utils.littleEndian(this.freeRecordThread));
        dataOutput.writeInt(Utils.littleEndian(this.reserv2));
        dataOutput.writeInt(Utils.littleEndian(this.reserv3));
        dataOutput.writeByte(this.mdxFlag);
        dataOutput.writeByte(this.languageDriver);
        dataOutput.writeShort(Utils.littleEndian(this.reserv4));

        for (DBFField aFieldArray : this.fieldArray) {
            aFieldArray.write(dataOutput);
        }

        dataOutput.writeByte(this.terminator1);
    }

    short findHeaderLength() {
        return (short)(32 + 32 * this.fieldArray.length + 1);
    }

    short findRecordLength() {
        int recordLength = 0;

        for (DBFField aFieldArray : this.fieldArray) {
            recordLength += aFieldArray.getFieldLength();
        }

        return (short)(recordLength + 1);
    }
}