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

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/*
 * This class is used to intercept USSD response dialog using the
 * accessibility service. It is mainly used to improve user experience
 * but user can choose to turn it off, if they do not want any
 * intervention with the original USSD response dialog.
 */

public class UssdService extends AccessibilityService {
    private static final String TAG = UssdService.class.getSimpleName();

    public static final String KEY_RESPONSE = "ussd_response";
    public static final String EVENT_RESPONSE = "event_response";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent fired");

        // doesn't intercept if USSD request was not sent by this app
        if (!UssdController.isRequestOngoing) return;

        // filters the appropriate event source
        AccessibilityNodeInfo nodeInfo = event.getSource();
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().toString().contains("AlertDialog")) {
            return;
        }
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (nodeInfo == null || TextUtils.isEmpty(nodeInfo.getText()) || !(nodeInfo.getClassName().toString().equals("android.view.TextView"))) {
                return;
            }
        }
        Log.d(TAG, "onAccessibilityEvent fired");

        // parses the reponse
        String response;
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            response = event.getText().get(0).toString() + " ";
        else
            response = nodeInfo.getText().toString();

        //      performGlobalAction(GLOBAL_ACTION_BACK);
        // cancels the dialog after successfully parsing response
        // and send the response to USSDController
        if (UssdController.isRequestOngoing) {
            cancelDialog(event);
            sendBroadcast(response);
        }
    }

    // sends local broadcast
    private void sendBroadcast(String response) {
        Intent receiverIntent = new Intent(EVENT_RESPONSE);
        receiverIntent.putExtra(KEY_RESPONSE, response);
        new Handler(getMainLooper()).postDelayed(() ->
                LocalBroadcastManager.getInstance(UssdService.this).sendBroadcast(receiverIntent), 500);
    }

    // perform programmatic click in USSD response dialog
    // by traversing the nodes and finding the appropriate button
    private void cancelDialog(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> nodeInfos = getLeaves(event);
        for (AccessibilityNodeInfo leaf : nodeInfos) {
            if (leaf.getClassName().toString().toLowerCase().contains("button")) {
                leaf.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        }
    }

    // gets all the leaf nodes of the accessibility event
    private List<AccessibilityNodeInfo> getLeaves(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> leaves = new ArrayList<>();
        if (event.getSource() != null)
            getLeaves(leaves, event.getSource());
        return leaves;
    }

    // traverses the node based on whether it is a parent node
    private void getLeaves(List<AccessibilityNodeInfo> leaves, AccessibilityNodeInfo node) {
        if (node.getChildCount() == 0) {
            leaves.add(node);
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            getLeaves(leaves, node.getChild(i));
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt fired");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected fired");
    }
}
