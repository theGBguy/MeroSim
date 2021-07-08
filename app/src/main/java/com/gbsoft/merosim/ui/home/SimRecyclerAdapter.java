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

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.merosim.data.Sim;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimRecyclerAdapter extends RecyclerView.Adapter<SimViewHolder> {
    private final List<Sim> simList;

    public SimRecyclerAdapter(List<Sim> simList) {
        this.simList = simList;
    }

    @NonNull
    @Override
    public SimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SimViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SimViewHolder holder, int position) {
        holder.bind(simList.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SimViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public int getItemCount() {
        return simList.size();
    }

    @Override
    public void registerAdapterDataObserver(@NonNull @NotNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }
}
