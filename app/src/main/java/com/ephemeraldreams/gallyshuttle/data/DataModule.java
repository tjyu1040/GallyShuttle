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

package com.ephemeraldreams.gallyshuttle.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.ReminderLength;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.RingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.VibrationEnabled;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.api.ApiModule;
import com.ephemeraldreams.gallyshuttle.data.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import dagger.Module;
import dagger.Provides;

/**
 * A module for data-specific dependencies.
 */
@Module(includes = ApiModule.class)
public class DataModule {

    public static final int DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String DEFAULT_RINGTONE = "content://settings/system/alarm_alert";
    public static final boolean DEFAULT_VIBRATION_ENABLED = true;

    /**
     * Provide application's resources.
     *
     * @param application Application to get resources from.
     * @return Android resources.
     */
    @Provides
    @ApplicationScope
    Resources provideResources(Application application) {
        return application.getResources();
    }

    /**
     * Provide application's shared preferences.
     *
     * @param application Application to get shared preferences from.
     * @return Android shared preferences.
     */
    @Provides
    @ApplicationScope
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * Provide reminder length preference.
     *
     * @param sharedPreferences Shared preferences to pull preference from.
     * @param resources         Resources to pull preference key id from.
     * @return Reminder length preference.
     */
    @Provides
    @ApplicationScope
    @ReminderLength
    StringPreference provideReminderLengthPreference(SharedPreferences sharedPreferences, Resources resources) {
        return new StringPreference(sharedPreferences, resources.getString(R.string.pref_key_reminder_length), "5");
    }

    /**
     * Provide ringtone choice preference.
     *
     * @param sharedPreferences Shared preferences to pull preference from.
     * @param resources         Resources to pull preference key id from.
     * @return Ringtone choice preference.
     */
    @Provides
    @ApplicationScope
    @RingtoneChoice
    StringPreference provideRingtoneChoicePreference(SharedPreferences sharedPreferences, Resources resources) {
        return new StringPreference(sharedPreferences, resources.getString(R.string.pref_key_ringtone), DEFAULT_RINGTONE);
    }

    /**
     * Provide vibration enabled preference.
     *
     * @param sharedPreferences Shared preferences to pull preference from.
     * @param resources         Resources to pull preference key id from.
     * @return Vibration enabled preference.
     */
    @Provides
    @ApplicationScope
    @VibrationEnabled
    BooleanPreference provideVibrateEnabledPreference(SharedPreferences sharedPreferences, Resources resources) {
        return new BooleanPreference(sharedPreferences, resources.getString(R.string.pref_key_vibrate), DEFAULT_VIBRATION_ENABLED);
    }

    /**
     * Provide a HTTP client.
     *
     * @param application Application to get cache directory from.
     * @return Cached HTTP client.
     */
    @Provides
    @ApplicationScope
    OkHttpClient provideOkHttpClient(Application application) {
        OkHttpClient client = new OkHttpClient();
        File cacheDir = new File(application.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);
        return client;
    }

    /**
     * Provide Google Analytics.
     *
     * @param application Application to initialize.
     * @return Google Analytics instance.
     */
    @Provides
    @ApplicationScope
    GoogleAnalytics provideGoogleAnalytics(Application application) {
        return GoogleAnalytics.getInstance(application);
    }

    /**
     * Provide an application tracker.
     *
     * @param googleAnalytics Google Analytics instance to retrieve tracker from.
     * @return Application tracker.
     */
    @Provides
    @ApplicationScope
    Tracker provideAppTracker(GoogleAnalytics googleAnalytics) {
        return googleAnalytics.newTracker(R.xml.app_tracker);
    }
}