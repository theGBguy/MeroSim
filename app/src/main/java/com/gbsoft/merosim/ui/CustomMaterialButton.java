/*
 * Created by Chiranjeevi Pandey on 3/10/22, 3:34 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/03/10
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

package com.gbsoft.merosim.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

public class CustomMaterialButton extends MaterialButton {
    public CustomMaterialButton(@NonNull Context context) {
        super(context);
    }

    public CustomMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        animate().scaleX(.8f)
                .scaleY(.8f)
                .withEndAction(() ->
                        animate().scaleX(1f).scaleY(1f));
    }
}
