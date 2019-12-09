// Copyright 2018 The MACE Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.macetestsample;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import com.example.macetestsample.result.InitData;
import com.example.macetestsample.result.LabelCache;
import com.example.macetestsample.result.ResultData;
import com.example.macetestsample.tools.MathFunction;
import com.xiaomi.mace.JniMaceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class AppModel {

    private static final String TAG = "AppModel";

    private boolean stopClassify = false;
    private boolean classifyFinished = false;
    private Handler mJniThread;
    public static AppModel instance = new AppModel();

    private AppModel() {
        HandlerThread thread = new HandlerThread("jniThread");
        thread.start();
        mJniThread = new Handler(thread.getLooper());
    }

    public void maceCreateGPUContext(final InitData initData) {
        mJniThread.post(new Runnable() {
            @Override
            public void run() {
                int result = JniMaceUtils.maceCreateGPUContext(
                        initData.getStoragePath());
                Log.i(TAG, "maceCreateGPUContext result is " + result);
            }
        });
    }

    public void maceCreateEngine(final InitData initData, final CreateEngineCallback callback) {
        mJniThread.post(new Runnable() {
            @Override
            public void run() {
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
                int result = JniMaceUtils.maceCreateEngine(
                        initData.getOmpNumThreads(), initData.getCpuAffinityPolicy(),
                        initData.getGpuPerfHint(), initData.getGpuPriorityHint(),
                        initData.getModel(), initData.getModelData(), initData.getDevice());
                Log.i(TAG, "maceCreateEngine result is " + result);

                // Handle result
                if (result == -1) {
                    stopClassify = true;
                    MaceApp.app.mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCreateEngineFail(InitData.DEVICES[0].equals(initData.getDevice()));
                        }
                    });
                } else {
                    stopClassify = false;
                }
            }
        });
    }

    private void maceClassifyInternal(final float[] input, final ArrayList<ResultData> results) {
        if (stopClassify) {
            Log.w(TAG, "Stop classify, something wrong");
            return;
        }
        //Log.i(TAG, "Start classify");

        long start = System.currentTimeMillis();
        float[] scores = JniMaceUtils.maceClassify(input);
        long costTime = System.currentTimeMillis() - start;

        /*****
        if (result != null) {
            final ResultData resultData = LabelCache.instance().getResultFirst(result);
            resultData.costTime = costTime;
        } else {
            Log.w(TAG, "Classify result is null");
        }
        *****/

        //Log.d(TAG, String.format(Locale.CHINA, "Result length %d", result.length));

        //int labelIndex = MathFunction.argmax(result);
        if (results != null)
            results.add(new ResultData(scores, costTime));

        Log.i(TAG, String.format(Locale.CHINA, "Classify time %d ms", costTime));
    }

    public void maceClassify(final float[] input, final ArrayList<ResultData> results, final boolean async) {
        classifyFinished = false;

        // Run in jni thread
        mJniThread.post(new Runnable() {
            @Override
            public void run() {
                maceClassifyInternal(input, results);
                classifyFinished = true;
            }
        });

        if (!async) {
            // Wait util classification finished
            while (!classifyFinished) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void maceShowLabel(final ArrayList<ResultData> results) {
        mJniThread.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<float[]> scoresArray = new ArrayList<>();
                for (ResultData result : results) {
                    scoresArray.add(result.getScores());
                }

                float[] finalResult = MathFunction.averageFloatsArray(scoresArray);
                int finalLabelIndex = MathFunction.argmax(finalResult);
                Log.i(TAG, String.format(Locale.CHINA, "Final label index %d", finalLabelIndex));
            }
        });
    }

    public void maceShowAvgCostTime(final ArrayList<ResultData> results) {
        mJniThread.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Long> costTimeArray = new ArrayList<>();
                for (ResultData result : results) {
                    costTimeArray.add(result.getCostTime());
                }

                float avgCostTime = MathFunction.averageLongArray(costTimeArray);
                Log.i(TAG, String.format(Locale.CHINA, "Average cost time %.2f ms", avgCostTime));
            }
        });
    }

    public interface CreateEngineCallback {
        void onCreateEngineFail(final boolean quit);
    }

}
