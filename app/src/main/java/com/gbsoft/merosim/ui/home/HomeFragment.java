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
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codertainment.materialintro.view.MaterialIntroView;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.FragmentHomeBinding;
import com.gbsoft.merosim.ui.MainActivity;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.gms.ads.AdRequest;

/*
 * This fragment shows the list of sim cards installed
 * in the device.
 */

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private MaterialIntroView materialIntroView;

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

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.rvSimList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSimList.setHasFixedSize(true);

        binding.viewSwitcherHome.setInAnimation(requireContext(), android.R.anim.fade_in);
        binding.viewSwitcherHome.setOutAnimation(requireContext(), android.R.anim.fade_out);

        simListObv = new RecyclerViewEmptyObserver(binding.rvSimList, binding.viewSwitcherHome);
        adapter = new SimRecyclerAdapter(simListObv);
        binding.rvSimList.setAdapter(adapter);

        binding.btnGrantPhonePerm.setOnClickListener(v ->
                readPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE));

        ((MainActivity) requireActivity()).userName.observe(getViewLifecycleOwner(), name -> {
            SpannableStringBuilder styledGreeting = new SpannableStringBuilder(getText(R.string.tv_greeting_home_txt));
            styledGreeting.append("\n");
            styledGreeting.append(name);
            styledGreeting.append(",\n\n");
            styledGreeting.append(getText(R.string.tv_sim_summary_txt));
            binding.tvGreetingHomeTxt.setText(styledGreeting);
        });

//         code to test smart details fragment
//        binding.btnOpenSmart.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(SimViewHolder.KEY_SIM, new SmartCell());
//            Navigation.findNavController(requireView()).navigate(R.id.nav_smart_detail, bundle);
//        });

        adapter.registerAdapterDataObserver(simListObv);

        // loads the banner ads
        binding.adViewHome.loadAd(new AdRequest.Builder().build());

        // request phone state permission if required
//        handleReadPhoneStatePermission();

        // observe the sim list livedata to update the UI
        viewModel.getLiveSimList().observe(getViewLifecycleOwner(), sims -> {
            // submit the new list to the list adapter which is used to populate the recycler view
            adapter.submitList(sims);
            if (sims == null || sims.isEmpty() || binding == null) {
                return;
            }
            // shows the intro view when there is at least one SIM card shown
            binding.rvSimList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (binding == null) {
                        return;
                    }
                    binding.rvSimList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (materialIntroView == null)
                        materialIntroView = Utils.getIntroView(binding.rvSimList.getChildAt(0), R.string.intro_home_txt);
                    if (materialIntroView.getParent() == null)
                        materialIntroView.show(requireActivity());
                }
            });
        });

        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_PHONE_STATE)) {
            viewModel.querySimCardDetails();
        }
    }

//    private void handleReadPhoneStatePermission() {
//        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_PHONE_STATE)) {
//            viewModel.querySimCardDetails();
//        } else if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_PHONE_STATE)) {
//            new MaterialAlertDialogBuilder(requireContext())
//                    .setTitle(getString(R.string.perm_dialog_title))
//                    .setMessage(getString(R.string.perm_read_phone_state_msg))
//                    .setCancelable(true)
//                    .setPositiveButton(getString(R.string.positive_dialog_btn_txt), (dialog, which) -> {
//                        readPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
//                        dialog.dismiss();
//                    })
//                    .setNegativeButton(getString(R.string.negative_dialog_btn_txt), (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
//            readPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
//        }
//    }

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
        if (materialIntroView != null && materialIntroView.getParent() != null) {
            materialIntroView.dismiss();
        }
        binding = null;
        super.onDestroyView();
    }
}