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
 * Last modified: 2021/10/28
 */

package com.gbsoft.merosim.intermediaries;

import android.content.Context;
import android.os.Handler;

import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.utils.TelephonyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Repository {
    private final ExecutorService executor;
    private final Handler mainThreadHandler;

    public Repository(ExecutorService executor, Handler mainThreadHandler) {
        this.executor = executor;
        this.mainThreadHandler = mainThreadHandler;
    }

    public void querySimCardDetails(Context context, RepoCallback<List<Sim>> callback) {
        executor.execute(() -> {
            List<Sim> simList = retrieveCachedSimDetails(context);
            mainThreadHandler.post(() -> callback.onComplete(new Result.Success<>(simList)));
        });
    }

    private List<Sim> retrieveCachedSimDetails(Context context) {
        TelephonyUtils utils = TelephonyUtils.getInstance(context);
        List<Sim> simList = new ArrayList<>();
        if (utils != null)
            simList = TelephonyUtils.getInstance(context).getSimList();
        for (Sim sim : simList) {
            List<String> simDetails = PrefsUtils.retrieveSimDetails(context, sim.getSimSlotIndex());
            sim.setPhoneNo(simDetails.get(0));
            sim.setBalance(simDetails.get(1));
            sim.setSimOwner(simDetails.get(2));
        }
        return simList;
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

    public String getRechargeUSSDRequest(String simChooseData, String pinScanData) {
        return TelephonyUtils.getRechargeUssdRequest(simChooseData, pinScanData);
    }

    public int getSimSlotIndex(Context context, String simName) {
        return TelephonyUtils.getInstance(context).getSimSlotIndex(simName);
    }
}
