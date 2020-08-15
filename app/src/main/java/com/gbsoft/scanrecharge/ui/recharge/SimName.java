package com.gbsoft.scanrecharge.ui.recharge;

public class SimName {

    public static final String NTC = "NTC";
    public static final String Ncell = "Ncell";
    public static final String SmartCell = "SmartCell";
    public static final String None = "None";

    private String currentSim;

    public SimName() {
        this.currentSim = None;
    }

    public SimName(String currentSim) {
        this.currentSim = currentSim;
    }

    public String getCurrentSim() {
        return currentSim;
    }

    public void setCurrentSim(String currentSim) {
        this.currentSim = currentSim;
    }
}
