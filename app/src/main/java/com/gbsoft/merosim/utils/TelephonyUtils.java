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


package com.gbsoft.merosim.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.gbsoft.easyussd.UssdController;
import com.gbsoft.easyussd.UssdResponseCallback;
import com.gbsoft.merosim.data.Namaste;
import com.gbsoft.merosim.data.Ncell;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.data.SmartCell;
import com.gbsoft.merosim.ui.BaseTelecomFragment;
import com.gbsoft.merosim.ui.PermissionFixerContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephonyUtils {
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_INPUT = 1;

    private final Context context;
    private final Map<Integer, TelephonyManager> telephonyManagers;
    private final TelephonyManager telephonyManager;
    private final SubscriptionManager subscriptionManager;
    private final UssdController ussdController;
    private List<SubscriptionInfo> subsInfoList;

    @SuppressLint("StaticFieldLeak")
    private static TelephonyUtils INSTANCE;

    public static TelephonyUtils getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TelephonyUtils.class) {
                if (INSTANCE == null)
                    INSTANCE = new TelephonyUtils(context.getApplicationContext());
            }
        }
        return INSTANCE;
    }

    private TelephonyUtils(Context context) {
        this.context = context;
        this.telephonyManagers = new HashMap<>();
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        this.ussdController = new UssdController(context);
        initializeTelephonyManagers();
    }

    private void initializeTelephonyManagers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (SubscriptionInfo subsInfo : getSubsInfoList()) {
                telephonyManagers.put(subsInfo.getSimSlotIndex(),
                        telephonyManager.createForSubscriptionId(subsInfo.getSubscriptionId()));
            }
        } else {
            telephonyManagers.put(0, telephonyManager);
        }
    }

    @SuppressLint("MissingPermission")
    private List<SubscriptionInfo> getSubsInfoList() {
        if (subsInfoList == null)
            subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        return subsInfoList;
    }

    @SuppressLint("MissingPermission")
    public void sendUssdRequest(String ussdRequest, boolean withOverlay, int type, int simSlotIndex, UssdResponseCallback callback, PermissionFixerContract fixerContract) {
        if (isAllCallPermissionGranted(fixerContract)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && type == TYPE_NORMAL)
                telephonyManagers.get(simSlotIndex).sendUssdRequest(ussdRequest, callback, new Handler());
            else
                ussdController.sendUssdRequest(ussdRequest, simSlotIndex, withOverlay, callback);
        }
    }

    private boolean isAllCallPermissionGranted(PermissionFixerContract fixerContract) {
        if (!PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)) {
            fixerContract.fixPermission(Manifest.permission.CALL_PHONE);
            return false;
        }

        if (!Utils.isAccessibilityServiceEnabled(context)) {
            fixerContract.fixPermission(BaseTelecomFragment.SERVICE_ACCESSIBILITY);
            return false;
        }

        if (!Utils.isOverlayServiceEnabled(context)) {
            fixerContract.fixPermission(BaseTelecomFragment.SERVICE_OVERLAY);
            return false;
        }

        return true;
    }

    public int getSimSlotIndex(String simName) {
        for (SubscriptionInfo subsInfo : getSubsInfoList()) {
            if (TextUtils.equals(simName, subsInfo.getCarrierName()))
                return subsInfo.getSimSlotIndex();
        }
        return -1;
    }

    public List<Sim> getSimList() {
        List<Sim> simsList = new ArrayList<>();
        for (SubscriptionInfo subsInfo : getSubsInfoList()) {
            String simName = subsInfo.getCarrierName().toString();
            int simSlotIndex = subsInfo.getSimSlotIndex();
            switch (simName) {
                case Sim.NAMASTE:
                    simsList.add(new Namaste(simSlotIndex));
                    break;
                case Sim.NCELL:
                    simsList.add(new Ncell(simSlotIndex));
                    break;
                case Sim.SMART_CELL:
                    simsList.add(new SmartCell(simSlotIndex));
                    break;
            }
        }
        return simsList;
    }

    public static String getBalanceText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("Rs. ([0-9]*\\.[0-9]+)", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(0);
        else return Sim.UNAVAILABLE;
    }

    public static String getPhoneText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("9?7?7?(\\d{10})", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(1);
        else return Sim.UNAVAILABLE;
    }

    public static String getSimOwnerText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("(\\w+\\s\\w+)[(.]", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(1);
        else return Sim.UNAVAILABLE;
    }

    public static String getRechargeUssdRequest(String simName, String pin) {
        String ussdRequest = null;
        switch (simName) {
            case Sim.NAMASTE:
                ussdRequest = String.format(Locale.getDefault(), Namaste.USSD_RECHARGE, pin);
                break;
            case Sim.NCELL:
                ussdRequest = String.format(Locale.getDefault(), Ncell.USSD_RECHARGE, pin);
                break;
            case Sim.SMART_CELL:
                ussdRequest = String.format(Locale.getDefault(), SmartCell.USSD_RECHARGE, pin);
                break;
        }
        return ussdRequest;
    }

    public void dial(String number) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)));
        dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialIntent);
    }

    public void call(String number, int simSlotIndex, PermissionFixerContract fixerContract) {
        if (isCallPhoneGranted()) {
            ussdController.call(number, simSlotIndex);
        } else {
            fixerContract.fixPermission(Manifest.permission.CALL_PHONE);
        }
    }

    public void sendSms(String number, String message, PermissionFixerContract fixerContract) {
        if (isSendSmsGranted()) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setDataAndType(Uri.parse("smsto:"), "vnd.android-dir/mms-sms");
            smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            smsIntent.setData(Uri.parse("smsto:" + number));
            smsIntent.putExtra("sms_body", message);
            try {
                context.startActivity(smsIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No activity found to handle this intent", Toast.LENGTH_LONG).show();
            }
        } else {
            fixerContract.fixPermission(Manifest.permission.SEND_SMS);
        }
    }

    private boolean isCallPhoneGranted() {
        return PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE);
    }

    private boolean isSendSmsGranted() {
        return PermissionUtils.isPermissionGranted(context, Manifest.permission.SEND_SMS);
    }
}
