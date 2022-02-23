/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/21
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocaleUtilsTest {

    @Test
    public void getPinCodeInNepaliDigit() {
        String pinEnglish = "1234567890";
        assertEquals("१२३४५६७८९०", LocaleUtils.getNumberInNepaliDigit(pinEnglish));
    }
}