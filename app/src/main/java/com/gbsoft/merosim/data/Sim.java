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

package com.gbsoft.merosim.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Sim implements Parcelable {
    public static final String NAMASTE = "Namaste";
    public static final String NCELL = "Ncell";
    public static final String SMART_CELL = "SmartCell";
    public static final String NONE = "None";

    public static final String START = "START";
    public static final String STATUS = "STATUS";
    public static final String STOP = "STOP";

    public static final String UNAVAILABLE = "(unavailable)";

    private String name;
    private String phoneNo;
    private String balance;
    private String simOwner;
    private int simSlotIndex;

    public Sim(String name, String phoneNo, String balance, String simOwner, int simSlotIndex) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.balance = balance;
        this.simOwner = simOwner;
        this.simSlotIndex = simSlotIndex;
    }

    protected Sim(Parcel in) {
        name = in.readString();
        phoneNo = in.readString();
        balance = in.readString();
        simOwner = in.readString();
        simSlotIndex = in.readInt();
    }

    public static final Creator<Sim> CREATOR = new Creator<Sim>() {
        @Override
        public Sim createFromParcel(Parcel in) {
            return new Sim(in);
        }

        @Override
        public Sim[] newArray(int size) {
            return new Sim[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getSimOwner() {
        return simOwner;
    }

    public void setSimOwner(String simOwner) {
        this.simOwner = simOwner;
    }

    public int getSimSlotIndex() {
        return simSlotIndex;
    }

    public void setSimSlotIndex(int simSlotIndex) {
        this.simSlotIndex = simSlotIndex;
    }

    @NotNull
    @Override
    public String toString() {
        return "Sim{" +
                "name='" + name + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", balance='" + balance + '\'' +
                ", simOwner='" + simOwner + '\'' +
                ", simSlotIndex=" + simSlotIndex +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNo);
        dest.writeString(balance);
        dest.writeString(simOwner);
        dest.writeInt(simSlotIndex);
    }
}
