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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void regexText() {
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

    @Test
    public void dataPackRegexTest() {
        String response = "1)SUMMER\n" +
                "2)Social Media\n" +
                "3)Unlimited Night Data\n" +
                "4)Voice\n" +
                "5)4G\n" +
                "6)GB/Day\n" +
                "7)INT. SERVICES\n" +
                "8)UNLIMITED\n" +
                "9)Day Packs\n" +
                "10)StayConnected\n" +
                "11)Festive Offer ";

        String ncellResponse = "1] Top Selling\n" +
                "2] Rs 22-1 hr\n" +
                "3] Popular Packs\n" +
                "4] 1Day\n" +
                "5] 3 Day\n" +
                "6] 30 day\n" +
                "7] Mero Plan\n" +
                "8] LockDown Offer\n" +
                "9] Student Plan\n" +
                "1] Gajjabko Daily GB pack\n" +
                "2] Triple Majja\n" +
                "3] YouTube Nonstop\n" +
                "4] Facebook Nonstop\n" +
                "5] TikTok Nonstop\n" +
                "6] NonstopYouTube&Facebook +4GB @Rs 191 /7day ";

        // Compile regular expression
        Pattern pattern = Pattern.compile("([\\w/.\\-&+@]+\\s)+", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        Matcher packNameMatcher = pattern.matcher(ncellResponse);

        while (packNameMatcher.find()) {
            padDataPackLabel(packNameMatcher.group().trim());
        }

        Pattern countPattern = Pattern.compile("(\\d+).*$", Pattern.CASE_INSENSITIVE);
        Matcher countMatcher = countPattern.matcher(ncellResponse);
        while (countMatcher.find()) {
            System.out.println("\n" + countMatcher.group(1));
        }
    }

    public void padDataPackLabel(String dataPackName) {
        String padded = dataPackName + new String(new char[47 - dataPackName.length()]).replace('\0', ' ') + ">";
        System.out.println(padded + " length : " + padded.length());
    }
}