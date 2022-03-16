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
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.model.SmartCell;
import com.gbsoft.merosim.ui.BaseTelecomViewModel;
import com.gbsoft.merosim.utils.Validator;

// view model for Smart details fragment
public class SmartDetailViewModel extends BaseTelecomViewModel {
    public SmartDetailViewModel(@NonNull Application app) {
        super(app);
    }

    public void init(Bundle args) {
        super.init(args);
        customerCare.setValue(SmartCell.CUSTOMER_CARE_NO);
    }

    boolean isTransferDataInvalid() {
        if (!Validator.isPhoneNumberValid(Sim.SMART_CELL, recipient.getValue())) {
            errorRecipient.setValue(R.string.smart_error_recipient);
            return true;
        }
        errorRecipient.setValue(null);
        if (!Validator.isAmountValid(Sim.SMART_CELL, amount.getValue())) {
            errorAmount.setValue(R.string.smart_error_amount);
            return true;
        }
        errorAmount.setValue(null);
        return false;
    }
}