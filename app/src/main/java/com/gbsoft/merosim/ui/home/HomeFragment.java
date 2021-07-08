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

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.TransitionSet;

import com.gbsoft.merosim.databinding.FragmentHomeBinding;
import com.gbsoft.merosim.ui.PropagatingTransition;
import com.gbsoft.merosim.utils.PermissionUtils;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private SimRecyclerAdapter adapter;
    private RVEmptyObserver simListObv;

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

        adapter = new SimRecyclerAdapter(viewModel.getSimList());
        binding.rvSimList.setAdapter(adapter);

        simListObv = new RVEmptyObserver(binding.rvSimList, binding.tvNoSim);
        adapter.registerAdapterDataObserver(simListObv);

        PermissionUtils.checkAndRequestPermissions(requireActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(simListObv);
        viewModel = null;
        binding = null;
    }
}