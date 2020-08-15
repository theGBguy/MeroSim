package com.gbsoft.scanrecharge.ui.recharge;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;

import com.gbsoft.scanrecharge.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RechargeViewModel extends AndroidViewModel {
    // variables for RechargeFragment
    public static final int PERMISSIONS_REQUEST_CODE = 1000;
    private SimName simChooseData;
    private String pinScanData;

    // variables for PinScanStep
    private ExecutorService cameraService;
    private Camera camera;
    private boolean isFlashEnabled;
    private LifecycleOwner lifecycleOwner;

    public RechargeViewModel(@NonNull Application application) {
        super(application);
        cameraService = Executors.newSingleThreadExecutor();
        this.isFlashEnabled = false;
        this.simChooseData = new SimName();
        this.pinScanData = "";
    }

    public String getStepTitle(int stepPos) {
        Context appContext = getApplication().getApplicationContext();
        switch (stepPos) {
            case 0:
                return appContext.getString(R.string.choose_carrier_text);
            case 1:
                return appContext.getString(R.string.scan_pin_text);
            case 2:
                return appContext.getString(R.string.confirm_details_text);
            default:
                return "";
        }
    }

    public SimName getSimChooseData() {
        return simChooseData;
    }

    public ExecutorService getCameraService() {
        return cameraService;
    }

    public void shutDownCameraService() {
        cameraService.shutdown();
    }

    public String getPinScanData() {
        return pinScanData;
    }

    public void setPinScanData(String pinScanData) {
        this.pinScanData = pinScanData;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean isFlashEnabled() {
        return isFlashEnabled;
    }

    public void setFlashEnabled(boolean flashEnabled) {
        isFlashEnabled = flashEnabled;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }
}
