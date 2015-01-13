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

package com.ephemeraldreams.gallyshuttle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ephemeraldreams.gallyshuttle.Constants;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesFragmentPagerAdapter;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment to display stops and times of a specific schedule.
 */
public class ScheduleFragment extends BaseFragment {

    private static final String ARG_SCHEDULE = "schedule";

    @InjectView(R.id.schedulePagerTabsStrip) PagerTabStrip mSchedulePagerTabsStrip;
    @InjectView(R.id.scheduleViewpager) ViewPager mScheduleViewpager;

    private Schedule mSchedule;

    /**
     * Required empty public constructor
     */
    public ScheduleFragment() {

    }

    public static ScheduleFragment newInstance(Schedule schedule) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SCHEDULE, schedule);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSchedule = getArguments().getParcelable(ARG_SCHEDULE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setActivityTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.inject(this, rootView);
        mSchedulePagerTabsStrip.setTabIndicatorColorResource(R.color.blue);
        TimesFragmentPagerAdapter timesFragmentPagerAdapter = new TimesFragmentPagerAdapter(getChildFragmentManager(), mSchedule);
        mScheduleViewpager.setAdapter(timesFragmentPagerAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Set activity title depending on which schedule is displayed.
     */
    public void setActivityTitle() {
        int activityTitle;
        switch (mSchedule.name) {
            case Constants.CONTINUOUS_NAME:
                activityTitle = R.string.schedule_fragment_continuous_title;
                break;
            case Constants.LATE_NIGHT_NAME:
                activityTitle = R.string.schedule_fragment_late_night_title;
                break;
            case Constants.WEEKEND_NAME:
                activityTitle = R.string.schedule_fragment_weekend_title;
                break;
            case Constants.MODIFIED_NAME:
                activityTitle = R.string.schedule_fragment_modified_title;
                break;
            default:
                activityTitle = R.string.main_fragment_title;
                break;
        }
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(activityTitle);
    }
}