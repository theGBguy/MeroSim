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

package com.gbsoft.merosim.ui.recharge;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.gbsoft.merosim.data.Sim;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;

public class PinAnalyzer implements ImageAnalysis.Analyzer {
    private static final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    private final OnTextRecognizedListener listener;
    private final String simName;
    private final ImageAnalysis pinAnalysis;

    public PinAnalyzer(OnTextRecognizedListener listener, ImageAnalysis pinAnalysis, String simName) {
        this.listener = listener;
        this.pinAnalysis = pinAnalysis;
        this.simName = simName;
    }

    @ExperimentalGetImage
    @Override
    public void analyze(@NonNull ImageProxy image) {
        Log.d("Analyse", "analyze is called.");
        Image cardImg = image.getImage();
        if (cardImg != null) {
            InputImage cardImage = InputImage.fromMediaImage(cardImg, image.getImageInfo().getRotationDegrees());

            recognizer.process(cardImage)
                    .addOnSuccessListener(text -> {
                        Log.d("Analyse", text.getText());

                        List<Text.TextBlock> textBlockList = text.getTextBlocks();
                        for (Text.TextBlock textBlock : textBlockList) {
                            List<Text.Line> lines = textBlock.getLines();
                            for (Text.Line line : lines) {
                                List<Text.Element> elements = line.getElements();
                                if ((simName.equals(Sim.NAMASTE) || (simName.equals(Sim.SMART_CELL)) && elements.size() == 3)
                                        || (simName.equals(Sim.NCELL) && elements.size() == 4)) {
                                    String scanned = getPinFromLine(line);
                                    if (scanned.length() == 16) {
                                        listener.onTextRecognized(scanned);
                                        pinAnalysis.clearAnalyzer();
                                    } else
                                        listener.onRecognizationFailed(new Exception("Couldn't scan the pin!!"));
                                    break;
                                }
                            }
                        }
                        image.close();
                    })
                    .addOnFailureListener(e -> {
                        image.close();
                        listener.onRecognizationFailed(e);
                    });
        }
    }

    private String getPinFromLine(Text.Line line) {
        StringBuilder pinStr = new StringBuilder();
        char[] lineChars = line.getText().toCharArray();
        for (char c : lineChars) {
            if (pinStr.length() < 16 && Character.isDigit(c)) {
                pinStr.append(c);
            }
        }
        return pinStr.toString();
    }
}
