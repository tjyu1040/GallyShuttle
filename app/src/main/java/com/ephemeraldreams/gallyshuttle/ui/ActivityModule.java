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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.app.FragmentManager;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

/**
 * A module for activity-specific or fragment-specific dependencies.
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Provide activity instance.
     *
     * @return Activity instance.
     */
    @Provides
    @ActivityScope
    Activity provideActivity() {
        return activity;
    }

    /**
     * Provide a bus singleton.
     *
     * @return Bus instance.
     */
    @Provides
    @ActivityScope
    Bus provideBus() {
        return new Bus();
    }

    /**
     * Provide a fragment manager instance.
     *
     * @return Fragment manager instance.
     */
    @Provides
    @ActivityScope
    FragmentManager provideFragmentManager() {
        return activity.getFragmentManager();
    }
}
