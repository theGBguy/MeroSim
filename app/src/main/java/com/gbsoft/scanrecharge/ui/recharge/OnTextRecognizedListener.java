package com.gbsoft.scanrecharge.ui.recharge;

public interface OnTextRecognizedListener {
    void onTextRecognized(String recognizedStr);

    void onRecognizationFailed(Exception e);
}
