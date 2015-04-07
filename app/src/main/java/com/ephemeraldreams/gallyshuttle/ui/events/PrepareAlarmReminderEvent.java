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

package com.ephemeraldreams.gallyshuttle.ui.events;

import com.ephemeraldreams.gallyshuttle.util.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Event for Otto subscription and publication. Event happens when user click on a time to set an alarm
 * reminder and handles preparation of alarm. This event passes along the alarm hour, alarm minute, and
 * dialog message to {@link com.ephemeraldreams.gallyshuttle.ui.ScheduleActivity}.
 */
public class PrepareAlarmReminderEvent {

    private String time;
    private Calendar alarmCalendar;

    public int hour;
    public int minute;
    public String reminderDialogMessage;

    public PrepareAlarmReminderEvent(String time, int prefReminderLength) {
        this.time = time;
        setAlarmCalendar(prefReminderLength);
        setReminderDialogMessage();
    }

    private void setAlarmCalendar(int prefReminderLength) {
        alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTime(DateUtils.parseToDate(time));
        alarmCalendar.add(Calendar.MINUTE, -prefReminderLength);
        hour = alarmCalendar.get(Calendar.HOUR);
        minute = alarmCalendar.get(Calendar.MINUTE);
    }

    private void setReminderDialogMessage() {
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
        String timeMessage = DateUtils.TIME_TWELVE_HOURS_FORMATTER.format(alarmCalendar.getTime()) + setDay;
        reminderDialogMessage = "Set reminder for " + timeMessage + "?";
    }
}
