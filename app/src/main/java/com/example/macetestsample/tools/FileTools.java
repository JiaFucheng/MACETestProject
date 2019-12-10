package com.example.macetestsample.tools;

import java.io.File;

public class FileTools {

    public static String combinePath(String p1, String p2) {
        String outPath = p1;

        if (!p1.endsWith(File.separator))
            outPath += File.separator;

        return outPath + p2;
    }

    public static String combinePath(String p1, String p2, String p3) {
        return combinePath(combinePath(p1, p2), p3);
    }

}
