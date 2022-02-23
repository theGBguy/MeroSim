/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/22
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
import com.gbsoft.merosim.databinding.FragmentHomeBinding;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/*
 * This fragment shows the list of sim cards installed
 * in the device.
 */

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private SimRecyclerAdapter adapter;
    // observer to observe whether the recycler view has
    // empty data set
    private RecyclerViewEmptyObserver simListObv;

    // request read phone state permission and query sim details
    // when granted
    private final ActivityResultLauncher<String> readPhoneStatePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> viewModel.querySimCardDetails());

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

        simListObv = new RecyclerViewEmptyObserver(binding.rvSimList, binding.tvNoSim);
        adapter = new SimRecyclerAdapter(simListObv);
        binding.rvSimList.setAdapter(adapter);

        adapter.registerAdapterDataObserver(simListObv);

        // loads the banner ads
        binding.adViewHome.loadAd(new AdRequest.Builder().build());

        // asks user name on the first usage of the app
        askUserNameIfRequired();

        // request phone state permission if required
        handleReadPhoneStatePermission();
    }

    private void askUserNameIfRequired() {
        if (viewModel.shouldAskUserName()) {
            Utils.askUserName(requireContext());
        }
    }

    private void handleReadPhoneStatePermission() {
        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_PHONE_STATE)) {
            viewModel.querySimCardDetails();
            viewModel.getLiveSimList().observe(getViewLifecycleOwner(), sims -> adapter.submitList(sims));
        } else if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_PHONE_STATE)) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.perm_dialog_title))
                    .setMessage(getString(R.string.perm_read_phone_state_msg))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.positive_dialog_btn_txt), (dialog, which) -> {
                        readPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.negative_dialog_btn_txt), (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            readPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
        }
    }

    @Override
    public void onPause() {
        binding.adViewHome.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.adViewHome.resume();
    }

    @Override
    public void onDestroyView() {
        binding.adViewHome.destroy();
        adapter.unregisterAdapterDataObserver(simListObv);
        viewModel = null;
        binding = null;
        super.onDestroyView();
    }
}