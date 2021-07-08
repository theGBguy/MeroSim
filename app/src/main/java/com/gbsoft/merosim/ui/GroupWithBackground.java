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
 * Last modified: 2021/06/06
 */

package com.gbsoft.merosim.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintHelper;

import com.gbsoft.merosim.R;

public class GroupWithBackground extends ConstraintHelper {
    public GroupWithBackground(Context context) {
        super(context);
    }

    public GroupWithBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupWithBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GroupWithBackground, 0, 0);
        try {
            Drawable background = a.getDrawable(R.styleable.GroupWithBackground_groupBackground);
            if (background != null)
                setBackground(background);
        } finally {
            a.recycle();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.applyLayoutFeatures();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.applyLayoutFeatures();
    }

    public void setElevation(float elevation) {
        super.setElevation(elevation);
        this.applyLayoutFeatures();
    }
}
