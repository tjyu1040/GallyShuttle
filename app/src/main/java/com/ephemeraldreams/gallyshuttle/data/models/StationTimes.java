/*
 *  Copyright (C) 2014 Timothy Yu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.data.models;

import android.os.Parcelable;

import org.joda.time.LocalTime;

import java.util.ArrayList;

import auto.parcel.AutoParcel;

/**
 * Parcelable wrapper class for ArrayList of {@link LocalTime}s.
 */
@AutoParcel
public abstract class StationTimes implements Parcelable {

    public static StationTimes create(ArrayList<LocalTime> times) {
        return new AutoParcel_StationTimes(times);
    }

    public abstract ArrayList<LocalTime> times();
}
