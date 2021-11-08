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
 * Last modified: 2021/10/22
 */

package com.gbsoft.merosim.utils;

import android.text.TextUtils;

import com.gbsoft.merosim.data.Sim;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isSecurityCodeValid(String securityCode) {
        return !TextUtils.isEmpty(securityCode);
    }

    public static boolean isPhoneNumberValid(String simName, String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) return false;

        String pattern = "";
        switch (simName) {
            case Sim.NAMASTE:
                pattern = "98[456][0-9]{7}";
                break;
            case Sim.NCELL:
                pattern = "98[0-2][0-9]{7}";
                break;
            case Sim.SMART_CELL:
                pattern = "9[68][128][0-9]{7}";
                break;
        }

        Pattern phoneNoPattern = Pattern.compile(pattern);
        Matcher phoneNoMatcher = phoneNoPattern.matcher(phoneNo);

        return phoneNoMatcher.find();
    }

    public static boolean arePhoneNumbersValid(String simName, String... phoneNo) {
        for (String phone : phoneNo) {
            if (TextUtils.isEmpty(phone)) return false;
        }

        String pattern = "";
        switch (simName) {
            case Sim.NAMASTE:
                pattern = "98[456][0-9]{7}";
                break;
            case Sim.NCELL:
                pattern = "98[0-2][0-9]{7}";
                break;
            case Sim.SMART_CELL:
                pattern = "9[68][128][0-9]{7}";
                break;
        }

        Pattern phoneNoPattern = Pattern.compile(pattern);
        boolean isValid = true;

        for (String phone : phoneNo) {
            Matcher phoneMatcher = phoneNoPattern.matcher(phone);
            isValid = isValid && phoneMatcher.find();
        }

        return isValid;
    }

    public static boolean isAmountValid(String simName, String amount) {
        if (TextUtils.isEmpty(amount)) return false;

        int amountInt = Integer.parseInt(amount);
        switch (simName) {
            case Sim.NAMASTE:
                return amountInt >= 10 && amountInt <= 500;
            case Sim.NCELL:
                return amountInt >= 10 && amountInt <= 200;
            case Sim.SMART_CELL:
                return false;
        }
        return false;
    }
}
