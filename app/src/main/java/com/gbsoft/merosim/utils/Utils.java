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

package com.gbsoft.merosim.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.text.util.LinkifyCompat;

import com.codertainment.materialintro.MaterialIntroConfiguration;
import com.codertainment.materialintro.sequence.MaterialIntroSequence;
import com.codertainment.materialintro.sequence.SkipLocation;
import com.codertainment.materialintro.shape.Focus;
import com.codertainment.materialintro.shape.FocusGravity;
import com.codertainment.materialintro.view.MaterialIntroView;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data_source.PrefsUtils;
import com.gbsoft.merosim.databinding.DialogAskUsernameBinding;
import com.gbsoft.merosim.model.Sim;
import com.gbsoft.merosim.telephony.UssdService;
import com.gbsoft.merosim.ui.recharge.OnTextRecognizedListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    // vibrates the device if it is set so in the preference
    @SuppressLint("MissingPermission")
    public static void vibrateIfNecessary(Context context) {
        if (PrefsUtils.getDefaultSharedPrefs(context).getBoolean("key_vibrate", true)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            else
                vibrator.vibrate(500);
        }
    }

    // returns color according to sim's primary color
    // send 0 for ntc, 1 for ncell and 2 for smart cell
    public static int getSimColor(Resources res, int sim) {
        boolean isNightMode = isNightMode(res);
        switch (sim) {
            case 0:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_ntc_200 : R.color.color_ntc, null);
            case 1:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_ncell_200 : R.color.color_ncell, null);
            case 2:
                return ResourcesCompat.getColor(res, isNightMode ? R.color.color_smart_200 : R.color.color_smart, null);
        }
        return ResourcesCompat.getColor(res, R.color.branded_surface, null);
    }

    // returns whether night mode is on
    public static boolean isNightMode(Resources res) {
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

    // converts dp value to px using the device's current resolution
    public static int dpToPx(Resources res, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    // shows generic info dialog
    public static void showInfoDialog(View anchor, @StringRes int info) {
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

    // shows dialog to ask user name on the first start of the app
    public static void askUserName(Context context, ViewGroup root) {
        WeakReference<ViewGroup> rootWeakReference = new WeakReference<>(root);
        DialogAskUsernameBinding binding = DialogAskUsernameBinding
                .inflate(LayoutInflater.from(context), rootWeakReference.get(), false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .show();
        // make the dialog non-cancellable on back button and touch outside
        // the dialog box
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        binding.btnSubmit.setOnClickListener(v -> {
            Editable username = binding.tilName.getEditText().getText();
            if (username != null && Validator.isFullNameValid(username.toString())) {
                PrefsUtils.saveUserName(context, username.toString().toUpperCase());
                dialog.dismiss();
            } else {
                SnackUtils.showMessage(rootWeakReference.get(), R.string.invalid_full_name_txt);
            }
        });

        binding.btnIgnore.setOnClickListener(v -> {
            PrefsUtils.saveUserName(context, PrefsUtils.NAME_UNKNOWN);
            dialog.dismiss();
        });

    }

    // shows dialog to warn user to use the sim registered in their own name
    public static void showDifferentNameDialog(Context context, String appName, String responseName, String simName) {
        // spannable string to make the link clickable in the text
        SpannableString msg = new SpannableString(context.getString(R.string.different_name_dialog_msg, responseName, appName, simName));
        LinkifyCompat.addLinks(msg, Linkify.WEB_URLS);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_different_name, null, false);
        TextView message = dialogView.findViewById(R.id.tv_msg);
        message.setText(msg);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        new MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.ic_round_help_center_24)
                .setTitle(R.string.different_name_dialog_title)
                .setView(dialogView)
                .setNeutralButton(R.string.okay_txt, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static MaterialIntroView getIntroView(View targetView, @StringRes Integer infoText) {
        Context context = targetView.getContext();
        MaterialIntroView materialIntroView = new MaterialIntroView(context);
        //  materialIntroView.setMaskColor(res.getColor(R.color.md_theme_light_primaryContainer, theme));
        materialIntroView.setPadding(24);
        materialIntroView.setInfoText(context.getString(infoText));
        materialIntroView.setInfoTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onSecondaryContainer));
        materialIntroView.setInfoTextSize(18f);
        materialIntroView.setInfoTextTypeface(ResourcesCompat.getFont(context, R.font.proxima_nova_regular));
        materialIntroView.setInfoCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_secondaryContainer));
//        materialIntroView.setShowOnlyOnce(false);
        materialIntroView.setHelpIconResource(R.drawable.ic_round_help_center_24);
        materialIntroView.setHelpIconColor(ContextCompat.getColor(context, R.color.md_theme_light_onSecondaryContainer));
        materialIntroView.setFocusType(Focus.NORMAL);
        materialIntroView.setDotIconColor(ContextCompat.getColor(context, R.color.white300));
        materialIntroView.setViewId(getId(targetView));
        materialIntroView.setTargetView(targetView);
//        materialIntroView.setPerformClick(true);

//        materialIntroView.setSkipLocation(SkipLocation.BOTTOM_RIGHT);
//        MaterialButton button = new MaterialButton(context);
//        button.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer));
//        materialIntroView.setSkipButton(button);

        return materialIntroView;
    }

    private static String getId(View view) {
        if (view.getId() == View.NO_ID) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }

    public static void showMaterialIntroSequence(Activity activity, View... views) {
        MaterialIntroSequence materialIntroSequence = MaterialIntroSequence.Companion.getInstance(activity);

        MaterialIntroConfiguration config = new MaterialIntroConfiguration();
        config.setPadding(24);
        config.setInfoTextColor(ContextCompat.getColor(activity, R.color.md_theme_light_onSecondaryContainer));
        config.setInfoTextSize(18f);
        config.setInfoTextTypeface(ResourcesCompat.getFont(activity, R.font.proxima_nova_regular));
        config.setInfoCardBackgroundColor(ContextCompat.getColor(activity, R.color.md_theme_light_secondaryContainer));
//        config.setShowOnlyOnce(false);
        config.setHelpIconResource(R.drawable.ic_round_help_center_24);
        config.setHelpIconColor(ContextCompat.getColor(activity, R.color.md_theme_light_onSecondaryContainer));
        config.setFocusType(Focus.NORMAL);
        config.setDotViewEnabled(false);
//        config.setDotIconColor(ContextCompat.getColor(activity, R.color.white300));
//        config.setPerformClick(true);
        config.setSkipLocation(SkipLocation.BOTTOM_RIGHT);
//        config.setSkipButtonStyling(materialButton -> {
//            materialButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.md_theme_light_primaryContainer));
//            return null;
//        });

        for (int i = 0; i < views.length; i++) {
            String introText = "";
            switch (i) {
                case 0:
                    introText = activity.getString(R.string.intro_phone_txt);
                    break;
                case 1:
                    introText = activity.getString(R.string.intro_balance_txt);
                    break;
                case 2:
                    if(views.length == 3){
                        config.setFocusGravity(FocusGravity.LEFT);
                        introText = activity.getString(R.string.intro_info_txt);
                    }else{
                        introText = activity.getString(R.string.intro_sim_owner_txt);
                    }
                    break;
                case 3:
                    config.setFocusGravity(FocusGravity.LEFT);
                    introText = activity.getString(R.string.intro_info_txt);
                    break;
            }
            materialIntroSequence.add(createNewConfigFromExisting(config, introText, views[i]));
        }
        //        materialIntroSequence.setShowSkip(true);
        materialIntroSequence.setInitialDelay(3000);
        materialIntroSequence.start();
    }

    private static MaterialIntroConfiguration createNewConfigFromExisting(MaterialIntroConfiguration existing, CharSequence info, View target) {
        existing.setInfoText(info);
        existing.setViewId(getId(target));
        existing.setTargetView(target);
        return existing;
    }

    // scans pin code from the captured image
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

    // uses regex to capture pin code from the overall text
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

        if (simName.equals(Sim.NCELL)) {
            List<String> pins = new ArrayList<>();
            while (pinMatcher.find()) {
                pins.add(pinMatcher.group().replaceAll("\\D", ""));
            }
            return pins.isEmpty() ? "" : pins.get(pins.size() - 1);
        }
        if (pinMatcher.find()) {
            return pinMatcher.group().replaceAll("\\D", "");
        }

        return "";
    }

    // checks whether accessibility service is enabled
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

    // checks whether overlay service is enabled
    public static boolean isOverlayServiceEnabled(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
