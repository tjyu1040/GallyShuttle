package com.ephemeraldreams.gallyshuttle;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.ephemeraldreams.gallyshuttle.util.CrashReportingTree;
import com.ephemeraldreams.gallyshuttle.util.DebugActivityCallbacks;
import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                enableStrictMode();
            }
            Timber.plant(new Timber.DebugTree());
            registerActivityLifecycleCallbacks(new DebugActivityCallbacks());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
    }

    /**
     * @return Application {@link dagger.Component} to provide and inject dependencies.
     */
    public static ApplicationComponent getComponent() {
        return component;
    }

    /**
     * Enable strict mode for further debugging and detection of any possible resources leaks.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void enableStrictMode() {
        StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            threadPolicyBuilder.detectResourceMismatches();
        }
        StrictMode.setThreadPolicy(threadPolicyBuilder
                .detectCustomSlowCalls()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
