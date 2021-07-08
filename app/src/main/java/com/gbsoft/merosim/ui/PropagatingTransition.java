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
 * Last modified: 2021/06/02
 */

package com.gbsoft.merosim.ui;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.transition.CircularPropagation;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionPropagation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PropagatingTransition {
    private final ViewGroup sceneRoot;
    private View startingView;
    private final Transition transition;
    private long duration;
    private Interpolator interpolator;
    private TransitionPropagation propagation;

    private final List<View> targets = new ArrayList<>();

    public PropagatingTransition(ViewGroup sceneRoot, View startingView, Transition transition, long duration, Interpolator interpolator, TransitionPropagation propagation) {
        this.sceneRoot = new WeakReference<>(sceneRoot).get();
        this.startingView = new WeakReference<>(startingView).get();
        this.transition = transition;
        this.duration = duration;
        this.interpolator = interpolator;
        this.propagation = propagation;
    }

    public void init() {
        if (sceneRoot == null) return;
        int childCount = sceneRoot.getChildCount();
        for (int i = 0; i < childCount; i++)
            targets.add(sceneRoot.getChildAt(i));

        if (interpolator == null)
            interpolator = new LinearOutSlowInInterpolator();
        if (duration == -1)
            duration = 650;
        if (propagation == null)
            propagation = new CircularPropagation();
        transition.setInterpolator(interpolator);
        transition.setDuration(duration);
        transition.setPropagation(propagation);
    }

    public void start() {
        if (sceneRoot == null) return;
        if (startingView == null && sceneRoot.getChildCount() > 0)
            startingView = sceneRoot.getChildAt(0);

        transition.setEpicenterCallback(callback);

        for (View target : targets)
            target.setVisibility(View.INVISIBLE);

        TransitionManager.beginDelayedTransition(sceneRoot, transition);

        for (View target : targets)
            target.setVisibility(View.VISIBLE);

    }

    private final Transition.EpicenterCallback callback = new Transition.EpicenterCallback() {
        @Override
        public Rect onGetEpicenter(@NonNull Transition transition) {
            if (sceneRoot == null) return null;
            Rect viewRect = new Rect();
            if (startingView != null)
                startingView.getGlobalVisibleRect(viewRect);
            else
                sceneRoot.getGlobalVisibleRect(viewRect);
            return viewRect;
        }
    };
}
