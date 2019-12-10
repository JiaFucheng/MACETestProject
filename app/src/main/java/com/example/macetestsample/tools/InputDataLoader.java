package com.example.macetestsample.tools;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

public class InputDataLoader {

    private static final String TAG = "InputDataLoader";

    public static float[] loadInputData(String filename, int width, int height, Representation repre) {
        try {
            //Log.i(TAG, "Loading input data from file " + filename);
            File file = new File(filename);
            if (!file.exists())
                return null;

            int channel = 3;
            if (repre == Representation.MV)
                channel = 2;

            int dataSize = width * height * channel * 4;
            byte[] tmpBytes = new byte[dataSize];

            int readBytes = 0;
            int offset = 0;
            int leftLength = dataSize;
            FileInputStream fis = new FileInputStream(file);
            while((readBytes = fis.read(tmpBytes, offset, leftLength)) != -1 && leftLength > 0) {
                //Log.i(TAG, String.format(Locale.CHINA, "Read %d Left %d", readBytes, leftLength));
                offset     += readBytes;
                leftLength -= readBytes;
            }

            float[] inputData = DataTypeConvert.byteArrayToFloatArray(tmpBytes);

            fis.close();

            if (inputData.length != width * height * channel) {
                Log.w(TAG, String.format(Locale.CHINA,
                                         "Input data length %d is not euqal with w*h*c %d*%d*%d",
                                         inputData.length, width, height, channel));
            }

            return inputData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float[] randomInputData(int width, int height, int channel) {
        int[] inputSize = {1, width, height, channel};
        float[] inputData = new float[inputSize[0] * inputSize[1] *
                                      inputSize[2] * inputSize[3]];

        fillRandomData(inputData);

        return inputData;
    }
    
    public static float[] randomInputData(int width, int height, Representation representation) {
        int channel = 3;
        if (representation == Representation.MV)
            channel = 2;
        
        return randomInputData(width, height, channel);
    }

    private static void fillRandomData(float[] input) {
        for (int i = 0; i < input.length; i ++) {
            input[i] = (float) (Math.random() * 2.0f - 1.0f);
        }
    }

}
