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

package com.ephemeraldreams.gallyshuttle.data.preferences;

import android.content.SharedPreferences;

/**
 * Wrapper class for an integer key preference.
 */
public class IntegerPreference {

    private final SharedPreferences sharedPreferences;
    private final String key;
    private final int defaultValue;

    public IntegerPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, 0);
    }

    public IntegerPreference(SharedPreferences sharedPreferences, String key, int defaultValue) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public int get() {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean isSet() {
        return sharedPreferences.contains(key);
    }

    public void set(int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void delete() {
        sharedPreferences.edit().remove(key).apply();
    }
}
