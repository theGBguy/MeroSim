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

import android.Manifest;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.StepPinScanBinding;
import com.gbsoft.merosim.ui.home.SimRecyclerAdapter;
import com.gbsoft.merosim.ui.recharge.OnTextRecognizedListener;
import com.gbsoft.merosim.ui.recharge.PinAnalyzer;
import com.gbsoft.merosim.ui.recharge.RechargeViewModel;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ernestoyaquello.com.verticalstepperform.Step;

public class PinScanStep extends Step<String> implements OnTextRecognizedListener, View.OnClickListener, View.OnTouchListener {
    private StepPinScanBinding binding;
    private final RechargeViewModel model;
    private Camera camera;
    private final LifecycleOwner lifecycleOwner;
    private static final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();

    public PinScanStep(RechargeViewModel model, String title, String nextButtonText, LifecycleOwner lifecycleOwner) {
        super(title, "", nextButtonText);
        this.model = model;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public String getStepData() {
        return model.getPinScanData();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String pinCode = getStepData();
        if (LocaleUtils.isNepali(getContext()))
            pinCode = LocaleUtils.getPinCodeInNepaliDigit(pinCode);
        return pinCode.isEmpty() ? getContext().getString(R.string.empty_pin_text) : pinCode;
    }

    @Override
    public void restoreStepData(String data) {
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(stepData.length() == 16, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected View createStepContentLayout() {
        binding = StepPinScanBinding.inflate(LayoutInflater.from(getContext()));
        binding.btnToggleFlash.setOnClickListener(this);
        binding.btnRetry.setOnClickListener(this);
        binding.preview.setOnTouchListener(PinScanStep.this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_toggle_flash) {
            toggleFlash(true);
        } else if (v.getId() == R.id.btn_retry) {
            initStep();
        }
    }

    private void toggleFlash(boolean withMsg) {
        boolean toggled = !model.isFlashEnabled();
        toggleIconAndText(toggled);
        if (camera.getCameraInfo().hasFlashUnit()) {
            camera.getCameraControl().enableTorch(toggled);
        } else {
            if (withMsg)
                SnackUtils.showMessage(getEntireStepLayout(), R.string.no_flash_text);
        }
    }

    private void initStep() {
        startStopCamera(true);
        toggleIconAndText(false);
        binding.groupPin.setVisibility(View.GONE);
        binding.groupPreview.setVisibility(View.VISIBLE);
        model.setPinScanData("");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof PreviewView) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:
                    MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(
                            (float) binding.preview.getWidth(), (float) binding.preview.getHeight()
                    );
                    MeteringPoint autoFocusPoint = factory.createPoint(event.getX(), event.getY());
                    camera.getCameraControl().startFocusAndMetering(
                            new FocusMeteringAction.Builder(
                                    autoFocusPoint, FocusMeteringAction.FLAG_AF
                            ).build()
                    );
                    return true;
                default:
                    return false;
            }
        }
        return true;
    }

    private void startStopCamera(boolean start) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(start ? binding.preview.getSurfaceProvider() : null);

                cameraProvider.unbindAll();

                if (start) {
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    ImageAnalysis pinAnalysis = new ImageAnalysis.Builder().build();
                    pinAnalysis.setAnalyzer(cameraExecutor, new PinAnalyzer(this, pinAnalysis, model.getSimChooseData()));

                    camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, pinAnalysis);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void toggleIconAndText(boolean isFlashEnabled) {
        model.setFlashEnabled(isFlashEnabled);
        binding.btnToggleFlash.setText(getContext().getString(isFlashEnabled ? R.string.btn_toggle_flash_off :
                R.string.btn_toggle_flash_on));
        binding.btnToggleFlash.setIconResource(isFlashEnabled ? R.drawable.ic_baseline_flash_on_24 :
                R.drawable.ic_baseline_flash_off_24);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        showProgressBar(true);
        binding.preview.postDelayed(() -> {
            showProgressBar(false);
            initStep();
        }, 2000);

        Snackbar.make(getContentLayout(), getContext().getString(R.string.tap_to_preview_text),
                Snackbar.LENGTH_LONG).show();
    }

    private void showProgressBar(boolean show) {
        binding.preview.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        binding.btnToggleFlash.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        if (model.isFlashEnabled())
            toggleFlash(false);
        startStopCamera(false);
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public void onTextRecognized(String recognizedStr) {
        Utils.vibrateIfNecessary(getContext());
        model.setPinScanData(recognizedStr);
        binding.groupPreview.setVisibility(View.GONE);
        binding.tvScannedPin.setText(getContext().getString(R.string.tv_scanned_pin, recognizedStr));
        binding.groupPin.setVisibility(View.VISIBLE);
        startStopCamera(false);
        markAsCompletedOrUncompleted(true);
    }

    @Override
    public void onRecognizationFailed(Exception e) {
        e.printStackTrace();
    }
}
