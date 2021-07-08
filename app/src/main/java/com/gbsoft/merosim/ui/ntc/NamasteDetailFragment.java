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
import com.gbsoft.merosim.databinding.FragmentNamasteDetailBinding;
import com.gbsoft.merosim.ui.ContactsLoader;
import com.gbsoft.merosim.ui.OnContactFoundListener;
import com.gbsoft.merosim.ui.PickPhoneNumber;
import com.gbsoft.merosim.utils.EventObserver;
import com.gbsoft.merosim.utils.SnackUtils;

public class NamasteDetailFragment extends Fragment implements OnContactFoundListener {
    private FragmentNamasteDetailBinding binding;
    private NamasteDetailViewModel viewModel;
    private ContactsLoader loader;

    private final ActivityResultLauncher<Void> contactPicker =
            registerForActivityResult(
                    new PickPhoneNumber(),
                    uri -> loader.restartLoader(LoaderManager.getInstance(NamasteDetailFragment.this), uri)
            );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNamasteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

//    private void startTransition() {
//        PropagatingTransition transition = new PropagatingTransition(
//                (ViewGroup) binding.getRoot(),
//                binding.ntcTvPhone,
//                new TransitionSet().addTransition(new Fade(Fade.IN))
//                        .setInterpolator((Interpolator) input -> (input - 0.5f) * 2)
//                        .addTransition(new Slide(Gravity.BOTTOM)),
//                600,
//                null,
//                null
//        );
//        transition.init();
//        transition.start();
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NamasteDetailViewModel.class);
        binding.setVm(viewModel);
        binding.setEventhandler(new NamasteEventHandler(requireContext(), viewModel));
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loader = new ContactsLoader(requireContext(), this);
        viewModel.init(requireArguments());


        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));

        binding.ntcTilRecipient.setEndIconOnClickListener(v -> contactPicker.launch(null));

//        ViewGroup parent = (ViewGroup) view.getParent();
//        parent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                parent.getViewTreeObserver().removeOnPreDrawListener(this);
//                startTransition();
//                return true;
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
        binding = null;
    }

    @Override
    public void onContactFound(@NonNull String name) {
        binding.ntcTilRecipient.setHelperText(getResources().getString(R.string.tiet_selected_recipient, name));
        new Handler().postDelayed(() -> binding.ntcTilRecipient.setHelperText(null), 3000);
    }
}