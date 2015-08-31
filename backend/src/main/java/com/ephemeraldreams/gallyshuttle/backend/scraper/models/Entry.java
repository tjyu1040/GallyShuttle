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

package com.ephemeraldreams.gallyshuttle.backend.scraper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * JSON object model for entry.
 */
public class Entry {

    @Expose @SerializedName("gsx$nomagallaudet")
    public Time noMaGallaudetStationTime;

    @Expose @SerializedName("gsx$unionstation")
    public Time unionStationTime;

    @Expose @SerializedName("gsx$mssd")
    public Time mssdStationTime;

    @Expose @SerializedName("gsx$kdes")
    public Time kdesStationTime;

    @Expose @SerializedName("gsx$benson")
    public Time bensonStationTime;

    @Expose @SerializedName("gsx$kellogg")
    public Time kelloggStationTime;

    public String toString() {
        return "No-Ma Gallaudet: " + noMaGallaudetStationTime + ", " +
                "Union Station: " + unionStationTime + ", " +
                "MSSD: " + mssdStationTime + ", " +
                "KDES: " + kdesStationTime + ", " +
                "Benson Hall: " + bensonStationTime + ", " +
                "Kellog: " + kelloggStationTime;
    }
}
