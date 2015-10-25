package com.ephemeraldreams.gallyshuttle;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.common.api.GoogleApiClient;
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
    AudioManager AudioManager(Application application) {
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @ApplicationScope
    Vibrator vibrator(Application application) {
        return (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    @ApplicationScope
    NotificationManagerCompat notificationManager(Application application) {
        return NotificationManagerCompat.from(application);
    }

    @Provides
    @ApplicationScope
    GoogleApiClient googleApiClient(Application application) {
        return new GoogleApiClient.Builder(application)
                .addApi(AppInvite.API)
                .build();
    }
}
