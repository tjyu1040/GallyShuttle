/*
 * Copyright 2014 Timothy Yu
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

package com.ephemeraldreams.gallyshuttle.content.database.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Model representing an alarm reminder for shuttle arrival at a station.
 * TODO: build an alarm database for synchronization and storage within app.
 */
public class Alarm {

    private static final String ALARM_AUTO_INCREMENT_ID_KEY = "realm.alarm.auto.increment.id.key";

    private int id;
    private String stationName;
    private String arrivalTime;
    private long triggerTime;

    public Alarm() {

    }

    private Alarm(Builder builder) {
        this.id = builder.id;
        this.arrivalTime = builder.arrivalTime;
        this.stationName = builder.stationName;
        this.triggerTime = builder.triggerTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    /**
     * Builder class for {@link Alarm}.
     */
    public static class Builder {

        private final int id;
        private String stationName;
        private String arrivalTime;
        private long triggerTime;

        public Builder(Context context) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            this.id = sharedPreferences.getInt(ALARM_AUTO_INCREMENT_ID_KEY, 0);
            sharedPreferences.edit().putInt(ALARM_AUTO_INCREMENT_ID_KEY, id + 1).apply();
        }

        public Builder setStationName(String stationName) {
            this.stationName = stationName;
            return this;
        }

        public Builder setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public Builder setTriggerTime(long triggerTime) {
            this.triggerTime = triggerTime;
            return this;
        }

        public Alarm build() {
            return new Alarm(this);
        }
    }
}
