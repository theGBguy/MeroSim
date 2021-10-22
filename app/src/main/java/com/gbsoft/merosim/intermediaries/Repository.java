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

import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.utils.TelephonyUtils;

import java.util.List;

public class Repository {
    public List<Sim> getSimList(Context context) {
        return new TelephonyUtils(context).getSimList();
    }

    public List<String> retrieveSimDetails(Context context, int slotIndex) {
        return PrefsUtils.retrieveSimDetails(context, slotIndex);
    }

    public void savePhone(Context context, int slotIndex, String phone) {
        PrefsUtils.savePhone(context, slotIndex, phone);
    }

    public void saveBalance(Context context, int slotIndex, String balance) {
        PrefsUtils.saveBalance(context, slotIndex, balance);

    }

    public void saveSimOwner(Context context, int slotIndex, String simOwner) {
        PrefsUtils.saveSimOwner(context, slotIndex, simOwner);
    }

    public void saveSecurityCode(Context context, String securityCode) {
        PrefsUtils.saveSecurityCode(context, securityCode);
    }

    public String getSecurityCode(Context context) {
        return PrefsUtils.getSecurityCode(context);
    }
}
