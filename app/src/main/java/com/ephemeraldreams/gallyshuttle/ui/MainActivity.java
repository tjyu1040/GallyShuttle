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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.ui.adapters.NavigationDrawerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Activity to handle navigation.
 */
public class MainActivity extends BaseActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    public static final String KEY_FRAGMENT_INSTANCE = "fragment instance";

    private static final int HOME_NAVIGATION_INDEX = 0;
    private static final int SCHEDULE_NAVIGATION_INDEX = 1;
    private static final int POLICIES_NAVIGATION_INDEX = 2;
    private static final int ABOUT_NAVIGATION_INDEX = 3;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer_expandable_list_view) ExpandableListView leftExpandableListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final Handler drawerHandler = new Handler();
    @Inject LayoutInflater layoutInflater;

    @Inject ShuttleApiService shuttleApiService;
    @Inject ConnectivityManager connectivityManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject CacheManager cacheManager;

    @Inject FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inject(this);

        cacheManager.checkAndClearCacheVersion();

        setUpNavigationDrawer();

        if (savedInstanceState != null) {
            currentFragment = fragmentManager.getFragment(savedInstanceState, KEY_FRAGMENT_INSTANCE);
            if (currentFragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, currentFragment)
                        .commit();
            }
        } else {
            setSelectedNavigationGroup(HOME_NAVIGATION_INDEX);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment != null) {
            fragmentManager.putFragment(outState, KEY_FRAGMENT_INSTANCE, currentFragment);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.launchActivity(this);
                return true;
            default:
                return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Set up navigation drawer.
     */
    private void setUpNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        String[] navigationHeaders = getResources().getStringArray(R.array.navigation_headers);
        List<String> scheduleTitles = Arrays.asList(getResources().getStringArray(R.array.schedule_titles));

        HashMap<String, List<String>> navigationItems = new HashMap<>();
        navigationItems.put(navigationHeaders[HOME_NAVIGATION_INDEX], null);
        navigationItems.put(navigationHeaders[SCHEDULE_NAVIGATION_INDEX], scheduleTitles);
        navigationItems.put(navigationHeaders[POLICIES_NAVIGATION_INDEX], null);
        navigationItems.put(navigationHeaders[ABOUT_NAVIGATION_INDEX], null);

        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(layoutInflater, navigationHeaders, navigationItems);
        leftExpandableListView.setAdapter(navigationDrawerAdapter);
        leftExpandableListView.expandGroup(SCHEDULE_NAVIGATION_INDEX);
        leftExpandableListView.setOnGroupClickListener(this);
        leftExpandableListView.setOnChildClickListener(this);
    }

    /**
     * Close drawers smoothly with no stutters.
     *
     * @param runnable Runnable to run after drawer closed.
     */
    private void closeDrawers(Runnable runnable) {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            drawerHandler.removeCallbacksAndMessages(null);
            drawerHandler.postDelayed(runnable, 250);
            drawerLayout.closeDrawers();
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
        closeDrawers(new Runnable() {
            @Override
            public void run() {
                setSelectedNavigationGroup(groupPosition);
            }
        });
        return true;
    }

    /**
     * Handle navigation group clicks. Header 1, "Schedules", has no actions and cannot be clicked.
     *
     * @param groupPosition Group to be selected.
     */
    private void setSelectedNavigationGroup(int groupPosition) {
        switch (groupPosition) {
            case HOME_NAVIGATION_INDEX:
                currentFragment = MainFragment.newInstance();
                break;
            case POLICIES_NAVIGATION_INDEX:
                currentFragment = PoliciesFragment.newInstance();
                break;
            case ABOUT_NAVIGATION_INDEX:
                currentFragment = AboutFragment.newInstance();
                break;
            default:
                currentFragment = null;
        }
        if (groupPosition != SCHEDULE_NAVIGATION_INDEX && currentFragment != null) {
            int position = leftExpandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
            leftExpandableListView.setItemChecked(position, true);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .commit();
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
        closeDrawers(new Runnable() {
            @Override
            public void run() {
                setSelectedNavigationChild(groupPosition, childPosition);
            }
        });
        return true;
    }

    /**
     * Handle navigation child clicks. Group 0's children are the only clickable children.
     *
     * @param groupPosition Group to find child to be selected.
     * @param childPosition Child to select.
     */
    private void setSelectedNavigationChild(int groupPosition, int childPosition) {
        if (groupPosition == SCHEDULE_NAVIGATION_INDEX) {
            int position = leftExpandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
            leftExpandableListView.setItemChecked(position, false);
            ScheduleActivity.launchActivity(this, childPosition);
        }
    }
}
