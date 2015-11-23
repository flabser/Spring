package kz.lof.dbf;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;

public final class Utils {
    public static final int ALIGN_LEFT = 10;
    public static final int ALIGN_RIGHT = 12;

    private Utils() {
    }

    public static int readLittleEndianInt(DataInput in) throws IOException {
        int bigEndian = 0;

        for(int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            bigEndian |= (in.readUnsignedByte() & 255) << shiftBy;
        }

        return bigEndian;
    }

    public static short readLittleEndianShort(DataInput in) throws IOException {
        int low = in.readUnsignedByte() & 255;
        int high = in.readUnsignedByte();
        return (short)(high << 8 | low);
    }

    public static byte[] trimLeftSpaces(byte[] arr) {
        StringBuffer t_sb = new StringBuffer(arr.length);

        for(int i = 0; i < arr.length; ++i) {
            if(arr[i] != 32) {
                t_sb.append((char)arr[i]);
            }
        }

        return t_sb.toString().getBytes();
    }

    public static short littleEndian(short value) {
        short mask = 255;
        short num2 = (short)(value & mask);
        num2 = (short)(num2 << 8);
        short mask1 = (short)(mask << 8);
        num2 = (short)(num2 | (value & mask1) >> 8);
        return num2;
    }

    public static int littleEndian(int value) {
        int num1 = value;
        int mask = 255;
        byte num2 = 0;
        int var5 = num2 | value & mask;

        for(int i = 1; i < 4; ++i) {
            var5 <<= 8;
            mask <<= 8;
            var5 |= (num1 & mask) >> 8 * i;
        }

        return var5;
    }

    public static byte[] textPadding(String text, String characterSetName, int length) throws UnsupportedEncodingException {
        return textPadding(text, characterSetName, length, 10);
    }

    public static byte[] textPadding(String text, String characterSetName, int length, int alignment) throws UnsupportedEncodingException {
        return textPadding(text, characterSetName, length, alignment, (byte)32);
    }

    public static byte[] textPadding(String text, String characterSetName, int length, int alignment, byte paddingByte) throws UnsupportedEncodingException {
        if(text.length() >= length) {
            return text.substring(0, length).getBytes(characterSetName);
        } else {
            byte[] byte_array = new byte[length];
            Arrays.fill(byte_array, paddingByte);
            switch(alignment) {
                case 10:
                    System.arraycopy(text.getBytes(characterSetName), 0, byte_array, 0, text.length());
                    break;
                case 12:
                    int t_offset = length - text.length();
                    System.arraycopy(text.getBytes(characterSetName), 0, byte_array, t_offset, text.length());
            }

            return byte_array;
        }
    }

    public static byte[] doubleFormating(Double doubleNum, String characterSetName, int fieldLength, int sizeDecimalPart) throws UnsupportedEncodingException {
        int sizeWholePart = fieldLength - (sizeDecimalPart > 0?sizeDecimalPart + 1:0);
        StringBuffer format = new StringBuffer(fieldLength);

        for(int i = 0; i < sizeWholePart; ++i) {
            format.append("#");
        }

        if(sizeDecimalPart > 0) {
            format.append(".");

            for(int df = 0; df < sizeDecimalPart; ++df) {
                format.append("0");
            }
        }

        DecimalFormat var8 = new DecimalFormat(format.toString());
        return textPadding(var8.format(doubleNum.doubleValue()).toString(), characterSetName, fieldLength, 12);
    }

    public static boolean contains(byte[] arr, byte value) {
        boolean found = false;

        for(int i = 0; i < arr.length; ++i) {
            if(arr[i] == value) {
                found = true;
                break;
            }
        }

        return found;
    }
}
