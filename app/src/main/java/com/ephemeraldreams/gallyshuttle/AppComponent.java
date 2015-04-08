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

package com.ephemeraldreams.gallyshuttle;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.ReminderLength;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.RingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.VibrationEnabled;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.data.DataModule;
import com.ephemeraldreams.gallyshuttle.data.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;
import com.squareup.okhttp.OkHttpClient;

import dagger.Component;
import retrofit.RestAdapter;

/**
 * A component whose lifetime is the life of the application.
 */
@ApplicationScope
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {
    void inject(ShuttleApplication shuttleApplication);

    // Exported for children components
    Application provideApplication();

    ConnectivityManager provideConnectivityManager();

    AlarmManager provideAlarmManager();

    NotificationManager provideNotificationManager();

    Resources provideResources();

    SharedPreferences provideSharedPreferences();

    OkHttpClient provideOkHttpClient();

    CacheManager provideCacheManager();

    RestAdapter provideRestAdapter();

    ShuttleApiService provideShuttleApiService();

    @ReminderLength
    StringPreference provideReminderLengthPreference();

    @RingtoneChoice
    StringPreference provideRingtonePreference();

    @VibrationEnabled
    BooleanPreference provideVibrateEnabledPreference();
}
