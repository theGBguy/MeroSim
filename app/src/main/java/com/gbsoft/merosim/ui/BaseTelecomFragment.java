/*
 * Created by Chiranjeevi Pandey on 3/10/22, 3:34 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/03/10
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

package com.gbsoft.merosim.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.preference.PreferenceManager;

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/*
 * Base Fragment which implements permission related stuffs and contact picking stuffs
 * common to all the ntc, ncell and smartcell fragments.
 */

public class BaseTelecomFragment extends Fragment implements OnContactFoundListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SERVICE_ACCESSIBILITY = "accessibility";
    public static final String SERVICE_OVERLAY = "overlay";
    private ContactsLoader loader;

    protected final ActivityResultLauncher<Void> contactPicker =
            registerForActivityResult(new PickPhoneNumber(), uri ->
                    loader.restartLoader(LoaderManager.getInstance(BaseTelecomFragment.this), uri)
            );

    protected final ActivityResultLauncher<String> readContactsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    contactPicker.launch(null);
                } else {
                    SnackUtils.showMessage(requireView(), R.string.perm_read_contacts_msg);
                }
            });

    protected final ActivityResultLauncher<String> callPhonePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    SnackUtils.showMessage(requireView(), R.string.perm_granted_txt, "Call phone");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_call_phone_msg);
            });

    protected final ActivityResultLauncher<String> sendSmsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    SnackUtils.showMessage(requireView(), R.string.perm_granted_txt, "Send sms");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_send_sms_msg);
            });

    protected final PermissionFixerContract fixerContract = permission -> {
        switch (permission) {
            case Manifest.permission.CALL_PHONE:
                handlePermission(permission, getString(R.string.perm_call_phone_msg), callPhonePermissionLauncher);
                break;
//            case Manifest.permission.SEND_SMS:
//                handlePermission(permission, getString(R.string.perm_send_sms_msg), sendSmsPermissionLauncher);
//                break;
            case SERVICE_ACCESSIBILITY:
                showAccessibilityDialog();
                break;
            case SERVICE_OVERLAY:
                showOverlayDialog();
                break;
        }
    };

    protected void handlePermission(String permission, String message, ActivityResultLauncher<String> launcher) {
        if (!PermissionUtils.isPermissionGranted(requireContext(), permission)) {
            if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.dialog_permission_title))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.dialog_positive_btn_txt), (dialog, which) -> {
                            launcher.launch(permission);
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.dialog_negative_btn_txt), (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                launcher.launch(permission);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loader = new ContactsLoader(requireContext(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    // empty implementation; intended to be overridden by child classes
    @Override
    public void onContactFound(@NonNull String name, @NonNull String number) {

    }

    protected void launchContactPicker() {
        if (PermissionUtils.isPermissionGranted(requireContext(), Manifest.permission.READ_CONTACTS)) {
            contactPicker.launch(null);
        } else {
            handlePermission(Manifest.permission.READ_CONTACTS, getString(R.string.perm_read_contacts_msg), readContactsPermissionLauncher);
        }
    }

    private void showAccessibilityDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_accessibility_title)
                .setMessage(R.string.dialog_accessibility_msg)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_positive_btn_txt, (dialog, id) ->
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
                .setNegativeButton(getString(R.string.dialog_ignore_btn_txt), (dialog, which) -> {
                    dialog.dismiss();
                    turnOffIntuitiveMode();
                })
                .show();
    }

    private void showOverlayDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_overlay_title)
                .setMessage(R.string.dialog_overlay_msg)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_positive_btn_txt, (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.dialog_ignore_btn_txt), (dialog, which) -> {
                    dialog.dismiss();
                    turnOffIntuitiveMode();
                })
                .show();
    }

    private void turnOffIntuitiveMode() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putBoolean(getString(R.string.key_intuitive), false)
                .apply();
    }

    // intended to be overridden by its children
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
