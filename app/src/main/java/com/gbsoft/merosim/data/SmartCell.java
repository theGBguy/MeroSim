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

public class SmartCell extends Sim {
    public static final String USSD_SELF = "*134#";
    public static final String USSD_BALANCE = "*123#";
    public static final String USSD_RECHARGE = "*122*%s#";

    public static final String CUSTOMER_CARE_NO = "4242";
    public static final String USSD_BALANCE_TRANSFER = "*131*%s*%s*123456#";

    public SmartCell() {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, -1);
    }

    public SmartCell(String phoneNo, String balance, int simSlotIndex) {
        super(Sim.SMART_CELL, phoneNo, balance, Sim.UNAVAILABLE, simSlotIndex);
    }
}
