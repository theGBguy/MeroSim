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

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentRechargeBinding;
import com.gbsoft.merosim.ui.recharge.steps.PinConfirmStep;
import com.gbsoft.merosim.ui.recharge.steps.PinScanStep;
import com.gbsoft.merosim.ui.recharge.steps.SimChooseStep;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.TelephonyUtils;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class RechargeFragment extends Fragment implements StepperFormListener {

    private FragmentRechargeBinding binding;
    private RechargeViewModel model;
    private TelephonyUtils telephonyUtils;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRechargeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(this).get(RechargeViewModel.class);

        telephonyUtils = new TelephonyUtils(requireContext());

        SimChooseStep simChooseStep = new SimChooseStep(model, getStepTitle(0), getNextButtonText(0));
        PinScanStep pinScanStep = new PinScanStep(model, getStepTitle(1), getNextButtonText(1), getViewLifecycleOwner());
        PinConfirmStep pinConfirmStep = new PinConfirmStep(model, getStepTitle(2), getNextButtonText(2));

        binding.stepperForm.setup(this, simChooseStep, pinScanStep, pinConfirmStep)
                .init();
    }

    private String getStepTitle(int stepPos) {
        switch (stepPos) {
            case 0:
                return requireContext().getString(R.string.choose_carrier_text);
            case 1:
                return requireContext().getString(R.string.scan_pin_text);
            case 2:
                return requireContext().getString(R.string.confirm_details_text);
            default:
                return "";
        }
    }

    private String getNextButtonText(int stepPos) {
        switch (stepPos) {
            case 0:
            case 1:
                return requireContext().getString(R.string.next_button_text);
            case 2:
                return requireContext().getString(R.string.recharge_button_text);
            default:
                return "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCompletedForm() {
        String ussdRequest = telephonyUtils.getRechargeUssdRequest(model.getSimChooseData(), model.getPinScanData());
        int slotIndex = telephonyUtils.getSimSlotIndex(model.getSimChooseData());

        if (PermissionUtils.isAllPermissionsGranted(requireContext()))
            telephonyUtils.sendUssdRequest(ussdRequest, TelephonyUtils.TYPE_NORMAL, slotIndex, callback);
        else {
            SnackUtils.showMessage(requireView(), R.string.permission_denied_text);
            binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
        }
    }

    private final UssdResponseCallback callback = new UssdResponseCallback() {
        @Override
        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
            if (response.toString().contains("already")
                    || response.toString().contains("exist"))
                binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
            SnackUtils.showMessage(requireView(), (response.toString()));
            super.onReceiveUssdResponse(telephonyManager, request, response);
        }

        @Override
        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
            SnackUtils.showMessage(requireView(), R.string.ussd_failed_snack_msg);
            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
        }
    };

    @Override
    public void onCancelledForm() {

    }
}