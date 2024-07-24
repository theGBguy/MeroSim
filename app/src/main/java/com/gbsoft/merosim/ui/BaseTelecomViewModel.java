/*
 * Created by Chiranjeevi Pandey on 3/10/22, 3:34 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/03/10
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

package com.gbsoft.merosim.ui;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data_source.PrefsUtils;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.ui.home.SimViewHolder;
import com.gbsoft.merosim.utils.Event;

public class BaseTelecomViewModel extends AndroidViewModel {
    protected final MediatorLiveData<String> phone = new MediatorLiveData<>();
    protected final MediatorLiveData<String> balance = new MediatorLiveData<>();
    protected final MediatorLiveData<String> simOwner = new MediatorLiveData<>();
    protected final MutableLiveData<String> customerCare = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> isIntuitiveModeOn = new MutableLiveData<>();

    public final MutableLiveData<String> recipient = new MutableLiveData<>();
    public final MutableLiveData<String> amount = new MutableLiveData<>();

    protected final MutableLiveData<Integer> errorRecipient = new MutableLiveData<>();
    protected final MutableLiveData<Integer> errorAmount = new MutableLiveData<>();

    protected final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    protected Sim sim;

    protected BaseTelecomViewModel(@NonNull Application app) {
        super(app);
    }

    public void init(Bundle args) {
        if (args == null) return;
        sim = args.getParcelable(SimViewHolder.KEY_SIM);
        if (sim == null) return;

        // initializes with cached data
        phone.setValue(sim.getPhoneNo());
        balance.setValue(sim.getBalance());
        simOwner.setValue(sim.getSimOwner());

        // observes the change in intuitive mode
        phone.addSource(isIntuitiveModeOn, modeOn -> {
            if (modeOn) {
                phone.setValue(PrefsUtils.getPhoneNumber(getAppContext(), sim.getSimSlotIndex()));
            } else {
                phone.setValue(getAppContext().getString(R.string.phone_txt));
            }
        });
        balance.addSource(isIntuitiveModeOn, modeOn -> {
            if (modeOn) {
                balance.setValue(PrefsUtils.getBalance(getAppContext(), sim.getSimSlotIndex()));
            } else {
                balance.setValue(getAppContext().getString(R.string.balance_txt));
            }
        });
        simOwner.addSource(isIntuitiveModeOn, modeOn -> {
            if (modeOn) {
                simOwner.setValue(PrefsUtils.getSimOwner(getAppContext(), sim.getSimSlotIndex()));
            } else {
                simOwner.setValue(getAppContext().getString(R.string.sim_owner_txt));
            }
        });

        isIntuitiveModeOn.setValue(PrefsUtils.isIntuitiveModeTurnedOn(getAppContext()));
    }

    public LiveData<Boolean> isIntuitiveModeOn() {
        return isIntuitiveModeOn;
    }

    public void setIntuitiveModeStatus(boolean status) {
        isIntuitiveModeOn.setValue(status);
    }

    public String getUserName() {
        return PrefsUtils.getUserName(getAppContext());
    }

    public boolean isUserNameDifferent() {
        String cachedUserName = getUserName();
        if (TextUtils.isEmpty(cachedUserName) || TextUtils.equals(cachedUserName, PrefsUtils.NAME_UNKNOWN)) {
            return false;
        }
        return !cachedUserName.equalsIgnoreCase(simOwner.getValue());
    }

    public int getSimSlotIndex() {
        return sim.getSimSlotIndex();
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null) return;

        this.phone.setValue(phone);
        PrefsUtils.savePhone(getAppContext(), sim.getSimSlotIndex(), phone);
    }

    public LiveData<String> getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        if (balance == null) return;

        this.balance.setValue(balance);
        PrefsUtils.saveBalance(getAppContext(), sim.getSimSlotIndex(), balance);
    }

    public LiveData<String> getSimOwner() {
        return simOwner;
    }

    public void setSimOwner(String simOwner) {
        if (simOwner == null) return;

        this.simOwner.setValue(simOwner);
        PrefsUtils.saveSimOwner(getAppContext(), sim.getSimSlotIndex(), simOwner);
    }

    public LiveData<String> getCustomerCare() {
        return customerCare;
    }

    public LiveData<Integer> getErrorRecipient() {
        return errorRecipient;
    }

    public LiveData<Integer> getErrorAmount() {
        return errorAmount;
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    public void setSnackMsg(@StringRes int res) {
        snackMsg.setValue(new Event<>(res));
    }

    protected Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}
