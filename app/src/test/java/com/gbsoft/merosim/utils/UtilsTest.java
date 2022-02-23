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

package com.gbsoft.merosim.utils;

import com.gbsoft.merosim.model.Sim;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void getPinFromLine() {
        String ntcPin = "kldjljlsa 50751.321616 86316\n";
        String ncellPin = "1234567890987654\nklajkldajlaj12341234 1234 1234\n";
        String smartPin = "kaljdlakja 1234 12345+1234\n";

        System.out.println(Utils.getPinFromLine(Sim.NAMASTE, ntcPin));
        System.out.println(Utils.getPinFromLine(Sim.NCELL, ncellPin));
        System.out.println(Utils.getPinFromLine(Sim.SMART_CELL, smartPin));
    }
}