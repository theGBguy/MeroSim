/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/23
 *
 * Licensed under GNU General Public License v3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsoft.merosim.data_source;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Utility class to access and store data in the shared preferences.
 */

public class PrefsUtils {
    public static final String KEY_USER_NAME = "user_name";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_PHONE = "sim%d_phone";
    private static final String KEY_BALANCE = "sim%d_balance";
    private static final String KEY_SIM_OWNER = "sim%d_sim_owner";
    private static final String UNAVAILABLE = "unavailable";
    private static final String KEY_SECURITY_CODE = "scode";
    public static final String NAME_UNKNOWN = "Stranger";

    public static String getUserName(Context context) {
        return getDefaultSharedPrefs(context).getString(KEY_USER_NAME, NAME_UNKNOWN);
    }

    public static void saveUserName(Context context, String userName) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(KEY_USER_NAME, userName)
                .apply();
    }

    public static void saveOperator(Context context, int slotIndex, String operator) {
        getDefaultSharedPrefs(context)
                .edit()
                .putString(getFormattedKey(KEY_OPERATOR, slotIndex), operator)
                .apply();
    }

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

    public static boolean isIntuitiveModeTurnedOn(Context context) {
        return getDefaultSharedPrefs(context).getBoolean("key_intuitive", true);
    }

    public static String getOperator(Context context, int slotIndex) {
        return getDefaultSharedPrefs(context).getString(getFormattedKey(KEY_OPERATOR, slotIndex), UNAVAILABLE);
    }

    public static String getPhoneNumber(Context context, int slotIndex) {
        return getDefaultSharedPrefs(context).getString(getFormattedKey(KEY_PHONE, slotIndex), UNAVAILABLE);
    }

    public static String getBalance(Context context, int slotIndex) {
        return getDefaultSharedPrefs(context).getString(getFormattedKey(KEY_BALANCE, slotIndex), UNAVAILABLE);
    }

    public static String getSimOwner(Context context, int slotIndex) {
        return getDefaultSharedPrefs(context).getString(getFormattedKey(KEY_SIM_OWNER, slotIndex), UNAVAILABLE);
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
