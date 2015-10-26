/*
 * Copyright 2014 Timothy Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;

import org.joda.time.LocalDateTime;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Broadcast receiver to send an arrival notification at the arrival time.
 */
public class ArrivalNotificationReceiver extends BroadcastReceiver {

    public static final String STATION_NAME_EXTRA = "station.name.extra";

    @Inject NotificationManagerCompat notificationManagerCompat;
    @Inject SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        GallyShuttleApplication.getComponent().inject(this);
        displayReminderNotification(context, intent);
    }

    private void displayReminderNotification(Context context, Intent intent) {
        CharSequence stationName = intent.getCharSequenceExtra(STATION_NAME_EXTRA);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bus_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_content, stationName.toString()))
                .setAutoCancel(true);

        boolean vibrateEnabled = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_vibrate), true);
        if (vibrateEnabled) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        String ringtone = sharedPreferences.getString(context.getString(R.string.pref_key_notification_ringtone), "");
        if (!TextUtils.isEmpty(ringtone)) {
            Uri ringtoneUri = Uri.parse(ringtone);
            builder.setSound(ringtoneUri);
        }

        notificationManagerCompat.notify(0, builder.build());
        Timber.d("Notification sent at %s", LocalDateTime.now().toLocalTime().toString());
    }
}
