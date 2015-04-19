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

import org.joda.time.LocalDateTime;

/**
 * Event for Otto subscription and publication. Event happens when user click on a time to set an alarm
 * reminder and handles preparation of alarm. This event passes along the alarm hour, alarm minute, and
 * dialog message to {@link com.ephemeraldreams.gallyshuttle.ui.ScheduleActivity}.
 */
public class PrepareAlarmReminderEvent {

    private String time;
    private int prefReminderLength;

    public int hour;
    public int minute;
    public String reminderDialogMessage;

    public PrepareAlarmReminderEvent(String time, int prefReminderLength) {
        this.time = time;
        this.prefReminderLength = prefReminderLength;
        setAlarmCalendar();
        setReminderDialogMessage();
    }

    private void setAlarmCalendar() {
        LocalDateTime alarmDateTime = DateUtils.parseToLocalDateTime(time);
        alarmDateTime = alarmDateTime.minusMinutes(prefReminderLength);
        hour = alarmDateTime.getHourOfDay();
        minute = alarmDateTime.getMinuteOfHour();
    }

    private void setReminderDialogMessage() {
        reminderDialogMessage = "Remind me " + prefReminderLength + " minutes before " + time + " shuttle arrival?";
    }
}
