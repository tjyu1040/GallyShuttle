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

package com.ephemeraldreams.gallyshuttle.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.TimesFragment;

/**
 * Adapter class to handle and display schedule in a {@link android.support.v4.view.ViewPager}.
 */
public class TimesFragmentPagerAdapter extends FragmentPagerAdapter {

    private final Schedule schedule;

    public TimesFragmentPagerAdapter(FragmentManager fragmentManager, Schedule schedule) {
        super(fragmentManager);
        this.schedule = schedule;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return schedule.getStation(position).name;
    }

    @Override
    public Fragment getItem(int position) {
        return TimesFragment.newInstance(schedule.getTimes(position));
    }

    @Override
    public int getCount() {
        return schedule.getStationsCount();
    }
}