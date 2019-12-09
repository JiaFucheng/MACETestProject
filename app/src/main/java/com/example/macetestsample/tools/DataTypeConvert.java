package com.example.macetestsample.tools;

public class DataTypeConvert {

    public static int bytesToInt(byte[] bytes) {
        return (0xFF000000 & (bytes[0] << 24)) |
               (0x00FF0000 & (bytes[1] << 16)) |
               (0x0000FF00 & (bytes[2] << 8))  |
               (0x000000FF &  bytes[3]);
    }

    public static float bytesToFloat(byte[] bytes) {
        return Float.intBitsToFloat(bytesToInt(bytes));
    }

    public static float[] byteArrayToFloatArray(byte[] byteArray) {
        int floatArrayLength = byteArray.length >> 2;
        byte[] tmpBytes = new byte[4];
        float[] floatArray = new float[floatArrayLength];
        int offset = 0;

        for (int i = 0; i < floatArray.length; i ++) {
            System.arraycopy(byteArray, offset, tmpBytes, 0, tmpBytes.length);
            offset += tmpBytes.length;
            floatArray[i] = bytesToFloat(tmpBytes);
        }

        return floatArray;
    }

}
