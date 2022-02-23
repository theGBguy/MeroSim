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

import com.gbsoft.merosim.R;

/*
 * This class is used to display overlay to hide the
 * USSD dialog to improve user experience.
 */

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlay;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // inflates the overlay layout
        overlay = LayoutInflater.from(this).inflate(R.layout.layout_overlay, null, false);
        overlay.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            ((TextView) overlay.findViewById(R.id.tv_loading))
                    .setText(getString(R.string.tv_loading_txt_alt));
            UssdController.shouldCancel = true;
        });

        // sets the appropriate layout flag to display overlay
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

        // adds the view in the window
        windowManager.addView(overlay, layoutParams);

        return START_NOT_STICKY;
    }

    // called on service is stopped
    // removes the view from window manager
    // thereby stopping the overlay
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && overlay != null) {
            windowManager.removeView(overlay);
        }
    }
}