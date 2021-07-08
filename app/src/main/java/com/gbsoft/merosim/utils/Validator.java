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

import android.text.TextUtils;

import com.gbsoft.merosim.data.Sim;

public class Validator {
    public static boolean isSecurityCodeValid(String securityCode) {
        return !(securityCode == null || TextUtils.isEmpty(securityCode));
    }

    public static boolean isPhoneNumberValid(String phoneNo) {
        if (phoneNo == null || TextUtils.isEmpty(phoneNo)) return false;
        return phoneNo.startsWith("98") && phoneNo.length() == 10;
    }

    public static boolean isAmountValid(String simName, String amount) {
        if (amount == null || TextUtils.isEmpty(amount)) return false;
        switch (simName) {
            case Sim.NAMASTE:
                int amountInt = Integer.parseInt(amount);
                return amountInt >= 10 && amountInt <= 500;
            case Sim.NCELL:
                return false;
            case Sim.SMART_CELL:
                return false;
        }
        return false;
    }
}
