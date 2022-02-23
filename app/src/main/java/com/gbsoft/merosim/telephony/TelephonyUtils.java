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

import com.gbsoft.merosim.model.Namaste;
import com.gbsoft.merosim.model.Ncell;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.model.SmartCell;
import com.gbsoft.merosim.ui.PermissionFixerContract;
import com.gbsoft.merosim.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This is a utility class that is used to invoke different
 * telephony related operations.
 */

public class TelephonyUtils {
    // constants to identify whether the USSD response dialog
    // is a simple dismissable dialog or contains text field
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_INPUT = 1;

    private final Context context;
    private final Map<Integer, TelephonyManager> telephonyManagers;
    private final TelephonyManager telephonyManager;
    private final SubscriptionManager subscriptionManager;
    private final UssdController ussdController;
    private List<SubscriptionInfo> subsInfoList;

    // singleton instance of this class
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

    // initializes telephony managers instances for both
    // single and dual sim enabled devices
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
        if (isCallPermissionGranted(fixerContract, withOverlay)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && type == TYPE_NORMAL)
                telephonyManagers.get(simSlotIndex).sendUssdRequest(ussdRequest, callback, new Handler());
            else
                ussdController.sendUssdRequest(ussdRequest, simSlotIndex, withOverlay, callback);
        }
    }

    private boolean isCallPermissionGranted(PermissionFixerContract fixerContract, boolean withOverlay) {
        if (!PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)) {
            fixerContract.fixPermission(Manifest.permission.CALL_PHONE);
            return false;
        }
        return true;
    }

    // returns the sim slot index of the given sim
    public int getSimSlotIndex(String simName) {
        for (SubscriptionInfo subsInfo : getSubsInfoList()) {
            if (TextUtils.equals(simName, subsInfo.getCarrierName()))
                return subsInfo.getSimSlotIndex();
        }
        return -1;
    }

    // returns the list of sim card inserted in the device
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

    // extracts the balance from the USSD response
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

    // extracts the phone number from the USSD response
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

    // extracts the sim owner from the USSD response
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

    // generates the USSD request string for different sim card
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

    // dials the number in the phone's default dialer
    public void dial(String number) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)));
        dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialIntent);
    }

    // calls the number directly in the phone's default call app
    public void call(String number, int simSlotIndex, PermissionFixerContract fixerContract) {
        if (PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)) {
            ussdController.call(number, simSlotIndex);
        } else {
            fixerContract.fixPermission(Manifest.permission.CALL_PHONE);
        }
    }

    // sends sms to the given number using phone's default sms application
    public void sendSms(String number, String message, PermissionFixerContract fixerContract) {
        if (PermissionUtils.isPermissionGranted(context, Manifest.permission.SEND_SMS)) {
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

}
