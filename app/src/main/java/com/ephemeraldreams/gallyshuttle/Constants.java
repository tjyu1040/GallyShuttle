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

package com.ephemeraldreams.gallyshuttle;

/**
 * Project-specific constants
 */
public class Constants {

    public static final String ENDPOINT_URL = "http://www.gallaudet.edu/transportation/shuttle_bus_services";
    public static final String CONTINUOUS_URL = ENDPOINT_URL + "/continuous_shuttle_schedule.html";
    public static final String LATE_NIGHT_URL = ENDPOINT_URL + "/late_night_shuttle_service.html";
    public static final String WEEKEND_URL = ENDPOINT_URL + "/weekend_shuttle_schedule.html";
    public static final String MODIFIED_URL = ENDPOINT_URL + "/modified_schedule.html";

    public static final String CONTINUOUS_NAME = "continuous";
    public static final String LATE_NIGHT_NAME = "late_night";
    public static final String WEEKEND_NAME = "weekend";
    public static final String MODIFIED_NAME = "modified";

    private Constants() {
        throw new ClassCastException();
    }
}