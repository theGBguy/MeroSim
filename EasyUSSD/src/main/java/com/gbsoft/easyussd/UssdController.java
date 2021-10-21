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

package com.gbsoft.easyussd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class UssdController {
    static final String KEY_RESPONSE = "ussd_response";
    static final String EVENT_RESPONSE = "event_response";

    private final Context context;
    private static boolean isRequestOngoing = false;
    private final Intent overlayServiceIntent;
    @LayoutRes
    private int customOverlayLayout = -1;

    private String currentRequest;
    private static boolean shouldCancel = false;
    private UssdResponseCallback callback;

    public UssdController(@NonNull Context context) {
        this.context = context;
        this.overlayServiceIntent = new Intent(context, OverlayService.class);
    }

    public void setCustomOverlayLayout(@LayoutRes int customOverlayLayout) {
        this.customOverlayLayout = customOverlayLayout;
    }

    static void setCancel(boolean shouldCancel) {
        UssdController.shouldCancel = shouldCancel;
    }

    @SuppressLint("NewApi")
    public void sendUssdRequest(String request, int simSlotIndex, boolean withOverlay, UssdResponseCallback callback) {
        if (isRequestOngoing) {
//            callback.onCancelled(request, "Another request is in processing. Please wait!");
            callback.onReceiveUssdResponseCancelled(null, request, context.getString(R.string.ussd_response_cancelled_alt));
            return;
        }
        if (withOverlay) {
            if (verifyOverlayService() && verifyAccessibilityService()) {
                if (customOverlayLayout != -1)
                    overlayServiceIntent.putExtra(OverlayService.CUSTOM_LAYOUT_ID, customOverlayLayout);
                context.startService(overlayServiceIntent);

                this.currentRequest = request;
                this.callback = callback;
                registerBroadcast();
                sendUssdRequest(request, simSlotIndex, true);
            } else {
                callback.onReceiveUssdResponseFailed(null, request, R.string.ussd_response_failed_lack_permission);
            }
        } else {
            sendUssdRequest(request, simSlotIndex, false);
        }
    }

    public void cancelRequest(String request, UssdResponseCallback callback) {
        if (isRequestOngoing && TextUtils.equals(currentRequest, request)) {
            shouldCancel = true;
        }
    }

    private void sendUssdRequest(String request, int simSlotIndex, boolean isBlocking) {
        if (isBlocking)
            isRequestOngoing = true;
        context.startActivity(getActionCallIntent(Uri.parse("tel:" + Uri.encode(request)), simSlotIndex));
    }

    public void call(String number, int simSlotIndex) {
        context.startActivity(getActionCallIntent(Uri.parse("tel:" + number), simSlotIndex));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            isRequestOngoing = false;
            unregisterBroadcast();
            UssdController.this.context.stopService(overlayServiceIntent);

            if (shouldCancel) {
                shouldCancel = false;
                callback.onReceiveUssdResponseCancelled(null, currentRequest, context.getString(R.string.ussd_response_cancelled_msg));
                return;
            }

            String response = intent.getStringExtra(KEY_RESPONSE);
            if (response != null && !TextUtils.isEmpty(response) && callback != null) {
                if (response.contains("invalid") || response.contains("problem")) {
                    callback.onReceiveUssdResponseFailed(null, currentRequest, TelephonyManager.USSD_RETURN_FAILURE);
                } else {
                    callback.onReceiveUssdResponse(null, currentRequest, response);
                }
            }
        }
    };

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(EVENT_RESPONSE));
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    private boolean verifyAccessibilityService() {
        if (isAccessibilityServiceEnabled())
            return true;
        else {
            showAccessibilityDialog();
            return false;
        }
    }

    private boolean verifyOverlayService() {
        if (Settings.canDrawOverlays(context))
            return true;
        else {
            showOverlayDialog();
            return false;
        }
    }

    private void showAccessibilityDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_accessibility_title)
                .setMessage(R.string.dialog_accessibility_msg)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_positive_btn_txt, (dialog, id) ->
                        context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showOverlayDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_overlay_title)
                .setMessage(R.string.dialog_overlay_msg)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_positive_btn_txt, (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }).show();
    }

    private boolean isAccessibilityServiceEnabled() {
        ComponentName expectedComponentName = new ComponentName(context, UssdService.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }


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
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);

        for (String slotIndex : simSlotName)
            intent.putExtra(slotIndex, simSlotIndex);

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (isPhoneStatePermissionGranted()) {
            List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > simSlotIndex)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(simSlotIndex));
        } else
            Toast.makeText(context, R.string.phone_permission_failed, Toast.LENGTH_LONG).show();
        return intent;
    }

    static boolean isRequestOngoing() {
        return isRequestOngoing;
    }

    private boolean isPhoneStatePermissionGranted() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

}
