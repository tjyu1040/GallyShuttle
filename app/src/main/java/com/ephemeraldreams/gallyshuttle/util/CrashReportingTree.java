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

package com.ephemeraldreams.gallyshuttle.util;

//import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * A tree which logs important information for crash reporting using Crashlytics.
 */
public class CrashReportingTree extends Timber.HollowTree {

    /*
    @Override
    public void i(String message, Object... args) {
        logMessage(message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        logMessage(message, args);
    }

    @Override
    public void w(String message, Object... args) {
        logMessage("WARNING: " + message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        logMessage("WARNING: " + message, args);
    }

    @Override
    public void e(String message, Object... args) {
        logMessage("ERROR: " + message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        logMessage("ERROR: " + message, args);
        Crashlytics.logException(t);
    }

    private void logMessage(String message, Object... args) {
        Crashlytics.log(String.format(message, args));
    }
    */
}