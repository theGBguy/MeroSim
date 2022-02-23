/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/05/31
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

package com.gbsoft.merosim.utils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

/*
* This class is intended to use with Event class to implement
* observer pattern for events that we can handle only once.
*/

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
