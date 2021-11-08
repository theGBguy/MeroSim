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
 * Last modified: 2021/10/26
 */

package com.gbsoft.merosim.utils;

import static org.junit.Assert.*;

import com.gbsoft.merosim.data.Sim;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void getPinFromLine() {
        String ntcPin = "kldjljlsa 50751.321616 86316\n";
        String ncellPin = "klajkldajlaj12341234 1234 1234\n";
        String smartPin = "kaljdlakja 1234 12345+1234\n";

        System.out.println(Utils.getPinFromLine(Sim.NAMASTE, ntcPin));
        System.out.println(Utils.getPinFromLine(Sim.NCELL, ncellPin));
        System.out.println(Utils.getPinFromLine(Sim.SMART_CELL, smartPin));
    }
}