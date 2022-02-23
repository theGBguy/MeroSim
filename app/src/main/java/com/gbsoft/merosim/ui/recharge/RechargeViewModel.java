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

package com.gbsoft.merosim.ui.recharge;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.data_source.Repository;
import com.gbsoft.merosim.data_source.Result;
import com.gbsoft.merosim.model.Sim;

import java.util.ArrayList;
import java.util.List;

/* View Model for Recharge Fragment */
public class RechargeViewModel extends AndroidViewModel {
    // variables for RechargeFragment
    private String simChooseData;
    private String pinScanData;

    // variables for PinScanStep
    private boolean isFlashEnabled;

    private final Repository repo;
    private final MutableLiveData<List<Sim>> liveSimList = new MutableLiveData<>();

    public RechargeViewModel(@NonNull Application app) {
        super(app);
        this.isFlashEnabled = false;
        this.simChooseData = Sim.NONE;
        this.pinScanData = "";
        this.repo = new Repository(
                ((MeroSimApp) app).getExecutor(),
                ((MeroSimApp) app).getMainThreadHandler()
        );
    }

    public String getSimChooseData() {
        return simChooseData;
    }

    public void setSimChooseData(String simChooseData) {
        this.simChooseData = simChooseData;
    }

    public String getPinScanData() {
        return pinScanData;
    }

    public void setPinScanData(String pinScanData) {
        this.pinScanData = pinScanData;
    }

    public boolean isFlashEnabled() {
        return isFlashEnabled;
    }

    public void setFlashEnabled(boolean flashEnabled) {
        isFlashEnabled = flashEnabled;
    }

    public String getRechargeUSSDRequest() {
        return repo.getRechargeUSSDRequest(simChooseData, pinScanData);
    }

    public int getSimSlotIndex() {
        return repo.getSimSlotIndex(getAppContext(), simChooseData);
    }

    public void querySimCardDetails() {
        repo.querySimCardDetails(getAppContext(), result -> {
            if (result instanceof Result.Success)
                liveSimList.setValue(((Result.Success<List<Sim>>) result).data);
            else
                liveSimList.setValue(new ArrayList<>());
        });
    }

    public LiveData<List<Sim>> getLiveSimList() {
        return liveSimList;
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}
