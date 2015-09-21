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

package com.ephemeraldreams.gallyshuttle.content;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.ephemeraldreams.gallyshuttle.ApplicationModule;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmReminderLength;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmVibration;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationVibration;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.content.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.content.preferences.StringPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * A module for content-specific dependencies.
 */
@Module(includes = ApplicationModule.class)
public class ContentModule {

    public static final String DEFAULT_ALARM_RINGTONE = Settings.System.DEFAULT_ALARM_ALERT_URI.toString();
    public static final String DEFAULT_NOTIFICATION_RINGTONE = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
    public static final boolean DEFAULT_VIBRATION_ENABLED = true;

    @Provides
    @ApplicationScope
    Resources resources(Application application) {
        return application.getResources();
    }

    @Provides
    @ApplicationScope
    SharedPreferences sharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @ApplicationScope
    @AlarmReminderLength
    StringPreference alarmReminderLengthPreference(SharedPreferences sharedPreferences, Resources resources) {
        return new StringPreference(sharedPreferences,
                resources.getString(R.string.key_alarm_reminder_length_preference),
                "5"
        );
    }

    @Provides
    @ApplicationScope
    @AlarmRingtoneChoice
    StringPreference alarmRingtoneChoicePreference(SharedPreferences sharedPreferences, Resources resources) {
        return new StringPreference(sharedPreferences,
                resources.getString(R.string.key_alarm_ringtone_preference),
                DEFAULT_ALARM_RINGTONE
        );
    }

    @Provides
    @ApplicationScope
    @AlarmVibration
    BooleanPreference alarmVibrationPreference(SharedPreferences sharedPreferences, Resources resources) {
        return new BooleanPreference(sharedPreferences,
                resources.getString(R.string.key_alarm_vibrate_preference),
                DEFAULT_VIBRATION_ENABLED
        );
    }

    @Provides
    @ApplicationScope
    @NotificationRingtoneChoice
    StringPreference notificationRingtoneChoicePreference(SharedPreferences sharedPreferences, Resources resources) {
        return new StringPreference(sharedPreferences,
                resources.getString(R.string.key_notification_ringtone_preference),
                DEFAULT_NOTIFICATION_RINGTONE
        );
    }

    @Provides
    @ApplicationScope
    @NotificationVibration
    BooleanPreference notificationVibrationPreference(SharedPreferences sharedPreferences, Resources resources) {
        return new BooleanPreference(sharedPreferences,
                resources.getString(R.string.key_notification_vibrate_preference),
                DEFAULT_VIBRATION_ENABLED
        );
    }

    @Provides
    @ApplicationScope
    Gson gson() {
        return new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
    }

    @Provides
    @ApplicationScope
    CacheManager cacheManager(Application application, Gson gson) {
        try {
            return new CacheManager(application, gson);
        } catch (IOException e) {
            Timber.e(e, "Null cache manager.");
            return null;
        }
    }
}
