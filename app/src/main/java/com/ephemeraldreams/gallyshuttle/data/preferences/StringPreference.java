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
 * Wrapper class for a string key preference.
 */
public class StringPreference {

    private final SharedPreferences sharedPreferences;
    private final String key;
    private final String defaultValue;

    public StringPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, null);
    }

    public StringPreference(SharedPreferences sharedPreferences, String key, String defaultValue) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Get string preference value from preference key.
     *
     * @return String preference value.
     */
    public String get() {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Check whether preference key exists in shared preferences.
     *
     * @return true if preference key exists, false otherwise.
     */
    public boolean isSet() {
        return sharedPreferences.contains(key);
    }

    /**
     * Set a new preference value for preference key.
     *
     * @param value New preference value to set.
     */
    public void set(String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Remove preference key from shared preferences.
     */
    public void delete() {
        sharedPreferences.edit().remove(key).apply();
    }
}
