/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/21
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

package com.gbsoft.merosim.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.model.Sim;

/*
 * This class contains utility methods related to language.
 */

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

    public static String getNumberInNepaliDigit(String engNumber) {
        if (TextUtils.equals(engNumber, Sim.UNAVAILABLE)) {
            return "उपलब्ध छैन";
        }
        char[] num = engNumber.toCharArray();
        StringBuilder nepaliNum = new StringBuilder();
        for (char c : num) {
            if (c == '.' || c == ' ') {
                nepaliNum.append(c);
                continue;
            }
            if (Character.isDigit(c)) {
                nepaliNum.append((char) ((int) c + 2358));
            }
        }
        return nepaliNum.toString();
    }

    public static String getBalanceInNepali(String balance) {
        if (TextUtils.equals(balance, Sim.UNAVAILABLE)) {
            return "उपलब्ध छैन";
        }
        return "रु" + getNumberInNepaliDigit(balance);
    }

}
