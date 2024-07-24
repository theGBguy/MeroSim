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

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.model.Namaste;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.telephony.TelephonyUtils;
import com.gbsoft.merosim.telephony.USSDResponseCallback;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.utils.SnackUtils;
import com.gbsoft.merosim.utils.Utils;
import com.gbsoft.merosim.utils.Validator;

import java.util.Locale;

// handles most of the events generated in Namaste details fragment screen
public class NamasteEventHandler extends USSDResponseCallback {
    private final Context context;
    private final NamasteDetailViewModel vm;
    private final TelephonyUtils telephonyUtils;
    private final PermissionFixerContract fixerContract;

    public NamasteEventHandler(Context context, @NonNull NamasteDetailViewModel vm, PermissionFixerContract fixerContract) {
        this.context = context;
        this.vm = vm;
        this.fixerContract = fixerContract;
        this.telephonyUtils = TelephonyUtils.getInstance(context);
    }

    public void onTurnOnIntuitiveModeClick(View view) {
        Context context = view.getContext();
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_intuitive), true)
                .apply();
    }

    public void onPhoneRefreshClick(View view) {
        makeUSSDRequest(Namaste.USSD_SELF, vm.isIntuitiveModeOn().getValue());
    }

    public void onBalanceRefreshClick(View view) {
        makeUSSDRequest(Namaste.USSD_BALANCE, vm.isIntuitiveModeOn().getValue());
    }

    public void onSimOwnerRefreshClick(View view) {
        makeUSSDRequest(Namaste.USSD_SIM_OWNER, vm.isIntuitiveModeOn().getValue());
    }

    public void onTakePacksClick(View view) {
        makeUSSDRequest(Namaste.USSD_PACKS, false);
    }

    public void onCheckRemainingPacksClick(View view){
        makeUSSDRequest(Namaste.USSD_REMAINING_PACKS, false);
    }

    public void onCustomerCareClick(View view) {
        telephonyUtils.dial(vm.getCustomerCare().getValue());
    }

    public void onBalanceTransferInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ntc_balance_transfer_info);
    }

    public void onNamasteCreditInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ntc_credit_info);
    }

    public void onFNFInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ntc_fnf_info);
    }

    public void onMCAInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ntc_mca_info);
    }

    public void onCRBTInfoClick(View view) {
        Utils.showInfoDialog(view, R.string.ntc_crbt_info);
    }

    public void onBalanceTransferClick(View view) {
        // checks if the data entered is valid
        if (vm.isTransferDataInvalid()) return;
        makeUSSDRequest(String.format(Locale.getDefault(), Namaste.USSD_BALANCE_TRANSFER,
                vm.securityCode.getValue(), vm.recipient.getValue(), vm.amount.getValue()), false);
    }

    public void onBtnStartClick(View view) {
        telephonyUtils.sendSms(Namaste.NAMASTE_CREDIT_NO, Namaste.START);
    }

    public void onBtnStatusClick(View view) {
        telephonyUtils.sendSms(Namaste.NAMASTE_CREDIT_NO, Namaste.STATUS);
    }

    public void onBtnStopClick(View view) {
        telephonyUtils.sendSms(Namaste.NAMASTE_CREDIT_NO, Namaste.STOP);
    }

    public void onBtnFNFSubscribeClick(View view) {
        String phone = vm.getPhone().getValue();
        if (Validator.isPhoneNumberValid(Sim.NAMASTE, phone))
            telephonyUtils.sendSms(Namaste.NAMASTE_FNF, String.format(Locale.getDefault(), Namaste.FNF_SUB, phone));
        else
            SnackUtils.showMessage(view, "Please refresh the phone number above to subscribe!");
    }

    public void onBtnFNFModifyClick(View view) {
        String oldPhone = vm.oldPhone.getValue();
        String newPhone = vm.newPhone.getValue();
        if (Validator.arePhoneNumbersValid(Sim.NAMASTE, oldPhone, newPhone)) {
            telephonyUtils.sendSms(Namaste.NAMASTE_FNF, String.format(Locale.getDefault(), Namaste.FNF_MODIFY, oldPhone, newPhone));
        } else
            SnackUtils.showMessage(view, "Please enter correct old and new NTC phone numbers!");
    }

    public void onBtnFNFDeleteClick(View view) {
        String deletePhone = vm.deletePhone.getValue();
        if (Validator.isPhoneNumberValid(Sim.NAMASTE, deletePhone))
            telephonyUtils.sendSms(Namaste.NAMASTE_FNF, String.format(Locale.getDefault(), Namaste.FNF_DELETE, deletePhone));
        else
            SnackUtils.showMessage(view, "Please enter correct NTC phone number to delete!");
    }

    public void onBtnFNFQueryClick(View view) {
        telephonyUtils.sendSms(Namaste.NAMASTE_FNF, Namaste.FNF_QUERY);
    }

    public void onBtnMCASubscribeClick(View view) {
//        telephonyUtils.sendSms(Namaste.NAMASTE_MCA, Namaste.SUBSCRIBE_MCA);
        makeUSSDRequest(Namaste.SUBSCRIBE_MCA, false);
    }

    public void onBtnMCAStatusClick(View view) {
//        telephonyUtils.sendSms(Namaste.NAMASTE_MCA, Namaste.STATUS);
        makeUSSDRequest(Namaste.STATUS_MCA, false);
    }

    public void onBtnMCAUnsubscribeClick(View view) {
//        telephonyUtils.sendSms(Namaste.NAMASTE_MCA, Namaste.UNSUBSCRIBE_MCA);
        makeUSSDRequest(Namaste.UNSUBSCRIBE_MCA, false);
    }

    public void onBtnCRBTSubscribeClick(View view) {
        String songCode = vm.songCode.getValue();
        if (TextUtils.isEmpty(songCode))
            SnackUtils.showMessage(view, "Please do not leave the song code field empty!");
        else
            telephonyUtils.sendSms(Namaste.NAMASTE_CRBT, String.format(Locale.getDefault(), Namaste.SUBSCRIBE_CRBT, vm.songCode));
    }

    public void onBtnCRBTUnsubscribeClick(View view) {
        telephonyUtils.sendSms(Namaste.NAMASTE_CRBT, Namaste.UNSUBSCRIBE_CRBT);
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
            case Namaste.USSD_SELF:
                vm.setPhone(TelephonyUtils.getPhoneText(response.toString()));
                break;
            case Namaste.USSD_BALANCE:
                vm.setBalance(TelephonyUtils.getBalanceText(response.toString()));
                break;
            case Namaste.USSD_SIM_OWNER:
                String simOwner = TelephonyUtils.getSimOwnerText(response.toString());
                vm.setSimOwner(simOwner);
                if (vm.isUserNameDifferent()) {
                    Utils.showDifferentNameDialog(context, vm.getUserName(), simOwner, Sim.NAMASTE);
                }
                break;
            case Namaste.USSD_BALANCE_TRANSFER:
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
