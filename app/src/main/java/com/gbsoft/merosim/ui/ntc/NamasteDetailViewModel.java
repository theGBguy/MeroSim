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

package com.gbsoft.merosim.ui.ntc;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data_source.PrefsUtils;
import com.gbsoft.merosim.model.Namaste;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.ui.BaseTelecomViewModel;
import com.gbsoft.merosim.utils.Event;
import com.gbsoft.merosim.utils.Validator;

// view model for Namaste details fragment
public class NamasteDetailViewModel extends BaseTelecomViewModel {
    public final MutableLiveData<String> securityCode = new MutableLiveData<>();
    public final MutableLiveData<String> recipient = new MutableLiveData<>();
    public final MutableLiveData<String> amount = new MutableLiveData<>();
    public final MutableLiveData<String> oldPhone = new MutableLiveData<>();
    public final MutableLiveData<String> newPhone = new MutableLiveData<>();
    public final MutableLiveData<String> deletePhone = new MutableLiveData<>();
    public final MutableLiveData<String> songCode = new MutableLiveData<>();

    private final MutableLiveData<Integer> errorSecurityCode = new MutableLiveData<>();

    public NamasteDetailViewModel(@NonNull Application app) {
        super(app);
    }

    public void init(Bundle args) {
        super.init(args);
        customerCare.setValue(Namaste.CUSTOMER_CARE_NO);
        securityCode.setValue(PrefsUtils.getSecurityCode(getAppContext()));
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

    public void saveSecurityCode() {
        String code = securityCode.getValue();
        if (code == null || code.isEmpty()) {
            snackMsg.setValue(new Event<>(R.string.error_security_code));
            return;
        }

        PrefsUtils.saveSecurityCode(getAppContext(), code);
    }

    public LiveData<Integer> getErrorSecurityCode() {
        return errorSecurityCode;
    }
}