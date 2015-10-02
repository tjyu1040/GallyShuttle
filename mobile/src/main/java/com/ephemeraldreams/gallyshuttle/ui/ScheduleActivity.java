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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesFragmentPagerAdapter;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Activity to display schedule.
 */
public class ScheduleActivity extends BaseScheduleActivity {

    public static final String PATH_EXTRA = "schedule.path.extra";

    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.schedule_view_pager) ViewPager viewPager;
    @Inject FragmentManager fragmentManager;
    private TimesFragmentPagerAdapter adapter;

    private int pathId;

    /**
     * Launch a {@link ScheduleActivity} instance.
     *
     * @param activity       The activity opening the {@link ScheduleActivity}.
     * @param schedulePathId Path id of schedule to display.
     */
    public static void launch(Activity activity, @StringRes int schedulePathId) {
        Intent intent = new Intent(activity, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.PATH_EXTRA, schedulePathId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getComponent().inject(this);
        overridePendingTransition(0, 0);

        pathId = getIntent().getIntExtra(PATH_EXTRA, R.string.path_continuous);
    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        switch (pathId) {
            case R.string.path_continuous:
                return R.id.nav_continuous;
            case R.string.path_late_night:
                return R.id.nav_late_night;
            case R.string.path_weekend:
                return R.id.nav_weekend;
            case R.string.path_alt_continuous:
                return R.id.nav_alt_continuous;
            case R.string.path_modified:
                return R.id.nav_modified;
        }
        return INVALID_NAVIGATION_DRAWER_ITEM_ID;
    }

    @Override
    protected String getActionBarTitle() {
        String scheduleName;
        switch (pathId) {
            case R.string.path_continuous:
                scheduleName = getString(R.string.nav_continuous);
                break;
            case R.string.path_late_night:
                scheduleName = getString(R.string.nav_late_night);
                break;
            case R.string.path_weekend:
                scheduleName = getString(R.string.nav_weekend);
                break;
            case R.string.path_alt_continuous:
                scheduleName = getString(R.string.nav_alt_continuous);
                break;
            case R.string.path_modified:
                scheduleName = getString(R.string.nav_modified);
                break;
            default:
                scheduleName = "";
                break;
        }
        return getString(R.string.schedule_name_fmt, scheduleName).trim();
    }

    @Override
    protected void updateUiOnResponse(Schedule schedule) {
        setToolbarTitle(getActionBarTitle());
        adapter = new TimesFragmentPagerAdapter(fragmentManager, schedule);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUiOnRefreshStart() {
        //TODO: add refresh ui mechanism
    }

    @Override
    protected int getSchedulePathId() {
        return pathId;
    }
}
