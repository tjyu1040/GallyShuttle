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

package com.ephemeraldreams.gallyshuttle.data;

import android.content.res.Resources;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.api.models.Entry;
import com.ephemeraldreams.gallyshuttle.api.models.StationTime;
import com.ephemeraldreams.gallyshuttle.data.models.Schedule;
import com.ephemeraldreams.gallyshuttle.util.DateUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Utility class for handling schedules.
 */
public class ScheduleUtils {

    //private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeSerializer()).create();
    private static final Gson GSON = new Gson();

    /**
     * Convert Schedule POJO to JSON string representation.
     *
     * @param schedule Schedule to parse.
     * @return JSON string representation of schedule.
     */
    public static String toJsonString(Schedule schedule) {
        return GSON.toJson(schedule);
    }

    /**
     * Convert JSON file to Schedule POJO.
     *
     * @param bufferedReader Buffered reader to parse file.
     * @return Schedule POJO.
     */
    public static Schedule fromJsonFile(BufferedReader bufferedReader) {
        return GSON.fromJson(bufferedReader, Schedule.class);
    }

    /**
     * Get schedule title based on schedule id.
     *
     * @param scheduleId Schedule id.
     * @param resources  Resources to retrieve strings.
     * @return Title of schedule.
     */
    public static String getScheduleTitle(int scheduleId, Resources resources) {
        switch (scheduleId) {
            case R.array.continuous_stations:
                return resources.getString(R.string.schedule_title_continuous);
            case R.array.alt_continuous_stations:
                return resources.getString(R.string.schedule_title_alt_continuous);
            case R.array.late_night_stations:
                return resources.getString(R.string.schedule_title_late_night);
            case R.array.weekend_stations:
                return resources.getString(R.string.schedule_title_weekend);
            case R.array.modified_stations:
                return resources.getString(R.string.schedule_title_modified);
            default:
                return resources.getString(R.string.schedule_title_continuous);
        }
    }

    /**
     * Populate a linked map with stops and times.
     *
     * @param scheduleId Schedule id.
     * @param entries    List of entries to iterate over.
     * @param resources  Resources to retrieve strings.
     * @return LinkedHashMap of stops to times.
     */
    public static LinkedHashMap<String, ArrayList<String>> parseEntries(int scheduleId, List<Entry> entries, Resources resources) {

        String[] stops = resources.getStringArray(scheduleId);
        LinkedHashMap<String, ArrayList<String>> stopTimes = new LinkedHashMap<>();
        for (String stop : stops) {
            stopTimes.put(stop, new ArrayList<String>());
        }

        String bensonStationName = resources.getString(R.string.benson_station_name);
        String kelloggStationName = resources.getString(R.string.kellogg_station_name);
        String unionStationName = resources.getString(R.string.union_station_name);
        String mssdStationName = resources.getString(R.string.mssd_station_name);
        String kdesStationName = resources.getString(R.string.kdes_station_name);
        String noMaGallaudetStationName = resources.getString(R.string.no_ma_gallaudet_station_name);

        for (Entry entry : entries) {
            putTimeToStop(stopTimes, bensonStationName, entry.bensonStationTime);
            putTimeToStop(stopTimes, kelloggStationName, entry.kelloggStationTime);
            putTimeToStop(stopTimes, unionStationName, entry.unionStationTime);
            putTimeToStop(stopTimes, mssdStationName, entry.mssdStationTime);
            putTimeToStop(stopTimes, kdesStationName, entry.kdesStationTime);
            putTimeToStop(stopTimes, noMaGallaudetStationName, entry.noMaGallaudetStationTime);
        }
        return stopTimes;
    }

    /**
     * Put time to mapped stop.
     *
     * @param stopsTimes  LinkedHashMap to put time.
     * @param key         Stop key to map time to.
     * @param stationTime Station time to store into map.
     */
    private static void putTimeToStop(LinkedHashMap<String, ArrayList<String>> stopsTimes, String key, StationTime stationTime) {
        if (stopsTimes.containsKey(key) && !TextUtils.isEmpty(stationTime.toString()) && !stationTime.toString().contains("-")) {
            stopsTimes.get(key).add(DateUtils.trimAndFormat(stationTime.toString()));
        }
    }
}
