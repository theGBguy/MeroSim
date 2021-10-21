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
import com.gbsoft.merosim.intermediaries.PrefsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephonyUtils {
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_INPUT = 1;

    private final Context context;
    private final TelephonyManager telephonyManager;
    private final SubscriptionManager subscriptionManager;
    private final UssdController ussdController;
    private List<SubscriptionInfo> subsInfoList;

    public TelephonyUtils(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        this.ussdController = new UssdController(context);
    }

    @SuppressLint("MissingPermission")
    public List<SubscriptionInfo> getSubsInfoList() {
        if (subsInfoList == null && PermissionUtils
                .isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE))
            subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        else
            subsInfoList = new ArrayList<>();
        return subsInfoList;
    }

    @SuppressLint("MissingPermission")
    public void sendUssdRequestWithOverlay(String ussdRequest, int type, int simSlotIndex, UssdResponseCallback callback) {
        if (PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && type == TYPE_NORMAL)
                telephonyManager.sendUssdRequest(ussdRequest, callback, new Handler());
            else
                ussdController.sendUssdRequest(ussdRequest, simSlotIndex, true, callback);
        }
    }

    @SuppressLint("MissingPermission")
    public void sendUssdRequestWithoutOverlay(String ussdRequest, int type, int simSlotIndex, UssdResponseCallback callback) {
        if (PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && type == TYPE_NORMAL)
                telephonyManager.sendUssdRequest(ussdRequest, callback, new Handler());
            else
                ussdController.sendUssdRequest(ussdRequest, simSlotIndex, false, callback);
        }
    }

    public int getSimSlotIndex(String simName) {
        for (SubscriptionInfo subsInfo : getSubsInfoList()) {
            if (TextUtils.equals(simName, subsInfo.getCarrierName()))
                return subsInfo.getSimSlotIndex();
        }
        return -1;
    }

    public String getRechargeUssdRequest(String simName, String pin) {
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

    public List<Sim> getSimList() {
        List<Sim> simsList = new ArrayList<>();
        if (PermissionUtils.isAllPermissionsGranted(context)) {
            for (SubscriptionInfo subsInfo : getSubsInfoList()) {
                String simName = subsInfo.getCarrierName().toString();
                int simSlotIndex = subsInfo.getSimSlotIndex();
                List<String> details = PrefsUtils.retrieveSimDetails(context, simSlotIndex);
                switch (simName) {
                    case Sim.NAMASTE:
                        simsList.add(new Namaste(details.get(0), details.get(1), details.get(2), simSlotIndex));
                        break;
                    case Sim.NCELL:
                        simsList.add(new Ncell(details.get(0), details.get(1), details.get(2), simSlotIndex));
                        break;
                    case Sim.SMART_CELL:
                        simsList.add(new SmartCell(details.get(0), details.get(1), simSlotIndex));
                        break;
                }
            }
        }
        return simsList;
    }

//    public String getBalanceText(String simName, String response) {
//        String balance = "";
//        switch (simName) {
//            case Sim.NAMASTE:
//                String balanceStart = response.substring(16);
//                balance = balanceStart.substring(0, balanceStart.indexOf(' ', 4));
//                break;
//            case Sim.NCELL:
//                String balanceStart2 = response.substring(8);
//                balance = balanceStart2.substring(0, balanceStart2.indexOf('.', 8));
//                break;
//            case Sim.SMART_CELL:
//                break;
//        }
//        return balance;
//    }
//    public String getSimOwnerText(String simName, String response) {
//        String simOwner = "";
//        switch (simName) {
//            case Sim.NAMASTE:
//                simOwner = response.substring(response.indexOf(':') + 1,
//                        response.indexOf('('));
//                break;
//            case Sim.NCELL:
//                simOwner = response.substring(response.indexOf("of") + 3,
//                        response.indexOf('.'));
//                break;
//            case Sim.SMART_CELL:
//                break;
//        }
//        return simOwner;
//    }

    public static String getBalanceText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("Rs. ([0-9]*\\.[0-9]+)", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(0);
        else return "";
    }

    public static String getPhoneText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("9?7?7?(\\d{10})", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(1);
        else return "";
    }

    public static String getSimOwnerText(String response) {
        // Compile regular expression
        Pattern pattern = Pattern.compile("(\\w+\\s\\w+)[(.]", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher matcher = pattern.matcher(response);
        // Use results...
        if (matcher.find())
            return matcher.group(1);
        else return "";
    }

    public void dial(String number) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number))));
    }

    public void call(String number, int simSlotIndex) {
        ussdController.call(number, simSlotIndex);
    }

    public void sendSms(String number, String message) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setDataAndType(Uri.parse("smsto:"), "vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse("smsto:" + number));
        smsIntent.putExtra("sms_body", message);
        try {
            context.startActivity(smsIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No activity found to handle this intent", Toast.LENGTH_LONG).show();
        }
    }
}
