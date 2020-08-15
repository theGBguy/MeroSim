package com.gbsoft.scanrecharge.ui.recharge.steps;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.gbsoft.scanrecharge.R;
import com.gbsoft.scanrecharge.databinding.StepSimChooseBinding;
import com.gbsoft.scanrecharge.ui.recharge.RechargeViewModel;
import com.gbsoft.scanrecharge.ui.recharge.SimName;

import ernestoyaquello.com.verticalstepperform.Step;

public class SimChooseStep extends Step<String> implements RadioGroup.OnCheckedChangeListener {
    private StepSimChooseBinding binding;
    private RechargeViewModel model;

    public SimChooseStep(RechargeViewModel model) {
        super(model.getStepTitle(0));
        this.model = model;
    }

    @Override
    public String getStepData() {
        return model.getSimChooseData().getCurrentSim();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String carrierName = getStepData();
        return carrierName.equals(SimName.None) ? getContext().getString(R.string.empty_sim_text) : carrierName;
    }

    @Override
    public void restoreStepData(String data) {
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        boolean isDataValid = !stepData.equals(SimName.None);
        return new IsDataValid(isDataValid, "");
    }

    @Override
    protected View createStepContentLayout() {
        binding = StepSimChooseBinding.inflate(LayoutInflater.from(getContext()));
        binding.radioGroup.setOnCheckedChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rdo_btn_ntc:
                model.getSimChooseData().setCurrentSim(SimName.NTC);
                break;
            case R.id.rdo_btn_ncell:
                model.getSimChooseData().setCurrentSim(SimName.Ncell);
                break;
            case R.id.rdo_btn_smartcell:
                model.getSimChooseData().setCurrentSim(SimName.SmartCell);
                break;
            default:
                model.getSimChooseData().setCurrentSim(SimName.None);
                break;
        }
        markAsCompletedOrUncompleted(true);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        String currentSim = model.getSimChooseData().getCurrentSim();
        switch (currentSim) {
            case SimName.NTC:
                binding.rdoBtnNtc.setChecked(true);
                break;
            case SimName.Ncell:
                binding.rdoBtnNcell.setChecked(true);
                break;
            case SimName.SmartCell:
                binding.rdoBtnSmartcell.setChecked(true);
                break;
            default:
                break;
        }

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
