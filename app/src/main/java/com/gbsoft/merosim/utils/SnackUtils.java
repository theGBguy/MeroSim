/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/10/28
 *
 * Licensed under GNU General Public License v3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsoft.merosim.utils;

import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

/*
 * This class contains utility methods which are used to show snackbar
 * all over this app.
 */

public class SnackUtils {
    public static void showMessage(View view, @StringRes int resId) {
        // creating a weak reference instance so the original view object get
        // GC'ed when activity/fragment is destroyed
        WeakReference<View> viewWeakRef = new WeakReference<>(view);
        if (viewWeakRef.get() == null) return;
        Snackbar.make(viewWeakRef.get(), resId, Snackbar.LENGTH_LONG).show();
    }

    public static void showMessage(View view, @StringRes int resId, Object... formatArgs) {
        WeakReference<View> viewWeakRef = new WeakReference<>(view);
        if (viewWeakRef.get() == null) return;
        Snackbar.make(viewWeakRef.get(), view.getResources().getString(resId, formatArgs), Snackbar.LENGTH_LONG).show();
    }

    public static void showMessageWithCallback(View view, @StringRes int resId, Snackbar.Callback callback) {
        WeakReference<View> viewWeakRef = new WeakReference<>(view);
        if (viewWeakRef.get() == null) return;
        Snackbar.make(viewWeakRef.get(), resId, Snackbar.LENGTH_LONG).addCallback(callback).show();
    }

    public static void showMessage(View view, String msg) {
        WeakReference<View> viewWeakRef = new WeakReference<>(view);
        if (viewWeakRef.get() == null) return;
        Snackbar.make(viewWeakRef.get(), msg, Snackbar.LENGTH_LONG).show();
    }
}
