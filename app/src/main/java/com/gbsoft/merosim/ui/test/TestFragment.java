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

package com.gbsoft.merosim.ui.test;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Namaste;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.databinding.FragmentTestBinding;
import com.gbsoft.merosim.ui.home.SimViewHolder;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.TelephonyUtils;

public class TestFragment extends Fragment {
    private FragmentTestBinding binding;
    private TelephonyUtils telephonyUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Sim dummy = new Sim("Dummy", "9876543210", "100", "Dummy Owner", 1);
        Bundle args = new Bundle();
        args.putParcelable(SimViewHolder.KEY_SIM, dummy);

        binding.btnNtc.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_namaste_detail, args));
        binding.btnNcell.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_ncell_detail, args));
        binding.btnSmart.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_smart_detail, args));

        telephonyUtils = new TelephonyUtils(requireContext());
        binding.btnTest.setOnClickListener(v ->
                telephonyUtils.sendUssdRequest(Namaste.USSD_BALANCE, TelephonyUtils.TYPE_NORMAL, 0, callback));
    }

    private UssdResponseCallback callback = new UssdResponseCallback() {
        @Override
        public void onReceiveUssdResponseCancelled(TelephonyManager telephonyManager, String request, String cancellationMsg) {
            super.onReceiveUssdResponseCancelled(telephonyManager, request, cancellationMsg);
            SnackUtils.showMessage(requireView(), cancellationMsg);
        }

        @Override
        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
            super.onReceiveUssdResponse(telephonyManager, request, response);
            binding.tvResponse.setText(TelephonyUtils.getBalanceText(response.toString()));
        }

        @Override
        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
            SnackUtils.showMessage(requireView(), R.string.ussd_failed_snack_msg, failureCode);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        callback = null;
    }
}