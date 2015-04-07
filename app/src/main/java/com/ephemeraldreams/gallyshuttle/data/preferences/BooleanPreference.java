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
 * Wrapper class for a boolean key preference.
 */
public class BooleanPreference {

    private final SharedPreferences sharedPreferences;
    private final String key;
    private final boolean defaultValue;

    public BooleanPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, false);
    }

    public BooleanPreference(SharedPreferences sharedPreferences, String key, boolean defaultValue) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Get boolean preference value from preference key.
     *
     * @return Boolean preference value.
     */
    public boolean get() {
        return sharedPreferences.getBoolean(key, defaultValue);
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
     * Set a new boolean preference value for preference key.
     *
     * @param value New boolean preference value to set.
     */
    public void set(boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Remove preference key from shared preferences.
     */
    public void delete() {
        sharedPreferences.edit().remove(key).apply();
    }
}
