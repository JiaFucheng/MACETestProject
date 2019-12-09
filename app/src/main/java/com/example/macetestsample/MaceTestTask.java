package com.example.macetestsample;

import android.os.Environment;
import android.util.Log;

import com.example.macetestsample.result.ResultData;
import com.example.macetestsample.tools.InputDataList;
import com.example.macetestsample.tools.InputDataLoader;
import com.example.macetestsample.tools.Representation;

import java.util.ArrayList;
import java.util.Locale;

public class MaceTestTask extends Thread {

    private int mRepresentation;
    private final String TAG = "MaceTestTask";

    public MaceTestTask(int representation) {
        this.mRepresentation = representation;
    }

    public void setRepresentation(int representation) {
        this.mRepresentation = representation;
    }

    private void warmUpRun() {
        float[] inputData;
        if (mRepresentation == Representation.IFRAME) {
            final String inputDataFilenameFull =
                    String.format(Locale.CHINA, "%s/%s/%s",
                            Environment.getExternalStorageDirectory().getAbsolutePath(),
                            InputDataList.INPUT_DATA_PATH,
                            InputDataList.INPUT_DATA_FILENAMES_I_FRAME[0]);
            inputData = InputDataLoader.loadInputData(inputDataFilenameFull, 224, 224, mRepresentation);
            if (inputData == null) {
                Log.w(TAG, "Not found input data, use random input data");
                inputData = InputDataLoader.randomInputData(224, 224, mRepresentation);
            }
        } else {
            inputData = InputDataLoader.randomInputData(224, 224, mRepresentation);
        }

        final boolean async = false;
        AppModel.instance.maceClassify(inputData, null, async);
    }

    private void task1() {
        // Warm up
        warmUpRun();
        Log.i(TAG, "Warm up finished");

        // Load input data from file
        final String[] inputDataFilenames = InputDataList.INPUT_DATA_FILENAMES_I_FRAME;

        ArrayList<ResultData> results = new ArrayList<>();

        final boolean async = false;
        for (String inputDataFilename : inputDataFilenames) {
            final String inputDataFilenameFull =
                    String.format(Locale.CHINA, "%s/%s/%s",
                            Environment.getExternalStorageDirectory().getAbsolutePath(),
                            InputDataList.INPUT_DATA_PATH,
                            inputDataFilename);
            float[] inputData = InputDataLoader.loadInputData(inputDataFilenameFull, 224, 224, mRepresentation);
            if (inputData == null) {
                Log.w(TAG, "Not found input data, use random input data");
                inputData = InputDataLoader.randomInputData(224, 224, mRepresentation);
            }

            // Run classify
            AppModel.instance.maceClassify(inputData, results, async);
        }

        // Show final label
        AppModel.instance.maceShowLabel(results);
    }

    private void task2() {
        int round = 5;

        // Warm up
        warmUpRun();
        Log.i(TAG, "Warm up finished");

        float[] inputData;
        if (mRepresentation == Representation.IFRAME) {
            final String inputDataFilename = InputDataList.INPUT_DATA_FILENAMES_I_FRAME[0];
            inputData = InputDataLoader.loadInputData(
                            String.format(Locale.CHINA, "%s/%s/%s",
                                          Environment.getExternalStorageDirectory().getAbsolutePath(),
                                          InputDataList.INPUT_DATA_PATH,
                                          inputDataFilename),
                            224, 224, mRepresentation);
            if (inputData == null) {
                Log.w(TAG, "Not found input data, use random input data");
                inputData = InputDataLoader.randomInputData(224, 224, mRepresentation);
            }
        } else {
            inputData = InputDataLoader.randomInputData(224, 224, mRepresentation);
        }

        ArrayList<ResultData> results = new ArrayList<>();

        final boolean async = false;
        for (int i = 0; i < round; i ++) {
            // Run classify
            AppModel.instance.maceClassify(inputData, results, async);
        }

        // Show average delay
        AppModel.instance.maceShowAvgCostTime(results);
        // Show final label
        AppModel.instance.maceShowLabel(results);
    }

    @Override
    public void run() {
        super.run();

        try {
            final int countDownSec = 1;

            Log.i(TAG, String.format(Locale.CHINA, "Task will start in %ds", countDownSec));
            Thread.sleep(countDownSec * 1000);

            //task1();
            task2();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
