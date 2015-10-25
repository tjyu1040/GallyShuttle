package com.ephemeraldreams.gallyshuttle;

import android.app.AlarmManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.content.ContentModule;
import com.ephemeraldreams.gallyshuttle.net.NetworkModule;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationApiService;
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationIntentService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import dagger.Component;

/**
 * A component whose lifetime is constrained to {@link Application}'s lifecycle.
 */
@ApplicationScope
@Component(modules = {ApplicationModule.class, NetworkModule.class, ContentModule.class})
public interface ApplicationComponent {

    void inject(GallyShuttleApplication gallyShuttleApplication);
    void inject(RegistrationIntentService registrationIntentService);

    // Android system services dependencies made available to sub-components
    Application getApplication();
    Bus getBus();
    AlarmManager getAlarmManager();
    NotificationManagerCompat getNotificationManagerCompat();
    GoogleApiClient getGoogleApiClient();

    // Content dependencies made available to sub-components
    Resources getResources();
    SharedPreferences getSharedPreferences();
    Gson getGson();
    CacheManager getCacheManager();

    // Network dependencies made available to sub-components
    GallyShuttleApiService getGallyShuttleApiService();
    RegistrationApiService getRegistrationApiService();
}
