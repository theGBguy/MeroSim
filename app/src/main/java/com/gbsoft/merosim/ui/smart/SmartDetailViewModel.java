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

package com.gbsoft.merosim.ui.smart;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.model.SmartCell;
import com.gbsoft.merosim.data_source.Repository;
import com.gbsoft.merosim.ui.home.SimViewHolder;
import com.gbsoft.merosim.utils.Event;
import com.gbsoft.merosim.utils.Validator;

// view model for Smart details fragment
public class SmartDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<String> phone = new MutableLiveData<>(Sim.UNAVAILABLE);
    private final MutableLiveData<String> balance = new MutableLiveData<>(Sim.UNAVAILABLE);
    private final MutableLiveData<String> customerCare = new MutableLiveData<>(SmartCell.CUSTOMER_CARE_NO);

    public final MutableLiveData<String> recipient = new MutableLiveData<>();
    public final MutableLiveData<String> amount = new MutableLiveData<>();

    private final MutableLiveData<Integer> errorRecipient = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    private final Repository repository;
    private Sim sim;

    public SmartDetailViewModel(@NonNull Application app) {
        super(app);
        repository = new Repository(
                ((MeroSimApp) app).getExecutor(),
                ((MeroSimApp) app).getMainThreadHandler()
        );
    }

    public void init(Bundle args) {
        if (args == null) return;
        sim = args.getParcelable(SimViewHolder.KEY_SIM);
        if (sim == null) return;

        phone.setValue(sim.getPhoneNo());
        balance.setValue(sim.getBalance());
    }

    public boolean shouldUseOverlay() {
        return repository.shouldUseOverlay(getAppContext());
    }

    int getSimSlotIndex() {
        return sim.getSimSlotIndex();
    }

    boolean isDataInvalid() {
        if (!Validator.isPhoneNumberValid(Sim.SMART_CELL, recipient.getValue())) {
            errorRecipient.setValue(R.string.smart_error_recipient);
            return true;
        }
        errorRecipient.setValue(null);
        return false;
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null) return;

        this.phone.setValue(phone);
        repository.savePhone(getAppContext(), sim.getSimSlotIndex(), phone);
    }

    public LiveData<String> getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        if (balance == null) return;

        this.balance.setValue(balance);
        repository.saveBalance(getAppContext(), sim.getSimSlotIndex(), balance);
    }

    public LiveData<String> getCustomerCare() {
        return customerCare;
    }

    public LiveData<Integer> getErrorRecipient() {
        return errorRecipient;
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    public void setSnackMsg(@StringRes int res) {
        snackMsg.setValue(new Event<>(res));
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}