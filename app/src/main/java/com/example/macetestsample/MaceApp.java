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

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.macetestsample.result.LabelCache;

public class MaceApp extends Application {

    public static MaceApp app;
    public Handler mBackground;
    public Handler mMainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        mMainHandler = new Handler(getMainLooper());

        HandlerThread thread = new HandlerThread("mace_app");
        thread.start();
        mBackground = new Handler(thread.getLooper());
        //mBackground.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        LabelCache.instance();
        //    }
        //});
    }

    public Handler mainHandler() {
        return mMainHandler;
    }

}
