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
 * Last modified: 2021/10/26
 */

package com.gbsoft.merosim.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;

import com.gbsoft.easyussd.UssdService;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data.Sim;
import com.gbsoft.merosim.intermediaries.PrefsUtils;
import com.gbsoft.merosim.ui.recharge.OnTextRecognizedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    @SuppressLint("MissingPermission")
    public static void vibrateIfNecessary(Context context) {
        if (shouldVibrate(context)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            else
                vibrator.vibrate(500);
        }
    }

    private static boolean shouldVibrate(Context context) {
        return PrefsUtils.getDefaultSharedPrefs(context).getBoolean("key_vibrate", true);
    }

    // send 0 for ntc, 1 for ncell and 2 for smart cell
    public static int getSimColor(Resources res, int sim) {
        boolean isNightMode = isNightMode(res);
        switch (sim) {
            case 0:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_ntc_light : R.color.color_ntc, null);
            case 1:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_ncell_200 : R.color.color_ncell, null);
            case 2:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_smart_200 : R.color.color_smart, null);
        }
        return ResourcesCompat.getColor(res, R.color.branded_surface, null);
    }

    private static boolean isNightMode(Resources res) {
        int nightModeFlags = res.getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }
        return false;
    }

    public static int dpToPx(Resources res, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static void showInfoDialog(View anchor, @StringRes int info) {
//        int length = Utils.dpToPx(context.getResources(), 320);
//        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_info, null);
//
//        PopupWindow popup = new PopupWindow(popupView, length, length, true);
//        popup.setElevation(20);
//        if (isNightMode(context.getResources()))
//            popup.setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.black900)));
//        else
//            popup.setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.white300)));
//        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//
//        popupView.setOnClickListener(v -> popup.dismiss());
//
//        popup.showAsDropDown(anchor, 0, 0, Gravity.BOTTOM | Gravity.END);
//
//        TextView tvBalanceTransferInfo = popupView.findViewById(R.id.tv_content);
//        tvBalanceTransferInfo.setText(HtmlCompat.fromHtml(context.getString(info), 0));
        Context context = anchor.getContext();
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.info_txt)
                .setIcon(R.drawable.ic_round_help_center_24)
                .setMessage(HtmlCompat.fromHtml(context.getString(info), 0).toString())
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_positive_btn_txt, (dialog, id) ->
                        dialog.dismiss())
                .show();
    }

    @ExperimentalGetImage
    public static void recognizeText(String simName, InputImage image, OnTextRecognizedListener listener) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    Log.d("Analyse", text.getText());

                    String scanned = getPinFromLine(simName, text.getText());
                    if (scanned.length() == 16)
                        listener.onTextRecognized(scanned);
                    else
                        listener.onRecognizationFailed(new Exception("Couldn't scan the pin!!"));
                })
                .addOnFailureListener(listener::onRecognizationFailed);
    }

    private static String getPinFromLine(Text.Line line) {
        StringBuilder pinStr = new StringBuilder();
        char[] lineChars = line.getText().toCharArray();
        for (char c : lineChars) {
            if (pinStr.length() < 16 && Character.isDigit(c)) {
                pinStr.append(c);
            }
        }
        return pinStr.toString();
    }

    public static String getPinFromLine(String simName, String scannedText) {
        Pattern pinPattern = null;
        switch (simName) {
            case Sim.NAMASTE:
                pinPattern = Pattern.compile("(\\d{5}.?\\d{6}.?\\d{5})");
                break;
            case Sim.NCELL:
                pinPattern = Pattern.compile("(\\d{4}.?\\d{4}.?\\d{4}.?\\d{4})");
                break;
            case Sim.SMART_CELL:
                pinPattern = Pattern.compile("\\d{4}.?\\d{5}.?\\d{4}");
                break;
        }

        Matcher pinMatcher = pinPattern.matcher(scannedText);

        if (pinMatcher.find())
            return pinMatcher.group().replaceAll("\\D", "");
        else
            return "";
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        ComponentName expectedComponentName = new ComponentName(context, UssdService.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledComponentName = ComponentName.unflattenFromString(componentNameString);

            if (enabledComponentName != null && enabledComponentName.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public static boolean isOverlayServiceEnabled(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
