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
 * Last modified: 2021/10/26
 */

package com.gbsoft.merosim.ui.recharge.steps;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.StepPinScanBinding;
import com.gbsoft.merosim.ui.recharge.OnTextRecognizedListener;
import com.gbsoft.merosim.ui.recharge.RechargeViewModel;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import ernestoyaquello.com.verticalstepperform.Step;

public class PinScanStep extends Step<String> implements OnTextRecognizedListener, View.OnClickListener, View.OnTouchListener {
    final int STATE_LOADING = 1;
    final int STATE_PREVIEW = 2;
    final int STATE_CAPTURED = 3;
    final int STATE_SCANNED = 4;

    private StepPinScanBinding binding;
    private final RechargeViewModel model;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private ImageCapture imageCapture;
    private final LifecycleOwner lifecycleOwner;
    private final ExecutorService executor;

    private final MutableLiveData<Integer> state = new MutableLiveData<>();
    private final Observer<Integer> stateObserver = state -> {
        if (state == null) return;
        switch (state) {
            case STATE_LOADING:
                showHideProgress(true);
                showHidePreview(false);
//                showHideCaptured(false);
                showHidePin(false);
                break;
            case STATE_PREVIEW:
                showHideProgress(false);
                showHidePreview(true);
                showHideCaptured(false);
                showHidePin(false);
                break;
            case STATE_CAPTURED:
                showHideProgress(true);
                showHidePreview(false);
                showHideCaptured(true);
                showHidePin(false);
                break;
            case STATE_SCANNED:
                showHideProgress(false);
                showGonePreview(false);
                showHideCaptured(false);
                showHidePin(true);
                break;
        }
    };

    private void showHideProgress(boolean show) {
        binding.groupProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showGonePreview(boolean show) {
        binding.groupPreview.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showHidePreview(boolean show){
        binding.groupPreview.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showHideCaptured(boolean show) {
        binding.ivPreview.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showHidePin(boolean show) {
        binding.groupPin.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public PinScanStep(RechargeViewModel model, String title, String nextButtonText, LifecycleOwner lifecycleOwner, ExecutorService executor) {
        super(title, "", nextButtonText);
        this.model = model;
        this.lifecycleOwner = lifecycleOwner;
        this.executor = executor;
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
        binding.btnTakePic.setOnClickListener(this);
        binding.btnToggleFlash.setOnClickListener(this);
        binding.btnRetry.setOnClickListener(this);
        binding.preview.setOnTouchListener(PinScanStep.this);
        return binding.getRoot();
    }

    @ExperimentalGetImage
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_toggle_flash) {
            toggleFlash(true);
        } else if (id == R.id.btn_take_pic) {
            takePic();
        } else if (id == R.id.btn_retry) {
            initStep(false);
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

    @ExperimentalGetImage
    private void takePic() {
        File output = getTempImageFile();
        if (output == null) return;

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(output).build();
        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri imageUri = outputFileResults.getSavedUri();

                mediaPlayer.start();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (model.isFlashEnabled())
                        toggleFlash(false);
                    state.setValue(STATE_CAPTURED);
                    binding.ivPreview.setImageURI(imageUri);
                });

                InputImage image;
                try {
                    image = InputImage.fromFilePath(getContext(), imageUri);
                    Utils.recognizeText(model.getSimChooseData(), image, PinScanStep.this);
                } catch (IOException e) {
                    e.printStackTrace();
                    state.setValue(STATE_PREVIEW);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                SnackUtils.showMessage(getContentLayout(), "Couldn't capture picture. Please try again later.");
            }
        });
    }

    private File getTempImageFile() {
        File temp = null;
        try {
            temp = File.createTempFile("scanned_pin", ".jpg");
            temp.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private void initStep(boolean start) {
        if (start)
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.camera_shutter_sound);
        startStopCamera(true);
        toggleIconAndText(false);
        state.setValue(STATE_PREVIEW);
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
                    imageCapture = new ImageCapture.Builder()
                            .setTargetRotation(binding.preview.getDisplay().getRotation())
                            .build();
                    camera = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
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
        binding.btnToggleFlash.setIconResource(isFlashEnabled ? R.drawable.ic_round_flash_on_24 :
                R.drawable.ic_round_flash_off_24);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        state.setValue(STATE_LOADING);
        binding.preview.postDelayed(() -> initStep(true), 2000);
        state.observeForever(stateObserver);
        SnackUtils.showMessage(getContentLayout(), R.string.tap_to_preview_text);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        if (model.isFlashEnabled())
            toggleFlash(false);
        startStopCamera(false);
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
        state.removeObserver(stateObserver);
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
        state.setValue(STATE_SCANNED);
        binding.tvScannedPin.setText(getContext().getString(R.string.tv_scanned_pin, recognizedStr));
        startStopCamera(false);
        markAsCompleted(true);
    }

    @Override
    public void onRecognizationFailed(Exception e) {
        SnackUtils.showMessage(getContentLayout(), "Error occurred : " + e.getMessage());
        state.setValue(STATE_PREVIEW);
        markAsUncompleted("", true);
    }
}
