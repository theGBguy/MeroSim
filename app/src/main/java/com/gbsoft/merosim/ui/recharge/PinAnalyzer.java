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
 * Last modified: 2021/10/25
 */

package com.gbsoft.merosim.ui.recharge;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

public class PinAnalyzer implements ImageAnalysis.Analyzer {
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
//        Utils.recognizeText(simName, image, listener);
    }
}
