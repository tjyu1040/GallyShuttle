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

package com.ephemeraldreams.gallyshuttle.modules;

import android.app.FragmentManager;
import android.content.Context;

import com.ephemeraldreams.gallyshuttle.ui.AboutDialogFragment;
import com.ephemeraldreams.gallyshuttle.ui.MainActivity;
import com.ephemeraldreams.gallyshuttle.ui.MainFragment;
import com.ephemeraldreams.gallyshuttle.ui.PoliciesFragment;
import com.ephemeraldreams.gallyshuttle.ui.ScheduleFragment;
import com.ephemeraldreams.gallyshuttle.ui.SettingsFragment;
import com.ephemeraldreams.gallyshuttle.ui.TimesFragment;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module for dependencies that exists only for the scope of a single activity. Also applies to
 * fragments attached to the activity.
 */
@Module(
        injects = {
                MainActivity.class,
                MainFragment.class,
                ScheduleFragment.class,
                TimesFragment.class,
                AboutDialogFragment.class,
                PoliciesFragment.class,
                SettingsFragment.class
        },
        addsTo = AndroidModule.class,
        library = true
)
public class ActivityModule {

    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }

    @Provides
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    FragmentManager provideFragmentManager() {
        return activity.getFragmentManager();
    }
}