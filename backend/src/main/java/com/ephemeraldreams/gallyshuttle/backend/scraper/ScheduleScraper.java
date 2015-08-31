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

package com.ephemeraldreams.gallyshuttle.backend.scraper;

import com.ephemeraldreams.gallyshuttle.backend.Constants;
import com.ephemeraldreams.gallyshuttle.backend.models.Schedule;
import com.ephemeraldreams.gallyshuttle.backend.models.Station;
import com.ephemeraldreams.gallyshuttle.backend.models.StationTimes;
import com.ephemeraldreams.gallyshuttle.backend.scraper.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.backend.scraper.models.Entry;
import com.ephemeraldreams.gallyshuttle.backend.scraper.models.Time;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ephemeraldreams.gallyshuttle.backend.Constants.BENSON_HALL_STATION;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.KDES_STATION;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.KELLOGG_STATION;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.MSSD_STATION;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.NOMA_GALLAUDET_STATION;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.SCHEDULES_NAMES;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.SCHEDULES_PATH_NAMES;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.SCHEDULES_STATIONS;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.SCHEDULE_URLS;
import static com.ephemeraldreams.gallyshuttle.backend.Constants.UNION_STATION;

public class ScheduleScraper {

    /**
     * Scrap data from a specific schedule.
     *
     * @param scheduleId Id of schedule.
     * @return {@link Schedule}.
     * @throws IOException
     * @see Constants#CONTINUOUS_ID
     * @see Constants#ALT_CONTINUOUS_ID
     * @see Constants#LATE_NIGHT_ID
     * @see Constants#MODIFIED_ID
     * @see Constants#WEEKEND_ID
     */
    public static Schedule scrape(int scheduleId) throws IOException {
        URL url = new URL(SCHEDULE_URLS[scheduleId]);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            ApiResponse apiResponse = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), ApiResponse.class);
            LinkedHashMap<Station, ArrayList<String>> stationsTimesMap = parseEntries(scheduleId, apiResponse.feed.entries);

            String scheduleName = SCHEDULES_NAMES[scheduleId];
            String schedulePathName = SCHEDULES_PATH_NAMES[scheduleId];
            ArrayList<StationTimes> stationsTimes = new ArrayList<>();
            for (Station station : stationsTimesMap.keySet()) {
                stationsTimes.add(new StationTimes(station, stationsTimesMap.get(station)));
            }

            Schedule schedule = new Schedule(scheduleName, schedulePathName);
            schedule.setStationsTimes(stationsTimes);
            return schedule;
        }
        return null;
    }

    /**
     * Parse entries of station times into a map.
     *
     * @param scheduleId Id of schedule.
     * @param entries    Entries of scrapped schedule data.
     * @return ArrayList of times.
     */
    private static LinkedHashMap<Station, ArrayList<String>> parseEntries(int scheduleId, List<Entry> entries) {

        Station[] stations = SCHEDULES_STATIONS[scheduleId];

        LinkedHashMap<Station, ArrayList<String>> stationsTimesMap = new LinkedHashMap<>();

        for (Station station : stations) {
            stationsTimesMap.put(station, new ArrayList<String>());
        }
        for (Entry entry : entries) {
            putTimeToStation(stationsTimesMap, BENSON_HALL_STATION, entry.bensonStationTime);
            putTimeToStation(stationsTimesMap, KELLOGG_STATION, entry.kelloggStationTime);
            putTimeToStation(stationsTimesMap, UNION_STATION, entry.unionStationTime);
            putTimeToStation(stationsTimesMap, MSSD_STATION, entry.mssdStationTime);
            putTimeToStation(stationsTimesMap, KDES_STATION, entry.kdesStationTime);
            putTimeToStation(stationsTimesMap, NOMA_GALLAUDET_STATION, entry.noMaGallaudetStationTime);
        }

        return stationsTimesMap;
    }

    /**
     * Put time to mapped station.
     *
     * @param stationsTimes LinkedHashMap to put a time to a station.
     * @param station       Station key to put time to.
     * @param time          Time to put to station key.
     */
    private static void putTimeToStation(LinkedHashMap<Station, ArrayList<String>> stationsTimes, Station station, Time time) {
        if (stationsTimes.containsKey(station) &&
                !Strings.isNullOrEmpty(time.toString()) &&
                !time.toString().contains("-")) {
            stationsTimes.get(station).add(trimAndFormat(time.toString()));
        }
    }

    /**
     * Strip and format a String time.
     *
     * @param time String time to trim and format.
     * @return Trimmed and formatted time.
     */
    public static String trimAndFormat(String time) {
        return time.toUpperCase()
                .replace("*", "")           // Remove * characters
                .replaceAll("\u00a0", " ")  // Remove &nbsp characters
                .replaceAll("\\.", "")      // Remove . characters
                .replace("NOON", "PM")      // Replace NOON with PM
                .trim();
    }
}
