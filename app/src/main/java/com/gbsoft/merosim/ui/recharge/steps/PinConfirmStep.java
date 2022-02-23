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

package com.gbsoft.merosim.ui.recharge.steps;

import android.view.LayoutInflater;
import android.view.View;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.StepPinConfirmBinding;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.ui.recharge.RechargeViewModel;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.gbsoft.merosim.utils.SnackUtils;

import ernestoyaquello.com.verticalstepperform.Step;

// represents final step of the recharge process in the stepper view
public class PinConfirmStep extends Step<String> {
    private StepPinConfirmBinding binding;
    private final RechargeViewModel model;

    public PinConfirmStep(RechargeViewModel model, String title, String nextButtonText) {
        super(title, "", nextButtonText);
        this.model = model;
    }

    @Override
    public String getStepData() {
        return null;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return null;
    }

    @Override
    public void restoreStepData(String data) {
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return null;
    }

    @Override
    protected View createStepContentLayout() {
        binding = StepPinConfirmBinding.inflate(LayoutInflater.from(getContext()));
        return binding.getRoot();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        SnackUtils.showMessage(getContentLayout(), R.string.wrong_scanning_text);
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

            pinScanned = LocaleUtils.getNumberInNepaliDigit(pinScanned);
        }

        binding.tvConfirmSim.setText(getContext().getString(R.string.tv_confirm_sim, simChosen));
        binding.tietScannedPin.setText(pinScanned);
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