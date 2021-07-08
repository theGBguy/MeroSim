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

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class EventObserver<T> implements Observer<Event<T>> {
    private final Listener<T> listener;

    public EventObserver(Listener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onChanged(@Nullable Event<T> tEvent) {
        if (tEvent == null) return;
        if (listener == null) return;

        T content = tEvent.getContentIfNotHandled();
        if (content == null) return;
        listener.onUnhandledContent(content);
    }

    public interface Listener<T> {
        void onUnhandledContent(T data);
    }
}
