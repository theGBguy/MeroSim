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

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

public class OverlayService extends Service {
    public static final String CUSTOM_LAYOUT_ID = "custom_layout_id";
    public static final String SHOULD_CANCEL = "should_cancel";

    private WindowManager windowManager;
    private View overlay;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @LayoutRes int layoutId = R.layout.layout_overlay;
        boolean shouldCancel = false;
        if (intent.getExtras() != null) {
            layoutId = intent.getIntExtra(CUSTOM_LAYOUT_ID, R.layout.layout_overlay);
            shouldCancel = intent.getBooleanExtra(SHOULD_CANCEL, false);
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlay = LayoutInflater.from(this).inflate(layoutId, null, false);

        if (shouldCancel)
            cancel();

        overlay.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            cancel();
            UssdController.setCancel(true);
        });

        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.CENTER;

        windowManager.addView(overlay, layoutParams);
        return START_STICKY;
    }

    private void cancel() {
        ((TextView) overlay.findViewById(R.id.tv_loading))
                .setText(getString(R.string.tv_loading_txt_alt));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && overlay != null) {
            windowManager.removeView(overlay);
            overlay = null;
            windowManager = null;
        }
    }
}