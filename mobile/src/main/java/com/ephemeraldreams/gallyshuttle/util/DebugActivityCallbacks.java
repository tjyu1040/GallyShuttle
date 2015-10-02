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

package com.ephemeraldreams.gallyshuttle.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Small debugging utility class to output the current states of activities.
 */
public final class DebugActivityCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.d("onCreate(): " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.d("onStart(): " + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.d("onResume(): " + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.d("onPause(): " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.d("onStop(): " + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.d("onSavedInstanceState(): " + activity.getLocalClassName());
        for (String key : outState.keySet()) {
            Timber.d("----> " + key + " saved.");
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.d("onDestroy(): " + activity.getLocalClassName());
    }
}
