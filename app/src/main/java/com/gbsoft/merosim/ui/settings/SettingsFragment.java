/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/22
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

package com.gbsoft.merosim.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.utils.LocaleUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.google.android.material.snackbar.Snackbar;
import com.yariksoffice.lingver.Lingver;

// Fragments to display application's preferences
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        // sets listener to handle send feedback preference click event
        Preference sendFeedback = findPreference(getString(R.string.key_send_feedback));
        if (sendFeedback == null) return;
        sendFeedback.setOnPreferenceClickListener(preference -> {
            sendFeedback();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // change the system independent locale if language preference is clicked
        if (key.equals(getString(R.string.key_language))) {
            Lingver.getInstance().setLocale(requireActivity().getApplicationContext(), LocaleUtils.getLocale(requireContext()));
            SnackUtils.showMessageWithCallback(requireView(), R.string.snack_msg_restart_txt, new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (getView() == null) {
                        return;
                    }
                    transientBottomBar.removeCallback(this);
                    requireActivity().recreate();
                }
            });
        }
    }

    // attaches device info and send email to developer using appropriate
    // email client
    private void sendFeedback() {
        String body = null;
        try {
            body = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER +
                    "\n-----------------------------\n";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.setType("text/html");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact.merosim@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query for Mero Sim App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_client_title)));
    }
}