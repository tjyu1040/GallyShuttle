package com.ephemeraldreams.gallyshuttle;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmReminderLength;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmVibration;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationVibration;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.content.ContentModule;
import com.ephemeraldreams.gallyshuttle.content.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.content.preferences.StringPreference;
import com.ephemeraldreams.gallyshuttle.net.NetworkModule;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationApiService;
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationIntentService;
import com.ephemeraldreams.gallyshuttle.ui.receivers.NetworkStateBroadCastReceiver;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
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
    void inject(NetworkStateBroadCastReceiver networkStateBroadCastReceiver);

    // Android system services dependencies made available to sub-graphs
    Application getApplication();
    Bus getBus();
    ConnectivityManager getConnectivityManager();
    AlarmManager getAlarmManager();
    NotificationManager getNotificationManager();

    // Content dependencies made available to sub-graphs
    Resources getResources();
    SharedPreferences getSharedPreferences();
    @AlarmReminderLength StringPreference getAlarmReminderLengthStringPreference();
    @AlarmRingtoneChoice StringPreference getAlarmRingtoneChoiceStringPreference();
    @AlarmVibration BooleanPreference getAlarmVibrationBooleanPreference();
    @NotificationRingtoneChoice StringPreference getNotificationRingtoneChoiceStringPreference();
    @NotificationVibration BooleanPreference getNotificationVibrationBooleanPreference();
    Gson getGson();
    CacheManager getCacheManager();

    OkHttpClient getOkHttpClient();
    GallyShuttleApiService getGallyShuttleApiService();
    RegistrationApiService getRegistrationApiService();
}
