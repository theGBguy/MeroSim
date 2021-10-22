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

package com.gbsoft.merosim.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.gbsoft.merosim.R;

public class LocaleUtils {

    public static String getLocale(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean isNepaliOn = preferences.getBoolean(context.getApplicationContext().getString(R.string.key_language), false);
        return isNepaliOn ? "ne" : "en";
    }

    public static boolean isNepali(Context context) {
        String lang;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            lang = context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        else
            lang = context.getResources().getConfiguration().locale.getLanguage();

        return lang.equals("ne");
    }

    public static String getPinCodeInNepaliDigit(String pinCode) {
        char[] pinArr = pinCode.toCharArray();
        StringBuilder pin = new StringBuilder();
        for (char c : pinArr) {
            pin.append((char) ((int) c + 2358));
        }
        return pin.toString();
    }
}
