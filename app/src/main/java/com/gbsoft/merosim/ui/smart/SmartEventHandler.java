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

package com.gbsoft.merosim.ui.smart;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.SmartCell;
import com.gbsoft.merosim.ui.LoadingTextView;
import com.gbsoft.merosim.utils.TelephonyUtils;
import com.gbsoft.merosim.utils.Utils;

import java.util.Locale;

public class SmartEventHandler extends UssdResponseCallback {
    private final SmartDetailViewModel vm;
    private final TelephonyUtils telephonyUtils;

    public SmartEventHandler(Context context, @NonNull SmartDetailViewModel vm) {
        this.vm = vm;
        this.telephonyUtils = new TelephonyUtils(context);
    }

    public void onPhoneRefreshClick(View view) {
        telephonyUtils.sendUssdRequest(SmartCell.USSD_SELF, TelephonyUtils.TYPE_NORMAL, vm.getSimSlotIndex(), this);
        ((LoadingTextView) view).resetLoader();
    }

    public void onBalanceRefreshClick(View view) {
        telephonyUtils.sendUssdRequest(SmartCell.USSD_BALANCE, TelephonyUtils.TYPE_NORMAL, vm.getSimSlotIndex(), this);
        ((LoadingTextView) view).resetLoader();
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
                String.format(Locale.getDefault(), SmartCell.USSD_BALANCE_TRANSFER,
                        vm.recipient.getValue(), vm.amount.getValue()),
                TelephonyUtils.TYPE_NORMAL,
                vm.getSimSlotIndex(),
                this
        );
    }

    @Override
    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
        switch (request) {
            case SmartCell.USSD_SELF:
                vm.setPhone(TelephonyUtils.getPhoneText(response.toString()));
                break;
            case SmartCell.USSD_BALANCE:
                vm.setBalance(TelephonyUtils.getBalanceText(response.toString()));
                break;
            case SmartCell.USSD_BALANCE_TRANSFER:
                vm.setSnackMsg(R.string.balance_transfer_snack_msg);
                break;
        }
        super.onReceiveUssdResponse(telephonyManager, request, response);
    }

    @Override
    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
        switch (request) {
            case SmartCell.USSD_SELF:
                vm.setPhone(null);
                break;
            case SmartCell.USSD_BALANCE:
                vm.setBalance(null);
                break;
            case SmartCell.USSD_BALANCE_TRANSFER:
                break;
        }
        vm.setSnackMsg(R.string.ussd_failed_snack_msg);
        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
    }
}
