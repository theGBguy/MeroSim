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

package com.gbsoft.merosim.model;

// Model class to store Smartcell's service code
public class SmartCell extends Sim {
    public static final String USSD_SELF = "*134#";
    public static final String USSD_BALANCE = "*123#";
    public static final String USSD_RECHARGE = "*122*%s#";
    public static final String USSD_PACKS = "*141#";

    public static final String CUSTOMER_CARE_NO = "4242";
    public static final String USSD_BALANCE_TRANSFER = "*131*%s*%s*123456#";
    public static final String USSD_LOAN = "*129*40#";
    public static final String MCA = "4270";
    public static final String MCA_SUBSCRIBE = "SUB";
    public static final String MCA_UNSUBSCRIBE = "UNSUB";
    public static final String USSD_CRBT_SUB = "*171#";
    public static final String USSD_CRBT_UNSUB = "*171*6*3#";


    public SmartCell() {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, -1);
    }

    public SmartCell(int simSlotIndex) {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, simSlotIndex);
    }

    public SmartCell(String phoneNo, String balance, int simSlotIndex) {
        super(Sim.SMART_CELL, phoneNo, balance, Sim.UNAVAILABLE, simSlotIndex);
    }
}
