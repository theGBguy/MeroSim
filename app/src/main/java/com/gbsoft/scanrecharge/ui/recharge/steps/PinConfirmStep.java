package com.gbsoft.scanrecharge.ui.recharge.steps;

import android.view.LayoutInflater;
import android.view.View;

import com.gbsoft.scanrecharge.R;
import com.gbsoft.scanrecharge.databinding.StepPinConfirmBinding;
import com.gbsoft.scanrecharge.ui.recharge.RechargeViewModel;
import com.google.android.material.snackbar.Snackbar;

import ernestoyaquello.com.verticalstepperform.Step;

public class PinConfirmStep extends Step<Void> {
    private StepPinConfirmBinding binding;
    private RechargeViewModel model;

    public PinConfirmStep(RechargeViewModel model) {
        super(model.getStepTitle(2), "", "Recharge");
        this.model = model;
    }

    @Override
    public Void getStepData() {
        return null;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return null;
    }

    @Override
    public void restoreStepData(Void data) {

    }

    @Override
    protected IsDataValid isStepDataValid(Void stepData) {
        return null;
    }

    @Override
    protected View createStepContentLayout() {
        binding = StepPinConfirmBinding.inflate(LayoutInflater.from(getContext()));
        return binding.getRoot();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        Snackbar.make(getContentLayout(), getContext().getString(R.string.wrong_scanning_text),
                Snackbar.LENGTH_LONG).show();
        binding.tvConfirmSim.setText(getContext().getString(R.string.tv_confirm_sim, model.getSimChooseData().getCurrentSim()));
        binding.editTextTextPersonName.setText(model.getPinScanData());
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