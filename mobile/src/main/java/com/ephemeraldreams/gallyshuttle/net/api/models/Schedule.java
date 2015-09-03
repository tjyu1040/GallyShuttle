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

package com.ephemeraldreams.gallyshuttle.net.api.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plain old Java object model for a schedule.
 */
public class Schedule {

    public String name;
    public String path;
    public LinkedHashMap<Station, ArrayList<String>> stationsTimes;

    public Schedule(String name, LinkedHashMap<Station, ArrayList<String>> stationsTimes) {
        this.name = name;
        this.stationsTimes = stationsTimes;
    }

    /**
     * Get the count of stations.
     *
     * @return Count of stations.
     */
    public int getStationsCount() {
        return stationsTimes.keySet().size();
    }

    /**
     * Get stop at specified position.
     *
     * @param position Position to get stop.
     * @return Name of stop.
     */
    public Station getStation(int position) {
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
     * Get station times entry at specified position.
     *
     * @param position Position to get times.
     * @return Map entry of stop and times.
     */
    private Map.Entry<Station, ArrayList<String>> getStationTimes(int position) {
        int i = 0;
        for (Map.Entry<Station, ArrayList<String>> entry : stationsTimes.entrySet()) {
            if (i == position) {
                return entry;
            } else {
                i++;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", stationsTimes=" + stationsTimes +
                '}';
    }
}
