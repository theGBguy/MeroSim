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

// Model class to store Ncell's service code
public class Ncell extends Sim {
    public static final String USSD_SELF = "*903#";
    public static final String USSD_BALANCE = "*901#";
    public static final String USSD_RECHARGE = "*902*%s#";
    public static final String USSD_SIM_OWNER = "*9966#";

    public static final String USSD_DATA = "*17123#";
    public static final String USSD_VOICE = "*17118#";
    public static final String USSD_SMS = "*17119#";

    public static final String CUSTOMER_CARE_NO = "9005";
    public static final String USSD_BALANCE_TRANSFER = "*17122*%s*%s#";
    public static final String USSD_SAPATI = "*9988#";
    public static final String USSD_MY5 = "*5599%s#";
    public static final String USSD_MCN_ACTIVATE = "*100*2*2*1*1#";
    public static final String USSD_MCN_DEACTIVATE = "*100*2*2*1*2#";
    public static final String USSD_PRBT = "17117";
    public static final String LOW_BALANCE_CALL = "17102%s";

    public Ncell() {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, Sim.UNAVAILABLE, -1);
    }

    public Ncell(int simSlotIndex) {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, Sim.UNAVAILABLE, simSlotIndex);
    }

    public Ncell(String phoneNo, String balance, String simOwner, int simSlotIndex) {
        super(Sim.NCELL, phoneNo, balance, simOwner, simSlotIndex);
    }
}
