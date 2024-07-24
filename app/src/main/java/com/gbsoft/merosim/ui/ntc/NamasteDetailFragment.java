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

package com.gbsoft.merosim.ui.ntc;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.codertainment.materialintro.sequence.MaterialIntroSequenceListener;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentNamasteDetailBinding;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.MainActivity;
import com.gbsoft.merosim.utils.EventObserver;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.gms.ads.interstitial.InterstitialAd;

// Fragment used to show Namaste sim card details
public class NamasteDetailFragment extends BaseTelecomFragment implements MaterialIntroSequenceListener {
    private FragmentNamasteDetailBinding binding;
    private NamasteDetailViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNamasteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NamasteDetailViewModel.class);
        binding.setVm(viewModel);
        binding.setEventhandler(new NamasteEventHandler(requireContext(), viewModel, fixerContract));
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.init(requireArguments());

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));

        binding.ntcTilSecurityCode.setEndIconOnClickListener(v -> viewModel.saveSecurityCode());

        binding.ntcTilRecipient.setEndIconOnClickListener(v -> launchContactPicker());

        InterstitialAd interstitialAd = ((MainActivity) requireActivity()).getInterstitialAd();
        if (interstitialAd == null) {
            ((MainActivity) requireActivity()).loadInterstitialAds();
        } else {
            interstitialAd.show(requireActivity());
        }

        ((MainActivity) requireActivity()).showIntro.observe(getViewLifecycleOwner(), shouldShow -> {
            if (shouldShow) {
                Utils.showMaterialIntroSequence(requireActivity(), binding.ntcBtnPhone, binding.ntcBtnSimBalance, binding.ntcBtnSimOwner, binding.ntcTvTransferBalance);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_intuitive))) {
            viewModel.setIntuitiveModeStatus(sharedPreferences.getBoolean(getString(R.string.key_intuitive), true));
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onContactFound(@NonNull String name, @NonNull String number) {
        viewModel.recipient.setValue(number);
        binding.ntcTilRecipient.setHelperText(getResources().getString(R.string.tiet_selected_recipient, name));
        new Handler().postDelayed(() -> {
            if (binding == null) return;
            binding.ntcTilRecipient.setHelperText(null);
        }, 3000);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onProgress(boolean b, @NonNull String s, int i, int i1) {

    }
}