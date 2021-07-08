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

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private final RecyclerView recyclerView;
    private final TextView emptyTextView;

    public RVEmptyObserver(RecyclerView recyclerView, TextView emptyTextView) {
        this.recyclerView = recyclerView;
        this.emptyTextView = emptyTextView;
        onDataChanged();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        onDataChanged();
    }

    private void onDataChanged() {
        SimRecyclerAdapter adapter = (SimRecyclerAdapter) recyclerView.getAdapter();
        if (adapter == null) return;
        setVisibility(adapter.getItemCount() == 0);
    }

    private void setVisibility(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
