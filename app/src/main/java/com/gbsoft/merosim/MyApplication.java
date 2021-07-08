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
 * Last modified: 2021/05/31
 */

package com.gbsoft.merosim;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.yariksoffice.lingver.Lingver;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this, getLocale());
    }

    private String getLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNepaliOn = preferences.getBoolean(getApplicationContext().getString(R.string.key_language), false);
        return isNepaliOn ? "ne" : "en";
    }
}
