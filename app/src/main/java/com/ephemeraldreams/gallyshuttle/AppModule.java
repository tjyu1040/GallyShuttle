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
import android.content.Context;
import android.net.ConnectivityManager;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.data.DataModule;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require an {@link Application} to create.
 */
@Module(includes = DataModule.class)
public class AppModule {

    private ShuttleApplication application;

    public AppModule(ShuttleApplication application) {
        this.application = application;
    }

    /**
     * Provide application singleton.
     *
     * @return Application singleton instance.
     */
    @Provides
    @ApplicationScope
    Application provideApplication() {
        return application;
    }

    /**
     * Provide a connectivity manager singleton.
     *
     * @return Connectivity manager instance.
     */
    @Provides
    @ApplicationScope
    ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Provide an alarm manager singleton.
     *
     * @return Alarm manager instance.
     */
    @Provides
    @ApplicationScope
    AlarmManager provideAlarmManager() {
        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Provide a notification manager singleton.
     *
     * @return Notification manager instance.
     */
    @Provides
    @ApplicationScope
    NotificationManager provideNotificationManager() {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
