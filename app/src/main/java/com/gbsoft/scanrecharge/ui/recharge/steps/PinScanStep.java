package com.gbsoft.scanrecharge.ui.recharge.steps;

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

import com.gbsoft.scanrecharge.R;
import com.gbsoft.scanrecharge.databinding.StepPinScanBinding;
import com.gbsoft.scanrecharge.ui.recharge.OnTextRecognizedListener;
import com.gbsoft.scanrecharge.ui.recharge.PinAnalyzer;
import com.gbsoft.scanrecharge.ui.recharge.RechargeViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import ernestoyaquello.com.verticalstepperform.Step;

public class PinScanStep extends Step<String> implements OnTextRecognizedListener, View.OnClickListener, View.OnTouchListener {
    private StepPinScanBinding binding;
    private RechargeViewModel model;

    public PinScanStep(RechargeViewModel model) {
        super(model.getStepTitle(1));
        this.model = model;
    }

    @Override
    public String getStepData() {
        return model.getPinScanData();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String pinCode = getStepData();
        return pinCode.isEmpty() ? getContext().getString(R.string.empty_pin_text) : pinCode;
    }

    @Override
    public void restoreStepData(String data) {
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        if (!stepData.isEmpty()) {
            char first = stepData.charAt(0);
            boolean isDataValid = Character.isDigit(first) && stepData.length() == 16;
            return new IsDataValid(isDataValid, "");
        }
        return new IsDataValid(false, "");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected View createStepContentLayout() {
        binding = StepPinScanBinding.inflate(LayoutInflater.from(getContext()));
        binding.btnToggleFlash.setOnClickListener(this);
        binding.preview.setOnTouchListener(PinScanStep.this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof MaterialButton) {
            boolean isFlashEnabled = model.isFlashEnabled();
            model.setFlashEnabled(!isFlashEnabled);
            toggleIconNText(isFlashEnabled);
            if (model.getCamera().getCameraInfo().hasFlashUnit()) {
                model.getCamera().getCameraControl().enableTorch(isFlashEnabled);
            } else {
                Snackbar.make(getEntireStepLayout(), getContext().getString(R.string.no_flash_text),
                        Snackbar.LENGTH_LONG).show();
            }
        }
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
                    model.getCamera().getCameraControl().startFocusAndMetering(
                            new FocusMeteringAction.Builder(
                                    autoFocusPoint, FocusMeteringAction.FLAG_AF
                            ).disableAutoCancel().build()
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
                preview.setSurfaceProvider(start ? binding.preview.createSurfaceProvider() : null);

                cameraProvider.unbindAll();

                if (start) {
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    ImageAnalysis pinAnalysis = new ImageAnalysis.Builder().build();
                    pinAnalysis.setAnalyzer(model.getCameraService(), new PinAnalyzer(this, model.getSimChooseData()));

                    Camera camera = cameraProvider.bindToLifecycle(model.getLifecycleOwner(), cameraSelector, preview, pinAnalysis);
                    model.setCamera(camera);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void toggleIconNText(boolean isFlashEnabled) {
        binding.btnToggleFlash.setText(getContext().getString(isFlashEnabled ? R.string.btn_toggle_flash_on :
                R.string.btn_toggle_flash_off));
        binding.btnToggleFlash.setIconResource(isFlashEnabled ? R.drawable.ic_baseline_flash_on_24 :
                R.drawable.ic_baseline_flash_off_24);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        startStopCamera(true);
        toggleIconNText(false);
        Snackbar.make(getContentLayout(), getContext().getString(R.string.tap_to_preview_text),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStepClosed(boolean animated) {
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
        if (!recognizedStr.isEmpty()) {
            model.setPinScanData(recognizedStr);
            binding.tvScannedPin.setText(getContext().getString(R.string.tv_scanned_pin, recognizedStr));
            binding.tvScannedPin.setVisibility(View.VISIBLE);
            markAsCompletedOrUncompleted(true);
        }
    }

    @Override
    public void onRecognizationFailed(Exception e) {
        e.printStackTrace();
    }
}
