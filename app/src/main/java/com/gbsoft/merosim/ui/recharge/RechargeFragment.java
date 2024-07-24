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

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentRechargeBinding;
import com.gbsoft.merosim.telephony.TelephonyUtils;
import com.gbsoft.merosim.telephony.USSDResponseCallback;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.ui.recharge.steps.PinConfirmStep;
import com.gbsoft.merosim.ui.recharge.steps.PinScanStep;
import com.gbsoft.merosim.ui.recharge.steps.SimChooseStep;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

// This fragment displays a stepper view to assist users
// in the balance recharge process.
public class RechargeFragment extends Fragment implements StepperFormListener {
    private FragmentRechargeBinding binding;
    private RechargeViewModel model;
    private String currentPermission;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (currentPermission.equals(Manifest.permission.CAMERA)) {
                        binding.viewSwitcherRecharge.reset();
                        binding.viewSwitcherRecharge.showNext();
                    }
                } else {
                    SnackUtils.showMessage(requireView(), R.string.perm_not_granted_txt);
                }
            });

    private final PermissionFixerContract fixerContract = permission -> {
        currentPermission = permission;
        handlePermission(Manifest.permission.CALL_PHONE, getString(R.string.perm_call_phone_msg), permissionLauncher);
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRechargeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(this).get(RechargeViewModel.class);

        // create instances of all the steps for the stepper view
        SimChooseStep simChooseStep = new SimChooseStep(
                model,
                requireContext().getString(R.string.choose_carrier_text),
                requireContext().getString(R.string.next_button_text)
        );
        PinScanStep pinScanStep = new PinScanStep(
                model,
                requireContext().getString(R.string.scan_pin_text),
                requireContext().getString(R.string.next_button_text),
                getViewLifecycleOwner(),
                ((MeroSimApp) requireActivity().getApplication()).getExecutor()
        );
        PinConfirmStep pinConfirmStep = new PinConfirmStep(
                model,
                requireContext().getString(R.string.confirm_details_text),
                requireContext().getString(R.string.recharge_button_text)
        );

        // initializes the stepper view
        binding.stepperForm.setup(this, simChooseStep, pinScanStep, pinConfirmStep)
                .init();

        // loads the recharger banner ads
        binding.adViewRecharge.loadAd(new AdRequest.Builder().build());

        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.CAMERA)) {
            binding.viewSwitcherRecharge.showNext();
        }

        binding.btnGrantCamPerm.setOnClickListener(v -> {
            // request camera related permission which is essential to
            // perform text scan
            handlePermission(Manifest.permission.CAMERA, getString(R.string.perm_camera_msg), permissionLauncher);
        });

    }

    private void handlePermission(String permission, String message, ActivityResultLauncher<String> launcher) {
        if (!PermissionUtils.isPermissionGranted(requireContext(), permission)) {
            if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.dialog_permission_title))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.dialog_positive_btn_txt), (dialog, which) -> {
                            launcher.launch(permission);
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.dialog_negative_btn_txt), (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                currentPermission = permission;
                launcher.launch(permission);
            }
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onCompletedForm() {
        // initiates the USSD request for recharge when form is completed
        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.CALL_PHONE)) {
            TelephonyUtils.getInstance(requireContext()).sendUssdRequest(
                    model.getRechargeUSSDRequest(),
                    true,
                    TelephonyUtils.TYPE_NORMAL,
                    model.getSimSlotIndex(),
                    callback,
                    fixerContract
            );
        } else {
            binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
            handlePermission(Manifest.permission.CALL_PHONE, getString(R.string.perm_call_phone_msg), permissionLauncher);
        }
    }

    // callback to handle USSD request's response
    private final USSDResponseCallback callback = new USSDResponseCallback() {
        @Override
        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
            String responseStr = response.toString();
            if (responseStr.contains("already") || responseStr.contains("exist"))
                binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
            SnackUtils.showMessage(requireView(), response.toString());
            super.onReceiveUssdResponse(telephonyManager, request, response);
        }

        @Override
        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
//            SnackUtils.showMessage(requireView(), R.string.ussd_response_failed_lack_permission);
            binding.stepperForm.cancelFormCompletionOrCancellationAttempt();
            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
        }
    };

    @Override
    public void onCancelledForm() {
    }

    @Override
    public void onStepAdded(int index, Step<?> addedStep) {
    }

    @Override
    public void onStepRemoved(int index) {
    }
}