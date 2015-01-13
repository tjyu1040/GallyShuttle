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

package com.ephemeraldreams.gallyshuttle.models;

import com.google.gson.Gson;

import java.io.BufferedReader;

/**
 * Helper class to convert {@link com.ephemeraldreams.gallyshuttle.models.Schedule} Schedule object
 * into a JSON string or to convert a JSON file into a {@link com.ephemeraldreams.gallyshuttle.models.Schedule}
 * object.
 */
public class GsonHelper {

    public static final Gson GSON = new Gson();

    public static String toJsonString(Schedule schedule) {
        return GSON.toJson(schedule);
    }

    public static Schedule fromJsonFile(BufferedReader bufferedReader) {
        return GSON.fromJson(bufferedReader, Schedule.class);
    }
}