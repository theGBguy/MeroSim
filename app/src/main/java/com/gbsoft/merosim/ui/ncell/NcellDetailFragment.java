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

package com.gbsoft.merosim.ui.ncell;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentNcellDetailBinding;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.MainActivity;
import com.gbsoft.merosim.utils.EventObserver;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import org.jetbrains.annotations.NotNull;

// Fragment to show details of Ncell sim card
public class NcellDetailFragment extends BaseTelecomFragment {
    private static final String TAG = "NcellDetailFragment";
    private FragmentNcellDetailBinding binding;
    private NcellDetailViewModel viewModel;
    private InterstitialAd interstitialAd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNcellDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NcellDetailViewModel.class);
        binding.setVm(viewModel);
        binding.setEventhandler(new NcellEventHandler(requireContext(), viewModel, fixerContract));
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.init(requireArguments());

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));

        binding.ncellTilRecipient.setEndIconOnClickListener(v -> launchContactPicker());

        InterstitialAd interstitialAd = ((MainActivity) requireActivity()).getInterstitialAd();
        if (interstitialAd == null) {
            ((MainActivity) requireActivity()).loadInterstitialAds();
        } else {
            interstitialAd.show(requireActivity());
        }

        ((MainActivity) requireActivity()).showIntro.observe(getViewLifecycleOwner(), shouldShow -> {
            if (shouldShow) {
                Utils.showMaterialIntroSequence(requireActivity(), binding.ncellBtnPhone, binding.ncellBtnSimBalance, binding.ncellBtnSimOwner);
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
        binding.ncellTilRecipient.setHelperText(getResources().getString(R.string.tiet_selected_recipient, name));
        new Handler().postDelayed(() -> binding.ncellTilRecipient.setHelperText(null), 3000);
    }
}