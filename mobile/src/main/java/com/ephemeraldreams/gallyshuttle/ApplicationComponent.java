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
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationIntentService;
import com.ephemeraldreams.gallyshuttle.ui.receivers.NetworkStateBroadCastReceiver;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import dagger.Component;

/**
 * A component whose lifetime is constrained to {@link Application}'s lifecycle.
 */
@ApplicationScope
@Component(modules = {ApplicationModule.class, ContentModule.class})
public interface ApplicationComponent {

    void inject(GallyShuttleApplication gallyShuttleApplication);
    void inject(RegistrationIntentService registrationIntentService);
    void inject(NetworkStateBroadCastReceiver networkStateBroadCastReceiver);

    // Android system services dependencies made available to sub-graphs
    Application application();
    Bus bus();
    ConnectivityManager connectivityManager();
    AlarmManager alarmManager();
    NotificationManager notificationManager();

    // Content dependencies made available to sub-graphs
    Resources resources();
    SharedPreferences sharedPreferences();
    @AlarmReminderLength StringPreference alarmReminderLengthStringPreference();
    @AlarmRingtoneChoice StringPreference alarmRingtoneChoiceStringPreference();
    @AlarmVibration BooleanPreference alarmVibrationBooleanPreference();
    @NotificationRingtoneChoice StringPreference notificationRingtoneChoiceStringPreference();
    @NotificationVibration BooleanPreference notificationVibrationBooleanPreference();
    Gson gson();
    CacheManager cacheManager();
}
