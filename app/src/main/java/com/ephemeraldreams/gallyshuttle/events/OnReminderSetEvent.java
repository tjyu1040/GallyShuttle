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
import android.content.Context;
import android.content.SharedPreferences;

import com.ephemeraldreams.gallyshuttle.ui.SettingsFragment;

public class OnReminderSetEvent {

    public String reminderMessage;
    public PendingIntent alarmIntent;

    public OnReminderSetEvent(Activity activity, String timeMessage, PendingIntent alarmIntent){
        this.reminderMessage = "Reminder for " + timeMessage + "!";
        this.alarmIntent = alarmIntent;

        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, true);
        editor.putString(SettingsFragment.KEY_PREF_REMINDER_TIME_MESSAGE, reminderMessage);
        editor.apply();
    }
}
