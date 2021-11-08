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
 * Last modified: 2021/10/28
 */

package com.gbsoft.merosim.ui.smart;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.SmartCell;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.TelephonyUtils;
import com.gbsoft.merosim.utils.Utils;

import java.util.Locale;

public class SmartEventHandler extends UssdResponseCallback {
    private final Context context;
    private final SmartDetailViewModel vm;
    private final TelephonyUtils telephonyUtils;
    private final PermissionFixerContract fixerContract;

    public SmartEventHandler(Context context, @NonNull SmartDetailViewModel vm, PermissionFixerContract fixerContract) {
        this.context = context;
        this.vm = vm;
        this.fixerContract = fixerContract;
        this.telephonyUtils = TelephonyUtils.getInstance(context);
    }

    public void onPhoneRefreshClick(View view) {
        makeUSSDRequest(SmartCell.USSD_SELF, true);
    }

    public void onBalanceRefreshClick(View view) {
        makeUSSDRequest(SmartCell.USSD_BALANCE, true);
    }

    public void onSimOwnerRefreshClick(View view) {
        SnackUtils.showMessage(view, R.string.smart_sim_owner_text);
    }

    public void onCustomerCareClick(View view) {
        telephonyUtils.dial(vm.getCustomerCare().getValue());
    }

    public void onBalanceTransferInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.smart_balance_transfer_info);
    }

    public void onLoanInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.smart_loan_info);
    }

    public void onMCAInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.smart_mca_info);
    }

    public void onCRBTInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.smart_crbt_info);
    }

    public void onBalanceTransferClick(View view) {
        if (vm.isDataInvalid()) return;
        makeUSSDRequest(String.format(Locale.getDefault(), SmartCell.USSD_BALANCE_TRANSFER,
                vm.recipient.getValue(), vm.amount.getValue()), false);
    }

    public void onTakeLoanClick(View view) {
        makeUSSDRequest(SmartCell.USSD_LOAN, false);
    }

    public void onMCAActivateClick(View view) {
        telephonyUtils.sendSms(SmartCell.MCA, SmartCell.MCA_SUBSCRIBE, fixerContract);
    }

    public void onMCADeactivateClick(View view) {
        telephonyUtils.sendSms(SmartCell.MCA, SmartCell.MCA_UNSUBSCRIBE, fixerContract);
    }

    public void onCRBTSubscribeClick(View view) {
        makeUSSDRequest(SmartCell.USSD_CRBT_SUB, false);
    }

    public void onCRBTUnsubscribeClick(View view) {
        makeUSSDRequest(SmartCell.USSD_CRBT_UNSUB, false);
    }

    private void makeUSSDRequest(String ussdRequest, boolean withOverlay) {
        if (withOverlay) {
            if (!Utils.isAccessibilityServiceEnabled(context)) {
                fixerContract.fixPermission(BaseTelecomFragment.SERVICE_ACCESSIBILITY);
                return;
            }
            if (!Utils.isOverlayServiceEnabled(context)) {
                fixerContract.fixPermission(BaseTelecomFragment.SERVICE_OVERLAY);
                return;
            }
        }

        telephonyUtils.sendUssdRequest(
                ussdRequest,
                withOverlay,
                TelephonyUtils.TYPE_INPUT,
                vm.getSimSlotIndex(),
                this,
                fixerContract
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
        vm.setSnackMsg(R.string.ussd_failed_snack_msg);
        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
    }

    @Override
    public void onReceiveUssdResponseCancelled(TelephonyManager telephonyManager, String request, String cancellationMsg) {
        super.onReceiveUssdResponseCancelled(telephonyManager, request, cancellationMsg);
        vm.setSnackMsg(R.string.ussd_response_cancelled_msg);
    }
}
