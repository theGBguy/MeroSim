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

import android.widget.ViewSwitcher;

import androidx.recyclerview.widget.RecyclerView;

// Observer for RecyclerView to show empty state if there is empty data set
public class RecyclerViewEmptyObserver extends RecyclerView.AdapterDataObserver {
    private final RecyclerView recyclerView;
    private final ViewSwitcher viewSwitcher;
    private boolean isCurrentlyEmpty = true;

    public RecyclerViewEmptyObserver(RecyclerView recyclerView, ViewSwitcher viewSwitcher) {
        this.recyclerView = recyclerView;
        this.viewSwitcher = viewSwitcher;
//        onDataChanged();
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
        // if the current state is same as new state, return early
        if (isCurrentlyEmpty == isEmpty) {
            return;
        }
        isCurrentlyEmpty = isEmpty;
        viewSwitcher.reset();
        if (isEmpty) {
            return;
        }
        viewSwitcher.showNext();
    }
}
