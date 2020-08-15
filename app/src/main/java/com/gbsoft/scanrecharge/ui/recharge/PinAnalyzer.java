package com.gbsoft.scanrecharge.ui.recharge;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;

public class PinAnalyzer implements ImageAnalysis.Analyzer {
    private OnTextRecognizedListener listener;
    private SimName simName;

    public PinAnalyzer(OnTextRecognizedListener listener, SimName simName) {
        this.listener = listener;
        this.simName = simName;
    }

    @androidx.camera.core.ExperimentalGetImage
    @Override
    public void analyze(@NonNull ImageProxy image) {
        Log.d("Analyse", "analyze is called.");
        Image cardImg = image.getImage();
        if (cardImg != null) {
            InputImage cardImage = InputImage.fromMediaImage(cardImg, image.getImageInfo().getRotationDegrees());

            TextRecognizer recognizer = TextRecognition.getClient();

            recognizer.process(cardImage)
                    .addOnSuccessListener(text -> {

                        StringBuilder pinStrBuilder = new StringBuilder();
                        List<Text.TextBlock> textBlockList = text.getTextBlocks();
                        for (Text.TextBlock textBlock : textBlockList) {
                            List<Text.Line> lines = textBlock.getLines();
                            for (Text.Line line : lines) {
                                List<Text.Element> elements = line.getElements();
                                if (simName.getCurrentSim().equals(SimName.NTC) && elements.size() == 3) {
                                    char[] lineChars = line.getText().toCharArray();
                                    for (char c : lineChars) {
                                        if (pinStrBuilder.length() < 16 && Character.isDigit(c)) {
                                            pinStrBuilder.append(c);
                                        }
                                    }
                                    break;
                                } else if (simName.getCurrentSim().equals(SimName.Ncell) && elements.size() == 4) {
                                    char[] lineChars = line.getText().toCharArray();
                                    for (char c : lineChars) {
                                        if (pinStrBuilder.length() < 16 && Character.isDigit(c)) {
                                            pinStrBuilder.append(c);
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        listener.onTextRecognized(pinStrBuilder.toString());
                        image.close();
                    })
                    .addOnFailureListener(e -> listener.onRecognizationFailed(e));
        }
    }
}
