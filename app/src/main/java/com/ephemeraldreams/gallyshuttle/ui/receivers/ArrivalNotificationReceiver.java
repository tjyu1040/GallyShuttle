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

package com.ephemeraldreams.gallyshuttle.ui.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ShuttleApplication;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationVibration;
import com.ephemeraldreams.gallyshuttle.data.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;

import org.joda.time.LocalDateTime;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Broadcast receiver to send an arrival notification at the arrival time.
 */
public class ArrivalNotificationReceiver extends BroadcastReceiver {

    public static final String EXTRA_STATION_NAME = "extra_station_name";

    @Inject NotificationManager notificationManager;
    @Inject @NotificationRingtoneChoice StringPreference ringtoneChoiceStringPreference;
    @Inject @NotificationVibration BooleanPreference vibrationEnabledBooleanPreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShuttleApplication.getAppComponent().inject(this);
        displayReminderNotification(context, intent);
    }

    private void displayReminderNotification(Context context, Intent intent) {

        String stationName = intent.getStringExtra(EXTRA_STATION_NAME);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bus_white_48dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(String.format(context.getString(R.string.notification_content), stationName))
                .setAutoCancel(true);
        if (vibrationEnabledBooleanPreference.get()) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        String ringtone = ringtoneChoiceStringPreference.get();
        if (!TextUtils.isEmpty(ringtone)) {
            Uri ringtoneUri = Uri.parse(ringtone);
            builder.setSound(ringtoneUri);
        }
        notificationManager.notify(0, builder.build());
        Timber.d("Notification sent: " + LocalDateTime.now().toLocalTime().toString());
    }
}
