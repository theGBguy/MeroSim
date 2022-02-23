/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/10/29
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

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Observer for RecyclerView to show empty state if there is empty data set
public class RecyclerViewEmptyObserver extends RecyclerView.AdapterDataObserver {
    private final RecyclerView recyclerView;
    private final TextView emptyTextView;

    public RecyclerViewEmptyObserver(RecyclerView recyclerView, TextView emptyTextView) {
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
