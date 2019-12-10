package com.example.macetestsample;

import android.util.Log;

import com.example.macetestsample.mace.AppModel;
import com.example.macetestsample.tools.InputDataLoader;

public class MaceMultiModelTestTask extends Thread {

    private final String TAG = "MultiModelTask";

    private AppModel resnet152IFrameCPUModel;
    private AppModel resnet152IFrameGPUModel;
    private AppModel resnet18MVCPUModel;
    private AppModel resnet18MVGPUModel;
    private AppModel resnet18ResidualCPUModel;
    private AppModel resnet18ResidualGPUModel;

    @Override
    public void run() {
        super.run();

        initModels();
        runTask();
    }

    private void initModels() {
        String deviceName;

        deviceName = "CPU";
        resnet152IFrameCPUModel  = AppModel.maceCreateModel("tf_resnet152.pb", "tf_resnet152.data", deviceName);
        resnet18MVCPUModel       = AppModel.maceCreateModel("tf_resnet18_mv.pb", "tf_resnet18_mv.data", deviceName);
        resnet18ResidualCPUModel = AppModel.maceCreateModel("tf_resnet18_residual.pb", "tf_resnet18_residual.data", deviceName);

        deviceName = "GPU";
        resnet152IFrameGPUModel  = AppModel.maceCreateModel("tf_resnet152.pb", "tf_resnet152.data", deviceName);
        resnet18MVGPUModel       = AppModel.maceCreateModel("tf_resnet18_mv.pb", "tf_resnet18_mv.data", deviceName);
        resnet18ResidualGPUModel = AppModel.maceCreateModel("tf_resnet18_residual.pb", "tf_resnet18_residual.data", deviceName);
    }

    private void runTask() {
        final int inputWidth  = 224;
        final int inputHeight = 224;

        float[] inputData, outputData;
        final int round = 10;

        for (int i = 0; i < round; i ++) {

            Log.i(TAG, String.format("===== Round %d =====", i));

            inputData = InputDataLoader.randomInputData(inputWidth, inputHeight, 3);
            outputData = resnet152IFrameCPUModel.maceClassify(inputData);
            outputData = resnet152IFrameGPUModel.maceClassify(inputData);

            inputData = InputDataLoader.randomInputData(inputWidth, inputHeight, 2);
            outputData = resnet18MVCPUModel.maceClassify(inputData);
            outputData = resnet18MVGPUModel.maceClassify(inputData);

            inputData = InputDataLoader.randomInputData(inputWidth, inputHeight, 3);
            outputData = resnet18ResidualCPUModel.maceClassify(inputData);
            outputData = resnet18ResidualGPUModel.maceClassify(inputData);
        }

    }

}
