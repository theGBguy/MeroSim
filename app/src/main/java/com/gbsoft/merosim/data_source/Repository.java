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

package com.gbsoft.merosim.data_source;

import android.content.Context;
import android.os.Handler;

import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.telephony.TelephonyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/*
 * Nearly single source of truth for the data in this app. Some classes respect
 * that principle and call the repo's method meanwhile some directly call the
 * utils methods for convenience. Initially, author was inspired to create this
 * class as he was learning MVVM but then later on, the author find no real
 * advantage of using this class as most of the methods here just relay the
 * method calls and do nothing.
 */

public class Repository {
    private final ExecutorService executor;
    private final Handler mainThreadHandler;

    public Repository(ExecutorService executor, Handler mainThreadHandler) {
        this.executor = executor;
        this.mainThreadHandler = mainThreadHandler;
    }

    public void querySimCardDetails(Context context, RepoCallback<List<Sim>> callback) {
        executor.execute(() -> {
            // clear all cached data if sim card change is detected
            fixIfSimCardsIsChanged(context);
            List<Sim> simList = retrieveCachedSimDetails(context);
            mainThreadHandler.post(() -> callback.onComplete(new Result.Success<>(simList)));
        });
    }

    private List<Sim> retrieveCachedSimDetails(Context context) {
        TelephonyUtils utils = TelephonyUtils.getInstance(context);
        List<Sim> simList = new ArrayList<>();
        if (utils != null)
            simList = utils.getSimList();
        for (Sim sim : simList) {
            List<String> simDetails = PrefsUtils.retrieveSimDetails(context, sim.getSimSlotIndex());
            sim.setPhoneNo(simDetails.get(0));
            sim.setBalance(simDetails.get(1));
            sim.setSimOwner(simDetails.get(2));
        }
        return simList;
    }

    private void fixIfSimCardsIsChanged(Context context) {
        TelephonyUtils utils = TelephonyUtils.getInstance(context);
        List<Sim> simList = new ArrayList<>();
        if (utils != null)
            simList = utils.getSimList();
        for (Sim sim : simList) {
            String operator = PrefsUtils.getOperator(context, sim.getSimSlotIndex());
            if (operator.equals(sim.getName())) {
                continue;
            }
            if (operator.equals(Sim.UNAVAILABLE)) {
                PrefsUtils.saveOperator(context, sim.getSimSlotIndex(), sim.getName());
                continue;
            }
            PrefsUtils.getDefaultSharedPrefs(context).edit().clear().commit();
            break;
        }
    }

    public static int getSimSlotIndex(Context context, String simName) {
        TelephonyUtils utils = TelephonyUtils.getInstance(context);
        if (utils == null) {
            return -1;
        }
        return utils.getSimSlotIndex(simName);
    }

}
