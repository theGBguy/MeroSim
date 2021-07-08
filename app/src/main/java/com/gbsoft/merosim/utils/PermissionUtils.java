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
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionUtils {
    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    private static final String[] requiredPermissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS};

    public static void checkAndRequestPermissions(FragmentActivity activity) {
        if (!isAllPermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    public static boolean isAllPermissionsGranted(Context context) {
        for (String permission : requiredPermissions) {
            if (!isPermissionGranted(context, permission)) return false;
        }
        return true;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
