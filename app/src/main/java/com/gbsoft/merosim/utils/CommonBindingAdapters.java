/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/05/31
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

import android.text.Html;

import androidx.annotation.StringRes;
import androidx.databinding.BindingAdapter;

import com.gbsoft.merosim.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

// common binding adapters to be used with data binding library
public class CommonBindingAdapters {
    // adapter to set error text resources in TextInputLayout view
    @BindingAdapter("errorText")
    public static void setErrorText(TextInputLayout textInputLayout, Integer error) {
        if (error == null || error == 0) {
            textInputLayout.setError(null);
        } else {
            textInputLayout.setError(textInputLayout.getResources().getString(error));
            if (textInputLayout.getId() == R.id.ntc_til_recipient) {
                textInputLayout.setErrorIconDrawable(null);
            }
        }
    }
}
