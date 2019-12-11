package com.example.macetestsample.mace;

import android.os.Environment;
import android.util.Log;

import com.example.macetestsample.tools.FileTools;
import com.xiaomi.mace.JniMaceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class AppModel {

    private static final String TAG = "AppModel";
    private final static String MODEL_PATH = "mace_workspace/models";

    private String mModelName;
    private String mDeviceName;

    private boolean stopClassify = false;

    private int mModelIndex;
    private static AppModelCreateEngineCallback mCallback = new AppModelCreateEngineCallback();

    public void setModelName(String name) {
        this.mModelName = name;
    }

    public void setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
    }

    public static void maceCreateGPUContext() {
        maceCreateGPUContext(new InitData());
    }

    public static void maceCreateGPUContext(final InitData initData) {
        File file = new File(initData.getStoragePath());
        if (!file.exists())
            file.mkdirs();
        Log.i(TAG, "maceCreateGPUContext storage path " + initData.getStoragePath());

        int result = JniMaceUtils.maceCreateGPUContext(initData.getStoragePath());
        Log.i(TAG, "maceCreateGPUContext result is " + result);
    }

    public static AppModel maceCreateModel(String modelFileName,
                                           String modelDataName,
                                           String device) {
        InitData initData = new InitData();

        final String externalStorageDirectory =
                Environment.getExternalStorageDirectory().getAbsolutePath();
        final String modelFilePath = FileTools.combinePath(
                externalStorageDirectory, MODEL_PATH, modelFileName);
        final String modelDataPath = FileTools.combinePath(
                externalStorageDirectory, MODEL_PATH, modelDataName);

        initData.setModel(modelFilePath);
        initData.setModelData(modelDataPath);
        initData.setDevice(device);

        AppModel model = new AppModel();
        model.setModelName(modelFileName);
        model.setDeviceName(device);
        model.maceCreateEngine(initData, mCallback);

        return model;
    }

    private void maceCreateEngine(final InitData initData,
                                  final CreateEngineCallback callback) {
        // Check if model and data file exists
        if (!new File(initData.getModel()).exists()) {
            Log.w(TAG, String.format(Locale.CHINA, "Model file does not exist (path is %s)",
                    initData.getModel()));
            return;
        }

        if (!new File(initData.getModelData()).exists()) {
            Log.w(TAG, String.format(Locale.CHINA, "Model data file does not exist (path is %s)",
                    initData.getModelData()));
            return;
        }

        // Create engine
        long start = System.currentTimeMillis();
        int result = JniMaceUtils.maceCreateEngine(
                initData.getOmpNumThreads(), initData.getCpuAffinityPolicy(),
                initData.getGpuPerfHint(), initData.getGpuPriorityHint(),
                initData.getModel(), initData.getModelData(), initData.getDevice());
        long costTime = System.currentTimeMillis() - start;
        Log.i(TAG, "maceCreateEngine result is " + result);

        // Handle result
        if (result == -1) {
            stopClassify = true;
            callback.onCreateEngineFail(
                    InitData.DEVICES[0].equals(initData.getDevice()));
        } else {
            Log.i(TAG, "maceCreateEngine cost time " + costTime + " ms");
            stopClassify = false;
            mModelIndex = result - 1;
        }
    }

    private float[] maceClassifyInternal(final float[] input,
                                         final ArrayList<ResultData> results) {
        if (stopClassify) {
            Log.w(TAG, "Stop classify, something wrong");
            return null;
        }

        long start = System.currentTimeMillis();
        float[] scores = JniMaceUtils.maceModelClassify(mModelIndex, input);
        long costTime = System.currentTimeMillis() - start;

        if (scores == null) {
            return null;
        }

        if (results != null)
            results.add(new ResultData(scores, costTime));

        Log.i(TAG, String.format(Locale.CHINA, "Classify time %d ms (%s, %s)",
                                 costTime, mModelName, mDeviceName));

        return scores;
    }

    public float[] maceClassify(final float[] input, final ArrayList<ResultData> results) {
        return maceClassifyInternal(input, results);
    }

    public float[] maceClassify(final float[] input) {
        return maceClassify(input, null);
    }

    public interface CreateEngineCallback {
        void onCreateEngineFail(final boolean quit);
    }

    private static class AppModelCreateEngineCallback implements CreateEngineCallback {
        @Override
        public void onCreateEngineFail(boolean quit) {
            Log.e(TAG, "Create mace failed");
        }
    }

}
