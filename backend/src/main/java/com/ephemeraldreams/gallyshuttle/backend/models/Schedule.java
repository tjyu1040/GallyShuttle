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

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

/**
 * Entity class representing a shuttle schedule.
 */
@Entity
public class Schedule {

    @Id public String scheduleName;
    @Index public String pathName;
    public ArrayList<StationTimes> stationsTimes;

    private Schedule() {
    }

    public Schedule(String scheduleName, String pathName) {
        this.scheduleName = scheduleName;
        this.pathName = pathName;
    }

    public void setStationsTimes(ArrayList<StationTimes> stationsTimes) {
        this.stationsTimes = stationsTimes;
    }
}
