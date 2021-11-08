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

package com.gbsoft.merosim.ui;

import android.Manifest;
import android.content.Intent;
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

import com.gbsoft.merosim.R;
import com.gbsoft.merosim.utils.PermissionUtils;
import com.gbsoft.merosim.utils.SnackUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BaseTelecomFragment extends Fragment implements OnContactFoundListener {
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
                    SnackUtils.showMessage(requireView(), R.string.permission_granted_txt, "Call phone");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_call_phone_msg);
            });

    protected final ActivityResultLauncher<String> sendSmsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    SnackUtils.showMessage(requireView(), R.string.permission_granted_txt, "Send sms");
                else
                    SnackUtils.showMessage(requireView(), R.string.perm_send_sms_msg);
            });

    protected final PermissionFixerContract fixerContract = permission -> {
        switch (permission) {
            case Manifest.permission.CALL_PHONE:
                handlePermission(permission, getString(R.string.perm_call_phone_msg), callPhonePermissionLauncher);
                break;
            case Manifest.permission.SEND_SMS:
                handlePermission(permission, getString(R.string.perm_send_sms_msg), sendSmsPermissionLauncher);
                break;
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
                        .setTitle(getString(R.string.perm_dialog_title))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.positive_dialog_btn_txt), (dialog, which) -> {
                            launcher.launch(permission);
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.negative_dialog_btn_txt), (dialog, which) -> {
                            dialog.dismiss();
                        })
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
                .setTitle(com.gbsoft.easyussd.R.string.dialog_accessibility_title)
                .setMessage(com.gbsoft.easyussd.R.string.dialog_accessibility_msg)
                .setCancelable(true)
                .setPositiveButton(com.gbsoft.easyussd.R.string.dialog_positive_btn_txt, (dialog, id) ->
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
                .show();
    }

    private void showOverlayDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(com.gbsoft.easyussd.R.string.dialog_overlay_title)
                .setMessage(com.gbsoft.easyussd.R.string.dialog_overlay_msg)
                .setCancelable(true)
                .setPositiveButton(com.gbsoft.easyussd.R.string.dialog_positive_btn_txt, (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(intent);
                }).show();
    }
}
