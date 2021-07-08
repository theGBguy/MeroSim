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

import androidx.databinding.BindingAdapter;

import com.gbsoft.merosim.R;
import com.google.android.material.textfield.TextInputLayout;

public class CommonBindingAdapters {
    @BindingAdapter("errorRes")
    public static void setErrorText(TextInputLayout textInputLayout, Integer error) {
        if (error == null || error == 0)
            textInputLayout.setError(null);
        else {
            textInputLayout.setError(textInputLayout.getResources().getString(error));
            if(textInputLayout.getId() == R.id.ntc_til_recipient){
                textInputLayout.setErrorIconDrawable(null);
            }
        }
    }
}
