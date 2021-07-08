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

package com.gbsoft.merosim.ui.recharge;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gbsoft.merosim.data.Sim;

public class RechargeViewModel extends AndroidViewModel {
    // variables for RechargeFragment
    private String simChooseData;
    private String pinScanData;

    // variables for PinScanStep
    private boolean isFlashEnabled;

    public RechargeViewModel(@NonNull Application application) {
        super(application);
        this.isFlashEnabled = false;
        this.simChooseData = Sim.NONE;
        this.pinScanData = "";
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
}
