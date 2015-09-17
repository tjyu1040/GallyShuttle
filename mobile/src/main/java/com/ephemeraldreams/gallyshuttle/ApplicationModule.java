package com.ephemeraldreams.gallyshuttle;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific system services dependencies which require an {@link Application}
 * to create.
 */
@Module()
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    Application application() {
        return application;
    }

    @Provides
    @ApplicationScope
    Bus bus() {
        return new Bus();
    }

    @Provides
    @ApplicationScope
    ConnectivityManager connectivityManager(Application application) {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @ApplicationScope
    AlarmManager alarmManager(Application application) {
        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @ApplicationScope
    NotificationManager notificationManager(Application application) {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
