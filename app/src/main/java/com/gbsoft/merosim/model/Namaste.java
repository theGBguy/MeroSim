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

// Model class to store Namaste's service code
public class Namaste extends Sim {
    public static final String USSD_SELF = "*9#";
    public static final String USSD_BALANCE = "*400#";
    public static final String USSD_SIM_OWNER = "*922#";
    public static final String USSD_RECHARGE = "*412*%s#";
    public static final String USSD_BALANCE_TRANSFER = "*422*%s*%s*%s#";
    public static final String USSD_PACKS = "*1415#";
    public static final String USSD_REMAINING_PACKS = "*1415*55#";

    public static final String START = "START";
    public static final String STATUS = "STATUS";
    public static final String STOP = "STOP";
    public static final String SUBSCRIBE_CRBT = "sub %s";
    public static final String UNSUBSCRIBE_CRBT = "unsub";

    public static final String CUSTOMER_CARE_NO = "1498";
    public static final String NAMASTE_CREDIT_NO = "1477";
    public static final String NAMASTE_FNF = "1415";
    // formatted args should be the own phone no
    public static final String FNF_SUB = "FNFSUB*%s";
    // formatted args should be old and new phone no respectively
    public static final String FNF_MODIFY = "FNFMOD*%s*%s";
    // formatted args should be phone no to delete from fnf service
    public static final String FNF_DELETE = "FNFDEL*%s";
    public static final String FNF_QUERY = "FNFINQ";

    //    public static final String NAMASTE_MCA = "1400";
    //    public static final String SUBSCRIBE_MCA = "sub mca";
    //    public static final String UNSUBSCRIBE_MCA = "unsub mca";
    public static final String SUBSCRIBE_MCA = "*1400*1#";
    public static final String UNSUBSCRIBE_MCA = "*1400*2#";
    public static final String STATUS_MCA = "*1400*3#";

    public static final String NAMASTE_CRBT = "1455";


    public Namaste() {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, Sim.UNAVAILABLE, -1);
    }

    public Namaste(int simSlotIndex) {
        this(Sim.UNAVAILABLE, Sim.UNAVAILABLE, Sim.UNAVAILABLE, simSlotIndex);
    }

    public Namaste(String phoneNo, String balance, String simOwner, int simSlotIndex) {
        super(Sim.NAMASTE, phoneNo, balance, simOwner, simSlotIndex);
    }
}
