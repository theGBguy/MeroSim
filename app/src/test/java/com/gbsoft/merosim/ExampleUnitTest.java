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

package com.gbsoft.merosim;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void printASCII() {
        for (char i = '0'; i <= '9'; i++) {
            System.out.println((int) i);
        }
        System.out.println((int) '०');
        System.out.println((int) '१');
        System.out.println((int) '८');
        System.out.println((int) '९');
    }

    @Test
    public void regexText(){
        String ntcPhoneResponse = "MSISDN: 9779843776289";
        String ncellPhoneResponse = "Your number is 9810374988, Status:Active, Active Date.....";

        // Compile regular expression
        Pattern pattern = Pattern.compile("9?7?7?(\\d{10})", Pattern.CASE_INSENSITIVE);

        // Match regex against input
        Matcher matcher = pattern.matcher(ntcPhoneResponse);
        // Use results...
        if (matcher.find())
            System.out.println(matcher.group(1));

        // Match regex against input
        matcher = pattern.matcher(ncellPhoneResponse);
        // Use results...
        if (matcher.find())
            System.out.println(matcher.group(1));
    }
}