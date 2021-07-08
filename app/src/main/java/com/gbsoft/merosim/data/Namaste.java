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

package com.gbsoft.merosim.data;

public class Namaste extends Sim {
    public static final String USSD_SELF = "*9#";
    public static final String USSD_BALANCE = "*400#";
    public static final String USSD_SIM_OWNER = "*922#";
    public static final String USSD_RECHARGE = "*412*%s#";
    public static final String USSD_BALANCE_TRANSFER = "*422*%s*%s*%s#";

    public static final String CUSTOMER_CARE_NO = "1498";
    public static final String NAMASTE_CREDIT_NO = "1477";

    public Namaste() {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, Sim.UNAVAILABLE, -1);
    }

    public Namaste(String phoneNo, String balance, String simOwner, int simSlotIndex) {
        super(Sim.NAMASTE, phoneNo, balance, simOwner, simSlotIndex);
    }
}
