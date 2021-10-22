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

package com.gbsoft.merosim.ui.home;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.databinding.FragmentHomeBinding;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private SimRecyclerAdapter adapter;
    private RVEmptyObserver simListObv;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                List<Sim> simList;
                if (isGranted)
                    simList = viewModel.getSimList();
                else
                    simList = new ArrayList<>();
                adapter = new SimRecyclerAdapter(simList);
                binding.rvSimList.setAdapter(adapter);
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.rvSimList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSimList.setHasFixedSize(true);

        handleReadPhoneStatePermission();

        simListObv = new RVEmptyObserver(binding.rvSimList, binding.tvNoSim);
        adapter.registerAdapterDataObserver(simListObv);
    }

    private void handleReadPhoneStatePermission() {
        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_PHONE_STATE)) {
            adapter = new SimRecyclerAdapter(viewModel.getSimList());
            binding.rvSimList.setAdapter(adapter);
        } else if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_PHONE_STATE)) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.perm_dialog_title))
                    .setMessage(getString(R.string.perm_read_phone_state_msg))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.positive_dialog_btn_txt), (dialog, which) -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.negative_dialog_btn_txt), (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(simListObv);
        viewModel = null;
        binding = null;
    }
}