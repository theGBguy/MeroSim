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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.databinding.SimRowBinding;
import com.gbsoft.merosim.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class SimViewHolder extends RecyclerView.ViewHolder {
    public static final String KEY_SIM = "sim";

    private SimRowBinding binding;
    private final Resources res;

    public SimViewHolder(@NotNull SimRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.res = binding.getRoot().getResources();
    }

    void bind(Sim model) {
        String simName = model.getName();
        int destinationId = -1;
        Bundle args = new Bundle();
        args.putParcelable(KEY_SIM, model);

        switch (simName) {
            case Sim.NAMASTE:
                binding.cardSim.setCardBackgroundColor(Utils.getSimColor(res, 0));
                binding.ivSimLogo.setImageResource(R.drawable.ntc);
                binding.ivSimLogo.setImageTintList(ColorStateList.valueOf(res.getColor(R.color.color_ntc_tint, null)));
                destinationId = R.id.nav_namaste_detail;
                break;
            case Sim.NCELL:
                binding.cardSim.setCardBackgroundColor(Utils.getSimColor(res, 1));
                binding.ivSimLogo.setImageResource(R.drawable.ic_ncell);
                destinationId = R.id.nav_ncell_detail;
                break;
            case Sim.SMART_CELL:
                binding.cardSim.setCardBackgroundColor(Utils.getSimColor(res, 2));
                binding.ivSimLogo.setImageResource(R.drawable.smart);
                destinationId = R.id.nav_smart_detail;
                break;
        }
        binding.cardSim.setOnClickListener(Navigation.createNavigateOnClickListener(destinationId, args));
        binding.tvPhoneNo.setText(model.getPhoneNo());
        binding.tvBalance.setText(model.getBalance());
    }

    static SimViewHolder create(ViewGroup parent) {
        return new SimViewHolder(SimRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    void unbind() {
        binding = null;
    }
}
