package com.ephemeraldreams.gallyshuttle;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

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

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    //TODO: implement crash reporting...
                }
            });
        }
    }

    public static ApplicationComponent getComponent(){
        return component;
    }
}
