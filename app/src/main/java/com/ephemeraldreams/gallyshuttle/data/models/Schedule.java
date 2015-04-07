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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plain old Java object model for schedule.
 */
public class Schedule {

    public String title;
    public LinkedHashMap<String, ArrayList<String>> stopsTimes;

    public Schedule(String title, LinkedHashMap<String, ArrayList<String>> stopsTimes) {
        this.title = title;
        this.stopsTimes = stopsTimes;
    }

    /**
     * Get the number of stops.
     *
     * @return Number of stops.
     */
    public int getNumberOfStops() {
        return stopsTimes.keySet().size();
    }

    /**
     * Get stop at specified position.
     *
     * @param position Position to get stop.
     * @return Name of stop.
     */
    public String getStop(int position) {
        return getStopTimes(position).getKey();
    }

    /**
     * Get times at specified position.
     *
     * @param position Position to get times.
     * @return ArrayList of times.
     */
    public ArrayList<String> getTimes(int position) {
        return getStopTimes(position).getValue();
    }

    /**
     * Get stop times at specified position.
     *
     * @param position Position to get times.
     * @return Map entry of stop and times.
     */
    public Map.Entry<String, ArrayList<String>> getStopTimes(int position) {
        int i = 0;
        for (Map.Entry<String, ArrayList<String>> entry : stopsTimes.entrySet()) {
            if (i++ == position) {
                return entry;
            }
        }
        return null;
    }
}
