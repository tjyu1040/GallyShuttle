/*
 * Copyright (C) 2014 Timothy Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Parcelable object to store the name, stops, and times of a schedule.
 */
public class Schedule implements Parcelable {

    public String name;
    public ArrayList<String> stops;
    public ArrayList<ArrayList<String>> times;

    public Schedule(String name, ArrayList<String> stops, ArrayList<ArrayList<String>> times) {
        this.name = name;
        this.stops = stops;
        this.times = times;
    }

    @SuppressWarnings("unchecked")
    private Schedule(Parcel parcel) {
        name = parcel.readString();
        stops = parcel.readArrayList(String.class.getClassLoader());
        times = parcel.readArrayList(ArrayList.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(name);
        destination.writeList(stops);
        destination.writeList(times);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public String toString() {
        return "Schedule [name=" + name + ", stops=" + stops + ", times=" + times + "]";
    }
}