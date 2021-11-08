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
 * Last modified: 2021/10/19
 */

package com.gbsoft.easyussd;

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

public class UssdService extends AccessibilityService {
    private static final String TAG = UssdService.class.getSimpleName();

    public static final String KEY_RESPONSE = "ussd_response";
    public static final String EVENT_RESPONSE = "event_response";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent fired");

        if (!UssdController.isRequestOngoing()) return;

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

        String response;
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            response = event.getText().get(0).toString() + " ";
        else
            response = nodeInfo.getText().toString();
//        performGlobalAction(GLOBAL_ACTION_BACK);
        if (UssdController.isRequestOngoing()) {
            cancelDialog(event);
            sendBroadcast(response);
        }
    }

    private void sendBroadcast(String response) {
        Intent receiverIntent = new Intent(EVENT_RESPONSE);
        receiverIntent.putExtra(KEY_RESPONSE, response);
        new Handler(getMainLooper()).postDelayed(() ->
                LocalBroadcastManager.getInstance(UssdService.this).sendBroadcast(receiverIntent), 500);
    }

    private void cancelDialog(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> nodeInfos = getLeaves(event);
        for (AccessibilityNodeInfo leaf : nodeInfos) {
            if (leaf.getClassName().toString().toLowerCase().contains("button")) {
                leaf.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        }
    }

    private List<AccessibilityNodeInfo> getLeaves(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> leaves = new ArrayList<>();
        if (event.getSource() != null)
            getLeaves(leaves, event.getSource());
        return leaves;
    }

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

//        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.flags = AccessibilityServiceInfo.DEFAULT;
//        serviceInfo.packageNames = new String[]{"com.android.phone"};
//        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        setServiceInfo(serviceInfo);
    }
}
