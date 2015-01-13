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

/**
 * Event for Otto subscription and publication. Event happens when a schedule has been chosen. This
 * event will send the specificed schedule url from {@link com.ephemeraldreams.gallyshuttle.ui.MainFragment}
 * to {@link com.ephemeraldreams.gallyshuttle.ui.MainActivity} to begin retrieving schedule data.
 */
public class OnScheduleStartEvent {

    public String url;
    public String scheduleName;

    public OnScheduleStartEvent(String url, String scheduleName) {
        this.url = url;
        this.scheduleName = scheduleName;
    }
}