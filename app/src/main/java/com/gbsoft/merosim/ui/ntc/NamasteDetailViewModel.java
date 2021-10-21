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

package com.gbsoft.merosim.ui.ntc;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Namaste;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.intermediaries.Repository;
import com.gbsoft.merosim.ui.home.SimViewHolder;
import com.gbsoft.merosim.utils.Event;
import com.gbsoft.merosim.utils.Validator;

public class NamasteDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<String> phone = new MutableLiveData<>(Sim.UNAVAILABLE);
    private final MutableLiveData<String> balance = new MutableLiveData<>(Sim.UNAVAILABLE);
    private final MutableLiveData<String> simOwner = new MutableLiveData<>(Sim.UNAVAILABLE);
    private final MutableLiveData<String> customerCare = new MutableLiveData<>(Namaste.CUSTOMER_CARE_NO);

    public final MutableLiveData<String> securityCode = new MutableLiveData<>();
    public final MutableLiveData<String> recipient = new MutableLiveData<>();
    public final MutableLiveData<String> amount = new MutableLiveData<>();
    public final MutableLiveData<String> oldPhone = new MutableLiveData<>();
    public final MutableLiveData<String> newPhone = new MutableLiveData<>();
    public final MutableLiveData<String> deletePhone = new MutableLiveData<>();
    public final MutableLiveData<String> songCode = new MutableLiveData<>();

    private final MutableLiveData<Integer> errorSecurityCode = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorRecipient = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorAmount = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    private final Repository repository;
    private Sim sim;

    public NamasteDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public void init(Bundle args) {
        if (args == null) return;
        sim = args.getParcelable(SimViewHolder.KEY_SIM);
        if (sim == null) return;
        phone.setValue(sim.getPhoneNo());
        balance.setValue(sim.getBalance());
        simOwner.setValue(sim.getSimOwner());
    }


    int getSimSlotIndex() {
        return sim.getSimSlotIndex();
    }

    boolean isTransferDataInvalid() {
        if (!Validator.isSecurityCodeValid(securityCode.getValue())) {
            errorSecurityCode.setValue(R.string.error_security_code);
            return true;
        }
        errorSecurityCode.setValue(null);
        if (!Validator.isPhoneNumberValid(Sim.NAMASTE, recipient.getValue())) {
            errorRecipient.setValue(R.string.ntc_error_recipient);
            return true;
        }
        errorRecipient.setValue(null);
        if (!Validator.isAmountValid(Sim.NAMASTE, amount.getValue())) {
            errorAmount.setValue(R.string.ntc_error_amount);
            return true;
        }
        errorAmount.setValue(null);
        return false;
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null)
            this.phone.setValue(this.phone.getValue());
        else {
            this.phone.setValue(phone);
            repository.savePhone(getAppContext(), sim.getSimSlotIndex(), phone);
        }
    }

    public LiveData<String> getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        if (balance == null)
            this.balance.setValue(this.balance.getValue());
        else {
            this.balance.setValue(balance);
            repository.saveBalance(getAppContext(), sim.getSimSlotIndex(), balance);
        }
    }

    public LiveData<String> getSimOwner() {
        return simOwner;
    }

    public void setSimOwner(String simOwner) {
        if (simOwner == null)
            this.simOwner.setValue(this.simOwner.getValue());
        else {
            this.simOwner.setValue(simOwner);
            repository.saveSimOwner(getAppContext(), sim.getSimSlotIndex(), simOwner);
        }
    }

    public LiveData<String> getCustomerCare() {
        return customerCare;
    }

    public LiveData<Integer> getErrorSecurityCode() {
        return errorSecurityCode;
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

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}