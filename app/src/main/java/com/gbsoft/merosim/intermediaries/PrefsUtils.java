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

package com.gbsoft.merosim.intermediaries;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrefsUtils {
    public static final String KEY_PHONE = "sim%d_phone";
    public static final String KEY_BALANCE = "sim%d_balance";
    public static final String KEY_SIM_OWNER = "sim%d_sim_owner";
    public static final String UNAVAILABLE = "(unavailable)";
    public static final String KEY_SECURITY_CODE = "scode";

    public static void savePhone(Context context, int slotIndex, String phone) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(getFormattedKey(KEY_PHONE, slotIndex), phone)
                .apply();
    }

    public static void saveBalance(Context context, int slotIndex, String balance) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(getFormattedKey(KEY_BALANCE, slotIndex), balance)
                .apply();
    }

    public static void saveSimOwner(Context context, int slotIndex, String simOwner) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(getFormattedKey(KEY_SIM_OWNER, slotIndex), simOwner)
                .apply();
    }

    public static void saveSecurityCode(Context context, String securityCode) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(KEY_SECURITY_CODE, securityCode)
                .apply();
    }

    public static String getSecurityCode(Context context) {
        return getDefaultSharedPrefs(context).getString(KEY_SECURITY_CODE, "");
    }


    public static List<String> retrieveSimDetails(Context context, int slotIndex) {
        List<String> details = new ArrayList<>();
        SharedPreferences sharedPreferences = getDefaultSharedPrefs(context);
        details.add(sharedPreferences.getString(getFormattedKey(KEY_PHONE, slotIndex), UNAVAILABLE));
        details.add(sharedPreferences.getString(getFormattedKey(KEY_BALANCE, slotIndex), UNAVAILABLE));
        details.add(sharedPreferences.getString(getFormattedKey(KEY_SIM_OWNER, slotIndex), UNAVAILABLE));
        return details;
    }

    public static SharedPreferences getDefaultSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static String getFormattedKey(String key, int slotIndex) {
        return String.format(Locale.getDefault(), key, slotIndex);
    }
}
