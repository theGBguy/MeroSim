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

package com.gbsoft.merosim.ui.smart;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentSmartDetailBinding;
import com.gbsoft.merosim.ui.ContactsLoader;
import com.gbsoft.merosim.ui.OnContactFoundListener;
import com.gbsoft.merosim.ui.PickPhoneNumber;
import com.gbsoft.merosim.utils.EventObserver;
import com.gbsoft.merosim.utils.SnackUtils;

public class SmartDetailFragment extends Fragment implements OnContactFoundListener {
    private FragmentSmartDetailBinding binding;
    private SmartDetailViewModel viewModel;
    private ContactsLoader loader;

    private final ActivityResultLauncher<Void> contactPicker =
            registerForActivityResult(
                    new PickPhoneNumber(),
                    uri -> loader.restartLoader(LoaderManager.getInstance(SmartDetailFragment.this), uri)
            );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSmartDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SmartDetailViewModel.class);
        binding.setVm(viewModel);
        binding.setEventhandler(new SmartEventHandler(requireContext(), viewModel));
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loader = new ContactsLoader(requireContext(), this);
        viewModel.init(requireArguments());

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));

        binding.smartTilRecipient.setEndIconOnClickListener(v -> contactPicker.launch(null));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
        binding = null;
    }

    @Override
    public void onContactFound(@NonNull String name, @NonNull String number) {
        viewModel.recipient.setValue(number);
        binding.smartTilRecipient.setHelperText(getResources().getString(R.string.tiet_selected_recipient, name));
        new Handler().postDelayed(() -> binding.smartTilRecipient.setHelperText(null), 3000);
    }
}