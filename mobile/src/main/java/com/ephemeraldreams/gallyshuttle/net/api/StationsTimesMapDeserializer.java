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

package com.ephemeraldreams.gallyshuttle.net.api;

import com.ephemeraldreams.gallyshuttle.net.api.models.Station;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class to deserialize JSON map format into a LinkedHashMap of stations and times.
 */
public class StationsTimesMapDeserializer implements JsonDeserializer<LinkedHashMap<Station, ArrayList<String>>> {

    private static final Gson GSON = new Gson();
    private static final Type listType = new TypeToken<List<String>>() {}.getType();

    @Override
    public LinkedHashMap<Station, ArrayList<String>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LinkedHashMap<Station, ArrayList<String>> map = new LinkedHashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        String key;
        JsonElement value;
        Station station;
        ArrayList<String> times;
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            key = entry.getKey();
            station = GSON.fromJson(key, Station.class);
            value = entry.getValue();
            times = GSON.fromJson(value, listType);
            map.put(station, times);
        }
        return map;
    }
}
