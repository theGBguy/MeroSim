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

package com.gbsoft.merosim.ui.recharge.steps;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.lifecycle.Observer;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.databinding.StepSimChooseBinding;
import com.gbsoft.merosim.ui.recharge.RechargeViewModel;

import java.util.List;

import ernestoyaquello.com.verticalstepperform.Step;

public class SimChooseStep extends Step<String> implements RadioGroup.OnCheckedChangeListener {
    private StepSimChooseBinding binding;
    private final RechargeViewModel model;

    public SimChooseStep(RechargeViewModel model, String title, String nextButtonText) {
        super(title, "", nextButtonText);
        this.model = model;
    }

    @Override
    public String getStepData() {
        return model.getSimChooseData();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String carrierName = getStepData();
        switch (carrierName) {
            case Sim.NAMASTE:
                return getContext().getString(R.string.sim_name_namaste);
            case Sim.NCELL:
                return getContext().getString(R.string.sim_name_ncell);
            case Sim.SMART_CELL:
                return getContext().getString(R.string.sim_name_smart);
            default:
                return getContext().getString(R.string.empty_sim_text);
        }
    }

    @Override
    public void restoreStepData(String data) {
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        boolean isDataValid = !stepData.equals(Sim.NONE);
        return new IsDataValid(isDataValid, null);
    }

    @Override
    protected View createStepContentLayout() {
        binding = StepSimChooseBinding.inflate(LayoutInflater.from(getContext()));
        binding.radioGroup.setOnCheckedChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rdo_btn_ntc)
            model.setSimChooseData(Sim.NAMASTE);
        else if (checkedId == R.id.rdo_btn_ncell)
            model.setSimChooseData(Sim.NCELL);
        else if (checkedId == R.id.rdo_btn_smartcell)
            model.setSimChooseData(Sim.SMART_CELL);
        markAsCompleted(true);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        model.querySimCardDetails();
        model.getLiveSimList().observeForever(new Observer<List<Sim>>() {
            @Override
            public void onChanged(List<Sim> sims) {
                model.getLiveSimList().removeObserver(this);

                String currentSim = model.getSimChooseData();

                for (Sim sim : sims) {
                    String name = sim.getName();
                    switch (name) {
                        case Sim.NAMASTE:
                            binding.rdoBtnNtc.setEnabled(true);
                            if (TextUtils.equals(name, currentSim))
                                binding.rdoBtnNtc.setChecked(true);
                            break;
                        case Sim.NCELL:
                            binding.rdoBtnNcell.setEnabled(true);
                            if (TextUtils.equals(name, currentSim))
                                binding.rdoBtnNcell.setChecked(true);
                            break;
                        case Sim.SMART_CELL:
                            binding.rdoBtnSmartcell.setEnabled(true);
                            if (TextUtils.equals(name, currentSim))
                                binding.rdoBtnSmartcell.setChecked(true);
                            break;
                    }
                }
            }
        });
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
