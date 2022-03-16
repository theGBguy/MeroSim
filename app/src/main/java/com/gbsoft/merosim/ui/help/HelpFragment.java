/*
 * Created by Chiranjeevi Pandey on 2/23/22, 4:19 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/23
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

package com.gbsoft.merosim.ui.help;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.util.LinkifyCompat;
import androidx.fragment.app.Fragment;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentHelpBinding;

public class HelpFragment extends Fragment {
    private FragmentHelpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvFaq.setText(Html.fromHtml(getString(R.string.tv_faq_txt), Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.tvFaq.setText(Html.fromHtml(getString(R.string.tv_faq_txt)));
        }
        LinkifyCompat.addLinks(binding.tvFaq, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}