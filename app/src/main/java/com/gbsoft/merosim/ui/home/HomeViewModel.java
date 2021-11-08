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
 * Last modified: 2021/10/28
 */

package com.gbsoft.merosim.ui.home;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.intermediaries.Repository;
import com.gbsoft.merosim.intermediaries.Result;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final Repository repository;
    private final MutableLiveData<List<Sim>> liveSimList = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application app) {
        super(app);
        repository = new Repository(
                ((MeroSimApp) app).getExecutor(),
                ((MeroSimApp) app).getMainThreadHandler()
        );
    }

    public void initializeSimCardDetails(){

    }

    public void querySimCardDetails() {
        repository.querySimCardDetails(getAppContext(), result -> {
            if (result instanceof Result.Success)
                liveSimList.setValue(((Result.Success<List<Sim>>) result).data);
            else
                liveSimList.setValue(new ArrayList<>());
        });
    }

    public LiveData<List<Sim>> getLiveSimList() {
        return liveSimList;
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}