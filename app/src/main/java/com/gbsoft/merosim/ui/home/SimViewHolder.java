/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/23
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

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.databinding.SimRowBinding;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.gbsoft.merosim.utils.Utils;

import org.jetbrains.annotations.NotNull;

// A sim item view holder class which can display a single list item
// of the recycler view.
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
                destinationId = R.id.nav_namaste_detail;
                break;
            case Sim.NCELL:
                binding.cardSim.setCardBackgroundColor(Utils.getSimColor(res, 1));
                destinationId = R.id.nav_ncell_detail;
                break;
            case Sim.SMART_CELL:
                binding.cardSim.setCardBackgroundColor(Utils.getSimColor(res, 2));
                destinationId = R.id.nav_smart_detail;
                break;
        }
        binding.cardSim.setOnClickListener(Navigation.createNavigateOnClickListener(destinationId, args));

        String phoneNo = model.getPhoneNo();
        String balance = model.getBalance();

        if (LocaleUtils.isNepali(itemView.getContext())) {
            phoneNo = LocaleUtils.getNumberInNepaliDigit(phoneNo);
            balance = LocaleUtils.getBalanceInNepali(balance);
        }

        binding.tvPhoneNo.setText(phoneNo);
        binding.tvBalance.setText(balance);
    }

    static SimViewHolder create(ViewGroup parent) {
        return new SimViewHolder(SimRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    void unbind() {
        binding = null;
    }
}
