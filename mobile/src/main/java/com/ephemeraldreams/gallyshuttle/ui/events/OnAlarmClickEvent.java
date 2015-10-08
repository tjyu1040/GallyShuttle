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

package com.ephemeraldreams.gallyshuttle.ui.events;

import com.ephemeraldreams.gallyshuttle.content.DateUtils;

import org.joda.time.LocalDateTime;

/**
 * Event for Otto subscription and publication. This event happens when user click the set alarm
 * button for a time and passes along a formatted and parsed {@link LocalDateTime} alarm instance.
 */
public class OnAlarmClickEvent {

    public LocalDateTime alarmTime;

    public OnAlarmClickEvent(String time) {
        alarmTime = DateUtils.parseToLocalDateTime(time);
    }
}
