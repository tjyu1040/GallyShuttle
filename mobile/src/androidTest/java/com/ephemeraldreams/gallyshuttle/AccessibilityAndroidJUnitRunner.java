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

package com.ephemeraldreams.gallyshuttle;

import android.support.test.espresso.contrib.AccessibilityChecks;
import android.support.test.runner.AndroidJUnitRunner;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesViews;

/**
 * An {@link AndroidJUnitRunner} that also runs accessibility checks.
 */
public class AccessibilityAndroidJUnitRunner extends AndroidJUnitRunner {
    static {
        AccessibilityChecks.enable()
                //TODO: figure out how to make switch more accessible.
                .setSuppressingResultMatcher(matchesViews(withId(R.id.arrival_notification_switch)));
    }
}
