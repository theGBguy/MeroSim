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

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gbsoft.merosim.data.Sim;

import java.util.List;

public class SimRecyclerAdapter extends ListAdapter<Sim, SimViewHolder> {
    private final RecyclerViewEmptyObserver emptyObserver;

    private static final DiffUtil.ItemCallback<Sim> diffCallback = new DiffUtil.ItemCallback<Sim>() {
        @Override
        public boolean areItemsTheSame(@NonNull Sim oldSim, @NonNull Sim newSim) {
            return TextUtils.equals(oldSim.getPhoneNo(), newSim.getPhoneNo());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Sim oldItem, @NonNull Sim newItem) {
            return oldItem.equals(newItem);
        }
    };

    public SimRecyclerAdapter(RecyclerViewEmptyObserver emptyObserver) {
        super(diffCallback);
        this.emptyObserver = emptyObserver;
    }

    @NonNull
    @Override
    public SimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SimViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SimViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SimViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onCurrentListChanged(@NonNull List<Sim> previousList, @NonNull List<Sim> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        if (!currentList.isEmpty())
            emptyObserver.onChanged();
    }
}
