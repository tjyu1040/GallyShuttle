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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Activity to handle navigation.
 */
public class MainActivity extends BaseActivity implements ListView.OnItemClickListener {

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer_list_view) ListView leftDrawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final Handler drawerHandler = new Handler();

    @Inject FragmentManager fragmentManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject ShuttleApiService shuttleApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inject(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        String[] navigationItems = getResources().getStringArray(R.array.navigation_items);
        leftDrawerListView.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.drawer_list_item,
                navigationItems
        ));
        leftDrawerListView.setOnItemClickListener(this);
        setCurrentNavigationItem(0);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else if (fragmentManager.getBackStackEntryCount() != 1) {
            fragmentManager.popBackStackImmediate();
            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
            int position = 0;
            if (fragment instanceof MainFragment) {
                position = 0;
            } else if (fragment instanceof PoliciesFragment) {
                position = 1;
            } else if (fragment instanceof AboutFragment) {
                position = 3;
            }
            leftDrawerListView.setItemChecked(position, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            drawerHandler.removeCallbacksAndMessages(null);
            drawerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentNavigationItem(position);
                }
            }, 250);
            drawerLayout.closeDrawers();
        }
    }

    private void setCurrentNavigationItem(int position) {
        if (position != 2) {
            leftDrawerListView.setItemChecked(position, true);
        }
        switch (position) {
            case 0:
                setFragment(MainFragment.newInstance());
                break;
            case 1:
                setFragment(PoliciesFragment.newInstance());
                break;
            case 2:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 3:
                setFragment(AboutFragment.newInstance());
                break;
        }
    }

    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, fragment)
                .commit();

    }

    private boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
