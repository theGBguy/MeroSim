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

import android.Manifest;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentRechargeBinding;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.ui.recharge.steps.PinConfirmStep;
import com.gbsoft.merosim.ui.recharge.steps.PinScanStep;
import com.gbsoft.merosim.ui.recharge.steps.SimChooseStep;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.TelephonyUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class RechargeFragment extends Fragment implements StepperFormListener {

    private FragmentRechargeBinding binding;
    private RechargeViewModel model;
    private TelephonyUtils telephonyUtils;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    SnackUtils.showMessage(requireView(), R.string.permission_granted_txt, "Camera");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_camera_msg);
            });

    private final ActivityResultLauncher<String> callPhonePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    SnackUtils.showMessage(requireView(), R.string.permission_granted_txt, "Call phone");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_call_phone_msg);
            });

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

        SimChooseStep simChooseStep = new SimChooseStep(
                model,
                requireContext().getString(R.string.choose_carrier_text),
                requireContext().getString(R.string.next_button_text)
        );

        PinScanStep pinScanStep = new PinScanStep(
                model,
                requireContext().getString(R.string.scan_pin_text),
                requireContext().getString(R.string.next_button_text),
                getViewLifecycleOwner()
        );

        PinConfirmStep pinConfirmStep = new PinConfirmStep(
                model,
                requireContext().getString(R.string.confirm_details_text),
                requireContext().getString(R.string.recharge_button_text)
        );

        binding.stepperForm.setup(this, simChooseStep, pinScanStep, pinConfirmStep)
                .init();

        handlePermission(Manifest.permission.CAMERA, getString(R.string.perm_camera_msg), cameraPermissionLauncher);
    }

    private void handlePermission(String permission, String message, ActivityResultLauncher<String> launcher) {
        if (!PermissionUtils.isPermissionGranted(requireContext(), permission)) {
            if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.perm_dialog_title))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.positive_dialog_btn_txt), (dialog, which) -> {
                            launcher.launch(permission);
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.negative_dialog_btn_txt), (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else {
                launcher.launch(permission);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final PermissionFixerContract fixerContract = permission ->
            handlePermission(Manifest.permission.CALL_PHONE, getString(R.string.perm_camera_msg), callPhonePermissionLauncher);

    @Override
    public void onCompletedForm() {
        String ussdRequest = telephonyUtils.getRechargeUssdRequest(model.getSimChooseData(), model.getPinScanData());
        int slotIndex = telephonyUtils.getSimSlotIndex(model.getSimChooseData());

        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.CALL_PHONE)) {
            telephonyUtils.sendUssdRequestWithOverlay(ussdRequest, TelephonyUtils.TYPE_NORMAL, slotIndex, callback, fixerContract);
        } else {
            binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
            handlePermission(Manifest.permission.CALL_PHONE, getString(R.string.perm_camera_msg), callPhonePermissionLauncher);
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