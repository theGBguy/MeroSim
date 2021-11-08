/*
 * Copyright 2021 Chiranjeevi Pandey Some rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified: 2021/10/28
 */

package com.gbsoft.merosim;

import android.app.Application;
import android.os.Handler;

import androidx.core.os.HandlerCompat;

import com.gbsoft.merosim.utils.LocaleUtils;
import com.yariksoffice.lingver.Lingver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeroSimApp extends Application {
    private ExecutorService executor;
    private Handler mainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this, LocaleUtils.getLocale(getApplicationContext()));
    }

    public ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newFixedThreadPool(4);
        return executor;
    }

    public Handler getMainThreadHandler() {
        if (mainThreadHandler == null)
            mainThreadHandler = HandlerCompat.createAsync(getMainLooper());
        return mainThreadHandler;
    }
}
