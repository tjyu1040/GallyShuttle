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

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Entity class representing a shuttle schedule. {@link #stationsTimes} is intentionally
 * ignored and not persisted in this entity since LinkedHashMap is stored as an unordered hash
 * map in the Google App Engine datastore.
 * <p/>
 * Instead, {@link #stationTimesList} is used to store the station times in the datastore, and
 * we call {@link #onLoad()} to convert {@link #stationTimesList} to {@link #stationsTimes} which
 * can then be sent through Google Cloud Endpoints.
 */
@Entity
public class Schedule {

    @Id public String name;
    @Index public String path;
    private ArrayList<StationTimes> stationTimesList;

    @Ignore public LinkedHashMap<Station, ArrayList<String>> stationsTimes;

    private Schedule() {
    }

    public Schedule(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void setStationTimesList(ArrayList<StationTimes> stationTimesList) {
        this.stationTimesList = stationTimesList;
    }

    /**
     * Intentionally ignored to prevent sending {@link #stationTimesList} through Google Cloud
     * Endpoints.
     *
     * @return List of {@link StationTimes}.
     */
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public ArrayList<StationTimes> getStationTimesList() {
        return stationTimesList;
    }

    /**
     * Convert {@link #stationTimesList} to {@link #stationsTimes} on loading from Google App
     * Engine datastore.
     */
    @OnLoad
    void onLoad() {
        if (this.stationTimesList != null) {
            this.stationsTimes = new LinkedHashMap<>();
            for (StationTimes stationTimes : this.stationTimesList) {
                this.stationsTimes.put(stationTimes.station, stationTimes.stationTimes);
            }
        }
    }
}
