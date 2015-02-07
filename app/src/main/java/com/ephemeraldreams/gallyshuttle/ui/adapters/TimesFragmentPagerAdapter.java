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

package com.ephemeraldreams.gallyshuttle.ui.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ephemeraldreams.gallyshuttle.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.TimesFragment;

import java.util.Locale;

/**
 * Adapter class to handle and display schedule in a {@link android.support.v4.view.ViewPager}.
 */
public class TimesFragmentPagerAdapter extends FragmentPagerAdapter {

    private Schedule mSchedule;

    public TimesFragmentPagerAdapter(FragmentManager fm, Schedule schedule) {
        super(fm);
        mSchedule = schedule;
    }

    @Override
    public Fragment getItem(int position) {
        return TimesFragment.newInstance(mSchedule.times.get(position));
    }

    @Override
    public int getCount() {
        return mSchedule.stops.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSchedule.stops.get(position).toUpperCase(Locale.getDefault());
    }
}