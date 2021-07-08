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

package com.gbsoft.merosim.ui.recharge.steps;

import android.view.LayoutInflater;
import android.view.View;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.databinding.StepPinConfirmBinding;
import com.gbsoft.merosim.ui.recharge.RechargeViewModel;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.google.android.material.snackbar.Snackbar;

import ernestoyaquello.com.verticalstepperform.Step;

public class PinConfirmStep extends Step<Void> {
    private StepPinConfirmBinding binding;
    private final RechargeViewModel model;

    public PinConfirmStep(RechargeViewModel model, String title, String nextButtonText) {
        super(title, "", nextButtonText);
        this.model = model;
    }

    @Override
    public Void getStepData() {
        return null;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return null;
    }

    @Override
    public void restoreStepData(Void data) {
    }

    @Override
    protected IsDataValid isStepDataValid(Void stepData) {
        return null;
    }

    @Override
    protected View createStepContentLayout() {
        binding = StepPinConfirmBinding.inflate(LayoutInflater.from(getContext()));
        return binding.getRoot();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        Snackbar.make(getContentLayout(), getContext().getString(R.string.wrong_scanning_text),
                Snackbar.LENGTH_LONG).show();
        String simChosen = model.getSimChooseData();
        String pinScanned = model.getPinScanData();

        if (LocaleUtils.isNepali(getContext())) {
            switch (simChosen) {
                case Sim.NAMASTE:
                    simChosen = getContext().getString(R.string.sim_name_namaste);
                    break;
                case Sim.NCELL:
                    simChosen = getContext().getString(R.string.sim_name_ncell);
                    break;
                case Sim.SMART_CELL:
                    simChosen = getContext().getString(R.string.sim_name_smart);
                    break;
                default:
                    simChosen = getContext().getString(R.string.empty_sim_text);
                    break;
            }

            pinScanned = LocaleUtils.getPinCodeInNepaliDigit(pinScanned);
        }

        binding.tvConfirmSim.setText(getContext().getString(R.string.tv_confirm_sim, simChosen));
        binding.etScannedPin.setText(pinScanned);
    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }
}