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

package com.ephemeraldreams.gallyshuttle.events;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ephemeraldreams.gallyshuttle.receivers.AlarmReceiver;
import com.ephemeraldreams.gallyshuttle.ui.SettingsFragment;
import com.ephemeraldreams.gallyshuttle.util.ScheduleTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OnReminderSetEvent {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm aa");

    public Calendar alarmCalendar;
    public String reminderDialogMessage;
    public String undoSnackbarMessage;
    public String persistentSnackbarMessage;
    public PendingIntent alarmPendingIntent;

    private Activity activity;
    private String time;

    public OnReminderSetEvent(Activity activity, String time) {
        this.activity = activity;
        this.time = time;
        prepareAlarmAndReminderMessages();
    }

    /**
     * Set up calendar alarm and messages.
     */
    private void prepareAlarmAndReminderMessages() {
        alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTime(ScheduleTimeFormatter.parseToDate(time));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int prefReminderLength = Integer.parseInt(sharedPreferences.getString(SettingsFragment.KEY_PREF_REMINDER_LENGTH, "5"));
        alarmCalendar.add(Calendar.MINUTE, -prefReminderLength);

        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = new Date();
        currentCalendar.setTime(currentDate);

        String setDay;
        if (alarmCalendar.before(currentCalendar)) {
            alarmCalendar.add(Calendar.DATE, 1);
            setDay = " tomorrow";
        } else {
            setDay = " today";
        }

        String timeMessage = TIME_FORMAT.format(alarmCalendar.getTime()) + setDay;
        reminderDialogMessage = "Set reminder for " + timeMessage + "?";
        undoSnackbarMessage = "Reminder set for " + timeMessage + "!";
        persistentSnackbarMessage = "Reminder for " + timeMessage + "!";

        setAlarmPendingIntent(prefReminderLength);
    }

    /**
     * Set up the alarm pending intent.
     *
     * @param prefReminderLength Reminder length.
     */
    private void setAlarmPendingIntent(int prefReminderLength) {
        Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
        alarmIntent.putExtra(AlarmReceiver.EXTRA_REMINDER, prefReminderLength);
        alarmPendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
    }
}