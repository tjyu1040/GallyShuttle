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

package com.ephemeraldreams.gallyshuttle.backend;

import com.ephemeraldreams.gallyshuttle.backend.models.Station;

public class Constants {

    public static final int CONTINUOUS_ID = 0;
    public static final int ALT_CONTINUOUS_ID = 1;
    public static final int LATE_NIGHT_ID = 2;
    public static final int MODIFIED_ID = 3;
    public static final int WEEKEND_ID = 4;

    public static final String CONTINUOUS_NAME = "Continuous";
    public static final String ALT_CONTINUOUS_NAME = "Alternate Continuous";
    public static final String LATE_NIGHT_NAME = "Late Night";
    public static final String MODIFIED_NAME = "Modified";
    public static final String WEEKEND_NAME = "Weekend";
    public static final String[] SCHEDULES_NAMES = {
            CONTINUOUS_NAME,
            ALT_CONTINUOUS_NAME,
            LATE_NIGHT_NAME,
            MODIFIED_NAME,
            WEEKEND_NAME
    };

    public static final String CONTINUOUS_PATH_NAME = "continuous";
    public static final String ALT_CONTINUOUS_PATH_NAME = "alt_continuous";
    public static final String LATE_NIGHT_PATH_NAME = "late_night";
    public static final String MODIFIED_PATH_NAME = "modified";
    public static final String WEEKEND_PATH_NAME = "weekend";
    public static final String[] SCHEDULES_PATH_NAMES = {
            CONTINUOUS_PATH_NAME,
            ALT_CONTINUOUS_PATH_NAME,
            LATE_NIGHT_PATH_NAME,
            MODIFIED_PATH_NAME,
            WEEKEND_PATH_NAME
    };

    private static final String API_URL = "https://spreadsheets.google.com/feeds/list/1P5VNOEitcInyNUUGCN7FNcSzvevpeZ530MP76uu5qB4";
    private static final String CONTINUOUS_URL = "/1/public/values?alt=json";
    private static final String ALT_CONTINUOUS_URL = "/2/public/values?alt=json";
    private static final String LATE_NIGHT_URL = "/3/public/values?alt=json";
    private static final String MODIFIED_URL = "/4/public/values?alt=json";
    private static final String WEEKEND_URL = "/5/public/values?alt=json";
    public static final String[] SCHEDULE_URLS = {
            API_URL + CONTINUOUS_URL,
            API_URL + ALT_CONTINUOUS_URL,
            API_URL + LATE_NIGHT_URL,
            API_URL + MODIFIED_URL,
            API_URL + WEEKEND_URL
    };

    public static final String NOMA_GALLAUDET_STATION_NAME = "NoMa-Gallaudet U";
    public static final String UNION_STATION_NAME = "Union Station";
    public static final String MSSD_STATION_NAME = "MSSD";
    public static final String KDES_STATION_NAME = "KDES";
    public static final String BENSON_HALL_STATION_NAME = "Benson Hall";
    public static final String KELLOGG_STATION_NAME = "Kellogg";

    public static final String NOMA_GALLAUDET_STATION_PATH_NAME = "noma_gallaudet";
    public static final String UNION_STATION_PATH_NAME = "union";
    public static final String MSSD_STATION_PATH_NAME = "mssd";
    public static final String KDES_STATION_PATH_NAME = "kdes";
    public static final String BENSON_HALL_STATION_PATH_NAME = "benson";
    public static final String KELLOGG_STATION_PATH_NAME = "kellogg";

    public static final Station NOMA_GALLAUDET_STATION = new Station(NOMA_GALLAUDET_STATION_NAME, NOMA_GALLAUDET_STATION_PATH_NAME);
    public static final Station UNION_STATION = new Station(UNION_STATION_NAME, UNION_STATION_PATH_NAME);
    public static final Station MSSD_STATION = new Station(MSSD_STATION_NAME, MSSD_STATION_PATH_NAME);
    public static final Station KDES_STATION = new Station(KDES_STATION_NAME, KDES_STATION_PATH_NAME);
    public static final Station BENSON_HALL_STATION = new Station(BENSON_HALL_STATION_NAME, BENSON_HALL_STATION_PATH_NAME);
    public static final Station KELLOGG_STATION = new Station(KELLOGG_STATION_NAME, KELLOGG_STATION_PATH_NAME);

    public static final Station[] ALL_STATIONS = {
            NOMA_GALLAUDET_STATION,
            UNION_STATION,
            MSSD_STATION,
            KDES_STATION,
            BENSON_HALL_STATION,
            KELLOGG_STATION
    };

    public static final Station[] CONTINUOUS_STATIONS = {
            NOMA_GALLAUDET_STATION,
            UNION_STATION,
            MSSD_STATION,
            KDES_STATION,
            BENSON_HALL_STATION,
            KELLOGG_STATION
    };

    public static final Station[] ALT_CONTINUOUS_STATIONS = {
            NOMA_GALLAUDET_STATION,
            UNION_STATION,
            MSSD_STATION,
            KDES_STATION,
            BENSON_HALL_STATION,
            KELLOGG_STATION
    };

    public static final Station[] LATE_NIGHT_STATIONS = {
            BENSON_HALL_STATION,
            KELLOGG_STATION,
            UNION_STATION
    };

    public static final Station[] MODIFIED_STATIONS = {
            BENSON_HALL_STATION,
            KELLOGG_STATION,
            UNION_STATION
    };

    public static final Station[] WEEKEND_STATIONS = {
            BENSON_HALL_STATION,
            KELLOGG_STATION,
            NOMA_GALLAUDET_STATION,
            UNION_STATION
    };

    public static final Station[][] SCHEDULES_STATIONS = {
            CONTINUOUS_STATIONS,
            ALT_CONTINUOUS_STATIONS,
            LATE_NIGHT_STATIONS,
            MODIFIED_STATIONS,
            WEEKEND_STATIONS
    };
}
