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

import com.ephemeraldreams.gallyshuttle.AppComponent;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;

import dagger.Component;

/**
 * A component whose lifetime is the life of the activity or fragment.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(ScheduleActivity scheduleActivity);

    void inject(MainFragment mainFragment);

    void inject(TimesFragment timesFragment);

    void inject(PoliciesFragment policiesFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(AboutDialogFragment aboutDialogFragment);

    void inject(AboutFragment aboutFragment);
}
