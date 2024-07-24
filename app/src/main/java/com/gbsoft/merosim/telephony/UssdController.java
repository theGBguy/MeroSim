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

package com.gbsoft.merosim.telephony;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.utils.PermissionUtils;

import java.util.List;

/*
 * This class is central to each USSD related operations. It shows
 * the overlay when required and returns the response to the caller.
 */

public class UssdController {
    // constants used to broadcast local events
    static final String KEY_RESPONSE = "ussd_response";
    static final String EVENT_RESPONSE = "event_response";
    static final String RESPONSE_DATA_CANCELLED = "cancelled";
    static final int OVERLAY_TIMEOUT_SECS = 25;

    private final Context context;
    private final Intent overlayServiceIntent;

    private static String currentRequest = "";
    private USSDResponseCallback callback;
    private final Handler ussdRequestHandler;

    public UssdController(@NonNull Context context) {
        this.context = context;
        this.ussdRequestHandler  = new Handler(Looper.getMainLooper());
        this.overlayServiceIntent = new Intent(context, OverlayService.class);
    }

    // sends ussd request with or without overlay
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @SuppressLint("NewApi")
    public void sendUssdRequest(String request, int simSlotIndex, boolean withOverlay, USSDResponseCallback callback) {
        if (!currentRequest.isEmpty()) {
            callback.onReceiveUssdResponseCancelled(null, request, context.getString(R.string.ussd_response_cancelled_alt));
            return;
        }
        context.startActivity(getActionCallIntent(Uri.parse("tel:" + Uri.encode(request)), simSlotIndex));
        if (withOverlay) {
            String currentRequestCached = currentRequest;
            context.startService(overlayServiceIntent);
            // will automatically close itself in 25 secs if its not closed already
            ussdRequestHandler.postDelayed(() -> {
                if (!currentRequestCached.equals(currentRequest)) {
                    currentRequest = "";
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
                    callback.onReceiveUssdResponseCancelled(null, currentRequest, context.getString(R.string.ussd_response_cancelled_msg));
                    context.stopService(overlayServiceIntent);
                }
            }, OVERLAY_TIMEOUT_SECS * 1000);
            currentRequest = request;
            this.callback = callback;
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(EVENT_RESPONSE));
        }
    }

    // calls the given number using the appropriate sim card
    public void call(String number, int simSlotIndex) {
        context.startActivity(getActionCallIntent(Uri.parse("tel:" + number), simSlotIndex));
    }

    // receives the locally broadcasted USSD related events
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(UssdController.this.context).unregisterReceiver(receiver);
            UssdController.this.context.stopService(overlayServiceIntent);
            ussdRequestHandler.removeCallbacksAndMessages(null);

            if (callback == null) {
                currentRequest = "";
                return;
            }

            String response = intent.getStringExtra(KEY_RESPONSE);
            if (TextUtils.equals(response, RESPONSE_DATA_CANCELLED)) {
                callback.onReceiveUssdResponseCancelled(null, currentRequest, context.getString(R.string.ussd_response_cancelled_msg));
                currentRequest = "";
                return;
            }
            if (response.contains("Connection problem")) {
                callback.onReceiveUssdResponseFailed(null, currentRequest, TelephonyManager.USSD_RETURN_FAILURE);
            } else {
                callback.onReceiveUssdResponse(null, currentRequest, response);
            }
            currentRequest = "";
        }
    };

    public static boolean isRequestOngoing() {
        return !currentRequest.isEmpty();
    }

    // generates appropriate call intent for any sim slot
    @SuppressLint("MissingPermission")
    private Intent getActionCallIntent(Uri uri, int simSlotIndex) {
        // https://stackoverflow.com/questions/25524476/make-call-using-a-specified-sim-in-a-dual-sim-device
        final String[] simSlotName = {
                "extra_asus_dial_use_dualsim",
                "com.android.phone.extra.slot",
                "slot",
                "simslot",
                "sim_slot",
                "subscription",
                "Subscription",
                "phone",
                "com.android.phone.DialingMode",
                "simSlot",
                "slot_id",
                "simId",
                "simnum",
                "phone_type",
                "slotId",
                "slotIdx"
        };

        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);

        for (String slotIndex : simSlotName)
            intent.putExtra(slotIndex, simSlotIndex);

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (PermissionUtils.isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE)) {
            List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > simSlotIndex)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(simSlotIndex));
        } else
            Toast.makeText(context, R.string.perm_phone_denied_txt, Toast.LENGTH_LONG).show();
        return intent;
    }

}
