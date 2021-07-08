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

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.intermediaries.Repository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final Repository repository;

    public HomeViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = new Repository();
    }

    public List<Sim> getSimList() {
        return repository.getSimList(getAppContext());
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}