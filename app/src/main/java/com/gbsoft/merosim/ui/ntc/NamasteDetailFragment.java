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

package com.gbsoft.merosim.ui.ntc;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentNamasteDetailBinding;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.ContactsLoader;
import com.gbsoft.merosim.ui.OnContactFoundListener;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.ui.PickPhoneNumber;
import com.gbsoft.merosim.utils.EventObserver;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NamasteDetailFragment extends BaseTelecomFragment {
    private FragmentNamasteDetailBinding binding;
    private NamasteDetailViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNamasteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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

        binding.ntcTilRecipient.setEndIconOnClickListener(v -> {
            if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_CONTACTS)) {
                contactPicker.launch(null);
            } else {
                handlePermission(Manifest.permission.READ_CONTACTS, getString(R.string.perm_read_contacts_msg), readContactsPermissionLauncher);
            }
        });
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
        binding.ntcTilRecipient.setHelperText(getResources().getString(R.string.tiet_selected_recipient, name));
        new Handler().postDelayed(() -> binding.ntcTilRecipient.setHelperText(null), 3000);
    }
}