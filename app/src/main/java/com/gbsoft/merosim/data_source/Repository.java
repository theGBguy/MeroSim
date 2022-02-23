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

package com.gbsoft.merosim.data_source;

import android.content.Context;
import android.os.Handler;

import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.telephony.TelephonyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/*
 * Nearly single source of truth for the data in this app. Some classes respect
 * that principle and call the repo's method meanwhile some directly call the
 * utils methods for convenience. Initially, author was inspired to create this
 * class as he was learning MVVM but then later on, the author find no real
 * advantage of using this class as most of the methods here just relay the
 * method calls and do nothing.
 */

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

    public String getUserName(Context context) {
        return PrefsUtils.getUserName(context);
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

    public boolean shouldUseOverlay(Context context) {
        return PrefsUtils.shouldUseOverlay(context);
    }

}
