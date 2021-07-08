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

package com.gbsoft.merosim.ui.ncell;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Ncell;
import com.gbsoft.merosim.ui.LoadingTextView;
import com.gbsoft.merosim.utils.TelephonyUtils;
import com.gbsoft.merosim.utils.Utils;

import java.util.Locale;

public class NcellEventHandler extends UssdResponseCallback {
    private final NcellDetailViewModel vm;
    private final TelephonyUtils telephonyUtils;

    public NcellEventHandler(Context context, @NonNull NcellDetailViewModel vm) {
        this.vm = vm;
        this.telephonyUtils = new TelephonyUtils(context);
    }

    public void onPhoneRefreshClick(View view) {
        telephonyUtils.sendUssdRequest(Ncell.USSD_SELF, TelephonyUtils.TYPE_INPUT, vm.getSimSlotIndex(), this);
        ((LoadingTextView) view).resetLoader();
    }

    public void onBalanceRefreshClick(View view) {
        telephonyUtils.sendUssdRequest(Ncell.USSD_BALANCE, TelephonyUtils.TYPE_INPUT, vm.getSimSlotIndex(), this);
        ((LoadingTextView) view).resetLoader();
    }

    public void onSimOwnerRefreshClick(View view) {
        telephonyUtils.sendUssdRequest(Ncell.USSD_SIM_OWNER, TelephonyUtils.TYPE_INPUT, vm.getSimSlotIndex(), this);
    }

    public void onCustomerCareClick(View view) {
        telephonyUtils.call(vm.getCustomerCare().getValue(), vm.getSimSlotIndex());
    }

    public void onBalanceTransferInfoClick(View view) {
        Utils.showPopup(view, R.string.ntc_balance_transfer_info);
    }

    public void onBalanceTransferClick(View view) {
        if (vm.isDataInvalid()) return;
        telephonyUtils.sendUssdRequest(
                String.format(Locale.getDefault(), Ncell.USSD_BALANCE_TRANSFER,
                        vm.recipient.getValue(), vm.amount.getValue()),
                TelephonyUtils.TYPE_NORMAL,
                vm.getSimSlotIndex(),
                this

        );
    }

    public void onNcellCreditInfoClick(View view) {
        Utils.showPopup(view, R.string.ntc_credit_info);
    }

    @Override
    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
        switch (request) {
            case Ncell.USSD_SELF:
                vm.setPhone(TelephonyUtils.getPhoneText(response.toString()));
                break;
            case Ncell.USSD_BALANCE:
                vm.setBalance(TelephonyUtils.getBalanceText(response.toString()));
                break;
            case Ncell.USSD_SIM_OWNER:
                vm.setSimOwner(TelephonyUtils.getSimOwnerText(response.toString()));
                break;
            case Ncell.USSD_BALANCE_TRANSFER:
                vm.setSnackMsg(R.string.balance_transfer_snack_msg);
                break;
        }
        super.onReceiveUssdResponse(telephonyManager, request, response);
    }

    @Override
    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
        switch (request) {
            case Ncell.USSD_SELF:
                vm.setPhone(null);
                break;
            case Ncell.USSD_BALANCE:
                vm.setBalance(null);
                break;
            case Ncell.USSD_SIM_OWNER:
                vm.setSimOwner(null);
                break;
            case Ncell.USSD_BALANCE_TRANSFER:
                break;
        }
        vm.setSnackMsg(R.string.ussd_failed_snack_msg);
        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
    }

}
