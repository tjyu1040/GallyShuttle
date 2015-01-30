/*
 * Copyright (C) 2014 Timothy Yu
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

package com.ephemeraldreams.gallyshuttle.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ShuttleApplication;
import com.ephemeraldreams.gallyshuttle.ui.SettingsFragment;
import com.nispok.snackbar.SnackbarManager;

import javax.inject.Inject;

/**
 * BroadcastReceiver class to send a notification at a set alarm time.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_REMINDER = "hour";

    @Inject NotificationManager mNotificationManager;
    @Inject SharedPreferences mSharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShuttleApplication application = (ShuttleApplication) context.getApplicationContext();
        application.getApplicationGraph().inject(this);
        remindNotification(context, intent);
    }

    private void remindNotification(Context context, Intent intent) {
        int reminderLength = intent.getIntExtra(EXTRA_REMINDER, 5);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bus_white_48dp)
                .setContentTitle("Gally Shuttle Reminder!")
                .setContentText("Your shuttle will be arriving in " + reminderLength + " minutes!")
                .setAutoCancel(true);

        boolean vibrationEnabled = mSharedPreferences.getBoolean(SettingsFragment.KEY_PREF_VIBRATE, true);
        if (vibrationEnabled) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        String ringtone = mSharedPreferences.getString(SettingsFragment.KEY_PREF_RINGTONE, "content://settings/system/alarm_alert");
        if (!TextUtils.isEmpty(ringtone)) {
            Uri ringtoneUri = Uri.parse(ringtone);
            builder.setSound(ringtoneUri);
        }
        mNotificationManager.notify(0, builder.build());
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsFragment.REMINDER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, false);
        editor.apply();
        SnackbarManager.dismiss();
    }
}