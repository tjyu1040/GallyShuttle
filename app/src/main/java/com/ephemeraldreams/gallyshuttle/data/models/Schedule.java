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

import android.content.res.Resources;

import com.ephemeraldreams.gallyshuttle.BuildConfig;
import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.data.ScheduleUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plain old Java object model for schedule.
 */
public class Schedule {

    public String cacheVersion;
    public final String title;
    public final LinkedHashMap<String, ArrayList<String>> stationsTimes;

    public Schedule() {
        this("Empty", null);
        this.cacheVersion = BuildConfig.VERSION_NAME;
    }

    public Schedule(int scheduleId, ApiResponse apiResponse, Resources resources) {
        this.cacheVersion = BuildConfig.VERSION_NAME;
        this.title = ScheduleUtils.getScheduleTitle(scheduleId, resources);
        this.stationsTimes = ScheduleUtils.parseEntries(scheduleId, apiResponse.feed.entries, resources);
    }

    public Schedule(String title, LinkedHashMap<String, ArrayList<String>> stationsTimes) {
        this.cacheVersion = BuildConfig.VERSION_NAME;
        this.title = title;
        this.stationsTimes = stationsTimes;
    }

    /**
     * Get the number of stops.
     *
     * @return Number of stops.
     */
    public int getNumberOfStations() {
        return stationsTimes.keySet().size();
    }

    /**
     * Get stop at specified position.
     *
     * @param position Position to get stop.
     * @return Name of stop.
     */
    public String getStation(int position) {
        return getStationTimes(position).getKey();
    }

    /**
     * Get times at specified position.
     *
     * @param position Position to get times.
     * @return ArrayList of times.
     */
    public ArrayList<String> getTimes(int position) {
        return getStationTimes(position).getValue();
    }

    /**
     * Get stop times entry at specified position.
     *
     * @param position Position to get times.
     * @return Map entry of stop and times.
     */
    private Map.Entry<String, ArrayList<String>> getStationTimes(int position) {
        int i = 0;
        for (Map.Entry<String, ArrayList<String>> entry : stationsTimes.entrySet()) {
            if (i++ == position) {
                return entry;
            }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        return !(title != null ? !title.equals(schedule.title) : schedule.title != null) && !(stationsTimes != null ? !stationsTimes.equals(schedule.stationsTimes) : schedule.stationsTimes != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (stationsTimes != null ? stationsTimes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ScheduleUtils.toJsonString(this);
    }
}
