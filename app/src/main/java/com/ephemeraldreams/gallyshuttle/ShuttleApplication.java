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

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.ephemeraldreams.gallyshuttle.util.CrashReportingTree;
import com.ephemeraldreams.gallyshuttle.util.DebugActivityCallbacks;
import com.google.android.gms.analytics.GoogleAnalytics;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Application to initialize Dagger, JodaTimeAndroid, Google Analytics, and callbacks for debugging
 * and Crashlytics reporting.
 */
public class ShuttleApplication extends Application {

    private static AppComponent appComponent;

    @Inject GoogleAnalytics googleAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);

        JodaTimeAndroid.init(this);

        googleAnalytics.setAppOptOut(BuildConfig.DEBUG);
        googleAnalytics.setDryRun(BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            registerActivityLifecycleCallbacks(new DebugActivityCallbacks());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
