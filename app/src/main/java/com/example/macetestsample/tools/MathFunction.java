package com.example.macetestsample.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class MathFunction {

    public static int argmax(float[] floats) {
        if (floats == null)
            return -1;

        int maxIndex = -1;
        float maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < floats.length; i ++) {
            //Log.d("Argmax", String.format(Locale.CHINA,
            //                              "cur value %f max index %d max value %f",
            //                              floats[i], maxIndex, maxValue));
            if (floats[i] > maxValue) {
                maxIndex = i;
                maxValue = floats[i];
            }
        }

        return maxIndex;
    }

    public static float[] averageFloatsArray(ArrayList<float[]> floatsArray) {
        if (floatsArray == null || floatsArray.size() == 0)
            return null;

        int floatsLength = floatsArray.get(0).length;
        float[] finalFloats = new float[floatsLength];
        float sum;
        for (int i = 0; i < floatsLength; i ++) {
            sum = 0;
            for (int j = 0; j < floatsArray.size(); j ++)
                sum += floatsArray.get(j)[i];
            finalFloats[i] = sum / floatsArray.size();
        }

        return finalFloats;
    }

    public static float averageLongArray(ArrayList<Long> longArray) {
        if (longArray == null || longArray.size() == 0)
            return 0;

        float sum = 0;
        int count = 0;
        for (Long v : longArray) {
            sum += v;
            count ++;
        }

        return (sum * 1.0f / count);
    }

}
