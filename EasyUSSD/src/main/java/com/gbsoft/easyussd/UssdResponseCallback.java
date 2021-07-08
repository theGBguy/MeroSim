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

import android.annotation.SuppressLint;
import android.telephony.TelephonyManager;

// -1 = Connection problem or invalid MMI code(Return failure).
// -2 = Other error such as network unavailable
// -3 = Request cancellation
@SuppressLint("NewApi")
public abstract class UssdResponseCallback extends TelephonyManager.UssdResponseCallback {
//    int ERROR_RETURN_FAILURE = -1;
//    int ERROR_NETWORK_UNAVAIL = -2;
//
//    void onSuccessfulResponse(String request, String response);
//
//    void onFailedResponse(String request, int failureCode);
//
//    void onCancelled(String request, String cancellationMessage);

    public void onReceiveUssdResponseCancelled(TelephonyManager telephonyManager, String request, String cancellationMsg) {
    }

    ;

}
