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

package com.ephemeraldreams.gallyshuttle.backend.models;

import java.util.ArrayList;

/**
 * POJO class representing times for a station.
 */
public class StationTimes {

    public Station station;
    public ArrayList<String> stationTimes;

    private StationTimes() {
    }

    public StationTimes(Station station, ArrayList<String> stationTimes) {
        this.station = station;
        this.stationTimes = stationTimes;
    }
}