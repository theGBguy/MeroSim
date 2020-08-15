package com.gbsoft.scanrecharge.ui.recharge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gbsoft.scanrecharge.R;
import com.gbsoft.scanrecharge.databinding.FragmentRechargeBinding;
import com.gbsoft.scanrecharge.ui.recharge.steps.PinConfirmStep;
import com.gbsoft.scanrecharge.ui.recharge.steps.PinScanStep;
import com.gbsoft.scanrecharge.ui.recharge.steps.SimChooseStep;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class RechargeFragment extends Fragment implements StepperFormListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private FragmentRechargeBinding binding;
    private RechargeViewModel model;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRechargeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(this).get(RechargeViewModel.class);
        model.setLifecycleOwner(this);

        SimChooseStep simChooseStep = new SimChooseStep(model);
        PinScanStep pinScanStep = new PinScanStep(model);
        PinConfirmStep pinConfirmStep = new PinConfirmStep(model);

        binding.stepperForm.setup(this, simChooseStep, pinScanStep, pinConfirmStep)
                .init();

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        String[] requiredPermissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE};
        boolean isAllGranted = true;
        for (String permission : requiredPermissions) {
            isAllGranted = isAllGranted && ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
        }
        if (!isAllGranted) {
            ActivityCompat.requestPermissions(requireActivity(), requiredPermissions, RechargeViewModel.PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RechargeViewModel.PERMISSIONS_REQUEST_CODE) {
            boolean showSnack = true;
            for (int result : grantResults) {
                showSnack = showSnack && result == PackageManager.PERMISSION_DENIED;
            }
            if (showSnack) {
                Snackbar.make(requireView(), getString(R.string.permission_denied_text), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.shutDownCameraService();
    }

    @Override
    public void onCompletedForm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) requireContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                List<String> carrierNames = new ArrayList<>();
                int subsCount = subscriptionManager.getActiveSubscriptionInfoCount();
                for (int i = 0; i < subsCount; i++) {
                    carrierNames.add(subsInfoList.get(i).getCarrierName().toString());
                }
            } else
                Snackbar.make(requireView(), getString(R.string.permission_denied_text), Snackbar.LENGTH_LONG).show();
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = telephonyManager.getNetworkOperatorName();
        }
    }

    private void sendUssdCode(){
    }

    @Override
    public void onCancelledForm() {

    }
}