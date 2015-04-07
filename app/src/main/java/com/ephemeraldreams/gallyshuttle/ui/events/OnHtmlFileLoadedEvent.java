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

/**
 * Event for Otto subscription and publication. Event happens once HTML file has been loaded into a
 * String. This event passes along the resulting html to {@link com.ephemeraldreams.gallyshuttle.ui.AboutDialogFragment},
 * or {@link com.ephemeraldreams.gallyshuttle.ui.PoliciesFragment}.
 */
public class OnHtmlFileLoadedEvent {

    public String html;

    public OnHtmlFileLoadedEvent(String html) {
        this.html = html;
    }
}
