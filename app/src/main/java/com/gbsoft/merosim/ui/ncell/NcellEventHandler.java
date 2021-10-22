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
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.utils.TelephonyUtils;
import com.gbsoft.merosim.utils.Utils;

import java.util.Locale;

public class NcellEventHandler extends UssdResponseCallback {
    private final NcellDetailViewModel vm;
    private final TelephonyUtils telephonyUtils;
    private final PermissionFixerContract fixerContract;

    public NcellEventHandler(Context context, @NonNull NcellDetailViewModel vm, PermissionFixerContract fixerContract) {
        this.vm = vm;
        this.fixerContract = fixerContract;
        this.telephonyUtils = new TelephonyUtils(context);
    }

    public void onPhoneRefreshClick(View view) {
        makeUSSDRequestWithOverlay(Ncell.USSD_SELF);
    }

    public void onBalanceRefreshClick(View view) {
        makeUSSDRequestWithOverlay(Ncell.USSD_BALANCE);
    }

    public void onSimOwnerRefreshClick(View view) {
        makeUSSDRequestWithOverlay(Ncell.USSD_SIM_OWNER);
    }

    public void onCustomerCareClick(View view) {
        telephonyUtils.dial(vm.getCustomerCare().getValue());
    }

    public void onBalanceTransferInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_transfer_balance_info);
    }

    // sapati, my5, mcn, prbt, low balance

    public void onSapatiInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_sapati_info);
    }

    public void onMy5InfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_my5_info);
    }

    public void onMCNInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_mcn_info);
    }

    public void onPRBTInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_prbt_info);
    }

    public void onLowBalanceCallInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ncell_low_balance_call_info);
    }

    public void onBalanceTransferClick(View view) {
        if (vm.isTransferDataInvalid()) return;
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_BALANCE_TRANSFER,
                vm.recipient.getValue(), vm.amount.getValue()));
    }

    public void onBtnTakeSapatiClick(View view) {
        makeUSSDRequestWithoutOverlay(Ncell.USSD_SAPATI);
    }

    public void onBtnMy5ActivateClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 1));
    }

    public void onBtnMy5AddNumberClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 2));
    }

    public void onBtnMy5ModifyNumberClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 3));
    }

    public void onBtnMy5DeleteNumberClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 4));
    }

    public void onBtnMy5QueryNumberClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 5));
    }

    public void onBtnMy5DeactivateClick(View view) {
        makeUSSDRequestWithoutOverlay(String.format(Locale.getDefault(), Ncell.USSD_MY5, 6));
    }

    public void onBtnMCNActivateClick(View view) {
        makeUSSDRequestWithoutOverlay(Ncell.USSD_MCN_ACTIVATE);
    }

    public void onBtnMCNDeactivateClick(View view) {
        makeUSSDRequestWithoutOverlay(Ncell.USSD_MCN_DEACTIVATE);
    }

    public void onBtnPRBTActivateClick(View view) {
        makeUSSDRequestWithoutOverlay("*" + Ncell.USSD_PRBT + "#");
    }

    public void onBtnPRBTDeactivateClick(View view) {
        telephonyUtils.sendSms(Ncell.USSD_PRBT, "R", fixerContract);
    }

    public void onBtnMakeLowBalanceCallClick(View view) {
        if (vm.isLowBalanceNumberInvalid()) return;
        telephonyUtils.call(
                String.format(Locale.getDefault(),
                        Ncell.LOW_BALANCE_CALL,
                        vm.lowBalanceCallNo.getValue()),
                vm.getSimSlotIndex(),
                fixerContract
        );
    }

    private void makeUSSDRequestWithoutOverlay(String ussdRequest) {
        telephonyUtils.sendUssdRequestWithoutOverlay(
                ussdRequest,
                TelephonyUtils.TYPE_INPUT,
                vm.getSimSlotIndex(),
                this,
                fixerContract
        );
    }

    private void makeUSSDRequestWithOverlay(String ussdRequest) {
        telephonyUtils.sendUssdRequestWithOverlay(
                ussdRequest,
                TelephonyUtils.TYPE_INPUT,
                vm.getSimSlotIndex(),
                this,
                fixerContract
        );
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

    @Override
    public void onReceiveUssdResponseCancelled(TelephonyManager telephonyManager, String request, String cancellationMsg) {
        super.onReceiveUssdResponseCancelled(telephonyManager, request, cancellationMsg);
        vm.setSnackMsg(R.string.ussd_response_cancelled_msg);
    }

}
