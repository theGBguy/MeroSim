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
 * Last modified: 2021/06/04
 */

package com.gbsoft.merosim.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TelephonyUtilsTest {

    @Test
    public void getBalanceText() {
        String ntcBalanceQueryResponse = "21293 Your balance is Rs. 995.75 test Rs.12";
        String ncellBalanceQueryResponse = "12113 Balance:Rs. 403.89. test Rs. 12";

        assertEquals("Rs. 995.75", TelephonyUtils.getBalanceText(ntcBalanceQueryResponse));
        assertEquals("Rs. 403.89", TelephonyUtils.getBalanceText(ncellBalanceQueryResponse));
    }

    @Test
    public void getSimOwnerText() {
        String ntcSimOwnerQueryResponse = "This SIM is registered in the Name:ISHWOR " +
                "KUMAR(9779843776289)";
        String ncellSimOwnerQueryResponse = "This SIM is registered in the name of Sabina " +
                "Pandey.";

        assertEquals("ISHWOR KUMAR", TelephonyUtils.getSimOwnerText(ntcSimOwnerQueryResponse));
        assertEquals("Sabina Pandey", TelephonyUtils.getSimOwnerText(ncellSimOwnerQueryResponse));
    }

    @Test
    public void getPhoneText() {
        String ntcPhoneResponse = "MSISDN: 9779843776289";
        String ncellPhoneResponse = "Your number is 9810374988, Status:Active, Active Date.....";

        assertEquals("9843776289", TelephonyUtils.getPhoneText(ntcPhoneResponse));
        assertEquals("9810374988", TelephonyUtils.getPhoneText(ncellPhoneResponse));
    }

    @Test
    public void getSubsInfoList() {
    }

    @Test
    public void sendUssdRequest() {
    }

    @Test
    public void getSimSlotIndex() {
    }

    @Test
    public void getRechargeUssdRequest() {
    }

    @Test
    public void getSimList() {
    }

    @Test
    public void dial() {
    }

    @Test
    public void call() {
    }

    @Test
    public void sendSms() {
    }
}