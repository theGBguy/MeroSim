/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/10/29
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

package com.gbsoft.merosim;

import android.app.Application;
import android.os.Handler;

import androidx.core.os.HandlerCompat;

import com.gbsoft.merosim.utils.LocaleUtils;
import com.yariksoffice.lingver.Lingver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Application class of the MeroSimApp
public class MeroSimApp extends Application {
    // Executors and Main Handler instances to implement background threading all over this app
    private ExecutorService executor;
    private Handler mainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize Lingver library instance which is used to enforce system independent locale
        Lingver.init(this, LocaleUtils.getLocale(getApplicationContext()));
    }

    public ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newFixedThreadPool(4);
        return executor;
    }

    public Handler getMainThreadHandler() {
        if (mainThreadHandler == null)
            mainThreadHandler = HandlerCompat.createAsync(getMainLooper());
        return mainThreadHandler;
    }
}
