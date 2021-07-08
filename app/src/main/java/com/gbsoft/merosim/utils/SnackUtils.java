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

package com.gbsoft.merosim.utils;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public class SnackUtils {
    public static void showMessage(View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    public static void showMessage(View view, @StringRes int resId, Object... formatArgs) {
        Snackbar.make(view, view.getResources().getString(resId, formatArgs), Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("ShowToast")
    public static void showMessageWithCallback(View view, @StringRes int resId, Snackbar.Callback callback) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).addCallback(callback).show();
    }

    public static void showMessage(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}
