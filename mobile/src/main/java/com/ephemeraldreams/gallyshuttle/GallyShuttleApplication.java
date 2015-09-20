package com.ephemeraldreams.gallyshuttle;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.ephemeraldreams.gallyshuttle.util.CrashReportingTree;
import com.ephemeraldreams.gallyshuttle.util.DebugActivityCallbacks;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

/**
 * Application to initialize libraries.
 */
public class GallyShuttleApplication extends Application {

    private static ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        JodaTimeAndroid.init(this);

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            registerActivityLifecycleCallbacks(new DebugActivityCallbacks());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
    }

    public static ApplicationComponent getComponent(){
        return component;
    }
}
