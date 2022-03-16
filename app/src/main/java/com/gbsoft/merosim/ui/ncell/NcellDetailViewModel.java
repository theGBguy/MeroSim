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

package com.gbsoft.merosim.ui.ncell;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.model.Ncell;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.ui.BaseTelecomViewModel;
import com.gbsoft.merosim.utils.Validator;

// view model for Ncell details fragment
public class NcellDetailViewModel extends BaseTelecomViewModel {
    public final MutableLiveData<String> lowBalanceCallNo = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorLowBalanceCallNo = new MutableLiveData<>();

    public NcellDetailViewModel(@NonNull Application app) {
        super(app);
    }

    public void init(Bundle args) {
        super.init(args);
        customerCare.setValue(Ncell.CUSTOMER_CARE_NO);
    }

    boolean isTransferDataInvalid() {
        if (!Validator.isPhoneNumberValid(Sim.NCELL, recipient.getValue())) {
            errorRecipient.setValue(R.string.ncell_error_recipient);
            return true;
        }
        errorRecipient.setValue(null);
        if (!Validator.isAmountValid(Sim.NCELL, amount.getValue())) {
            errorAmount.setValue(R.string.ncell_error_amount);
            return true;
        }
        errorAmount.setValue(null);
        return false;
    }

    boolean isLowBalanceNumberInvalid() {
        if (!Validator.isPhoneNumberValid(Sim.NCELL, lowBalanceCallNo.getValue())) {
            errorLowBalanceCallNo.setValue(R.string.ncell_error_recipient);
            return true;
        }
        errorLowBalanceCallNo.setValue(null);
        return false;
    }

    public LiveData<Integer> getErrorLowBalanceCallNo() {
        return errorLowBalanceCallNo;
    }
}