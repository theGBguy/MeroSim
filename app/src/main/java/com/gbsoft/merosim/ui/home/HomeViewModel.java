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

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.data_source.Repository;
import com.gbsoft.merosim.data_source.Result;
import com.gbsoft.merosim.model.Sim;

import java.util.ArrayList;
import java.util.List;

// ViewModel for Home Fragment
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

    public boolean shouldAskUserName() {
        return TextUtils.isEmpty(repository.getUserName(getAppContext()));
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