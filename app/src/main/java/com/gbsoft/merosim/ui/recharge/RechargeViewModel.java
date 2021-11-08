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

package com.gbsoft.merosim.ui.recharge;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.intermediaries.Repository;
import com.gbsoft.merosim.intermediaries.Result;

import java.util.ArrayList;
import java.util.List;

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
