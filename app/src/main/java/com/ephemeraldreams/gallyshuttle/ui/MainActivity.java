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

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import com.ephemeraldreams.gallyshuttle.events.OnDownloadScheduleEvent;
import com.ephemeraldreams.gallyshuttle.events.OnReminderSetEvent;
import com.ephemeraldreams.gallyshuttle.events.OnScheduleStartEvent;
import com.ephemeraldreams.gallyshuttle.models.GsonHelper;
import com.ephemeraldreams.gallyshuttle.models.Schedule;
import com.ephemeraldreams.gallyshuttle.receivers.AlarmReceiver;
import com.ephemeraldreams.gallyshuttle.tasks.DownloadHtmlTask;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

/**
 * Activity to handle main navigation hierarchy.
 */
public class MainActivity extends BaseActivity implements ListView.OnItemClickListener {

    public static final String KEY_FRAGMENT_INSTANCE = "fragment instance";

    @InjectView(R.id.main_toolbar) Toolbar mToolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer_listview) ListView mLeftDrawerListView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private Fragment mCurrentFragment;

    @Inject Bus mBus;
    @Inject FragmentManager mFragmentManager;
    @Inject ConnectivityManager mConnectivityManager;
    @Inject AlarmManager mAlarmManager;

    private SharedPreferences mSharedPreferences;
    private PendingIntent mCancelAlarmPendingIntent;
    private final Handler mDrawerHandler = new Handler();
    private int mPrevPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up navigation drawer
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name
        );
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        String[] navigationItems = getResources().getStringArray(R.array.navigation_items);
        mLeftDrawerListView.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.drawer_list_item,
                navigationItems
        ));
        mLeftDrawerListView.setOnItemClickListener(this);

        if (savedInstanceState != null) {
            // Replace fragments on configuration change
            mCurrentFragment = mFragmentManager.getFragment(savedInstanceState, KEY_FRAGMENT_INSTANCE);
            if (mCurrentFragment instanceof MainFragment) {
                setCurrentNavigationItem(0);
            } else if (mCurrentFragment instanceof ScheduleFragment) {
                setDrawerItemSelected(0);
                replaceContainer(mCurrentFragment);
            } else if (mCurrentFragment instanceof PoliciesFragment) {
                setCurrentNavigationItem(1);
            } else if (mCurrentFragment instanceof SettingsFragment) {
                setCurrentNavigationItem(2);
            } else if (mCurrentFragment instanceof AboutFragment) {
                setCurrentNavigationItem(3);
            } else {
                setCurrentNavigationItem(0);
            }
        } else {
            setCurrentNavigationItem(0);
        }

        // Prepare an intent to cancel alarm if needed.
        mCancelAlarmPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        // Display a reminder snackbar if there exists any reminder
        mSharedPreferences = getSharedPreferences(SettingsFragment.REMINDER_PREFERENCES, Context.MODE_PRIVATE);
        boolean reminderSet = mSharedPreferences.getBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, false);
        if (reminderSet) {
            String reminderMessage = mSharedPreferences.getString(SettingsFragment.KEY_PREF_REMINDER_TIME_MESSAGE, "");
            displayPersistentReminderSnackbar(reminderMessage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentManager.putFragment(outState, KEY_FRAGMENT_INSTANCE, mCurrentFragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
        } else if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
            Timber.d("B - Previous: " + mPrevPosition);
            mLeftDrawerListView.setItemChecked(mPrevPosition, true);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Store previously selected drawer item on another drawer item clicked.
     *
     * @param selectedPosition Selected position of clicked drawer item.
     */
    private void setDrawerItemSelected(int selectedPosition) {
        // TODO: fix navigation back bug
        mPrevPosition = mLeftDrawerListView.getSelectedItemPosition();
        if (mPrevPosition == -1) {
            mPrevPosition = 0;
        }
        Timber.d("A - Previous: " + mPrevPosition);
        mLeftDrawerListView.setItemChecked(selectedPosition, true);
        Timber.d("A - Selected: " + selectedPosition);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        // Ensure navigation drawer is closed smoothly with no stutters
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerHandler.removeCallbacksAndMessages(null);
            mDrawerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentNavigationItem(position);
                }
            }, 250);
            mDrawerLayout.closeDrawers();
        }
    }

    /**
     * Set current navigation drawer item position and display corresponding fragment.
     *
     * @param position Position of navigation drawer item.
     */
    private void setCurrentNavigationItem(int position) {
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        }
        setDrawerItemSelected(position);
        switch (position) {
            case 0:
                replaceContainer(MainFragment.newInstance());
                break;
            case 1:
                replaceContainer(PoliciesFragment.newInstance());
                break;
            case 2:
                replaceContainer(SettingsFragment.newInstance());
                break;
            case 3:
                replaceContainer(AboutFragment.newInstance());
                break;
        }
    }

    /**
     * Replace main container with a fragment.
     *
     * @param fragment Fragment to display
     */
    private void replaceContainer(Fragment fragment) {
        mCurrentFragment = fragment;
        mFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("fragmentStack")
                .commit();
    }

    /**
     * Check if device is connected to the Internet or not.
     *
     * @return true if connected, false otherwise.
     */
    private boolean isConnected() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Build and show a no connection error dialog.
     */
    private void showNoConnectionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_connection_dialog_title)
                .setMessage(R.string.no_connection_dialog_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog noConnectionDialog = builder.create();
        noConnectionDialog.show();
    }

    /**
     * Build and show a generic error dialog.
     */
    private void showErrorAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_dialog_title)
                .setMessage(R.string.error_dialog_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

    /**
     * Start downloading schedule.
     *
     * @param event Event with stored url.
     */
    @Subscribe
    public void startSchedule(OnScheduleStartEvent event) {
        // Check cached file.
        File tempFile = new File(getCacheDir(), event.scheduleName + ".json");
        if (tempFile.exists() && !tempFile.isDirectory()) {
            Timber.d("File " + event.scheduleName + ".json found.");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
                Schedule schedule = GsonHelper.fromJsonFile(bufferedReader);
                replaceContainer(ScheduleFragment.newInstance(schedule));
            } catch (FileNotFoundException e) {
                Timber.e("File " + event.scheduleName + ".json not found.", e);
            }
        } else {
            // Download data if no cached file.
            if (isConnected()) {
                new DownloadHtmlTask(this, mBus).execute(event.url, event.scheduleName);
            } else {
                showNoConnectionAlertDialog();
            }
        }
    }

    /**
     * Display schedule.
     *
     * @param event Event with stored schedule.
     */
    @Subscribe
    public void onDownloadSchedule(OnDownloadScheduleEvent event) {
        if (event.schedule != null) {
            createCacheFile(event.schedule);
            replaceContainer(ScheduleFragment.newInstance(event.schedule));
        } else {
            showErrorAlertDialog();
        }
    }

    /**
     * Create a cached file for schedule.
     *
     * @param schedule Schedule to cache.
     */
    private void createCacheFile(Schedule schedule) {
        try {
            // Create cached file.
            File tempFile = new File(getCacheDir(), schedule.name + ".json");
            FileWriter fileWriter = new FileWriter(tempFile);
            String json = GsonHelper.toJsonString(schedule);
            fileWriter.write(json);
            fileWriter.close();
            Timber.d("Cached file " + tempFile.getCanonicalPath() + " successfully created.");
        } catch (IOException e) {
            Timber.e("Error creating temp file for " + schedule.name + ".json", e);
        }
    }

    /**
     * Show dialog, display undo snackbar, and display a persistent snackbar.
     *
     * @param event Event with stored alarm details and reminder messages to display in snackbars.
     */
    @Subscribe
    public void onReminderSet(OnReminderSetEvent event) {
        buildReminderSetDialog(event);
    }

    /**
     * Build and show a dialog to set a reminder.
     *
     * @param event Event passed along. alarmCalendar and alarmPendingIntent are retrieved from event.
     */
    private void buildReminderSetDialog(final OnReminderSetEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder")
                .setMessage(event.reminderDialogMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlarmManager.set(AlarmManager.RTC_WAKEUP, event.alarmCalendar.getTimeInMillis(), event.alarmPendingIntent);
                        displayUndoReminderSnackbar(event);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog setReminderDialog = builder.create();
        setReminderDialog.show();
    }

    /**
     * Display an undo reminder snackbar.
     *
     * @param event Event passed along. undoSnackbarMessage, persistentSnackbarMessage, and
     *              alarmPendingIntent retrieved from event.
     */
    private void displayUndoReminderSnackbar(final OnReminderSetEvent event) {
        SnackbarManager.show(
                Snackbar.with(this)
                        .text(event.undoSnackbarMessage)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {

                            }

                            @Override
                            public void onShown(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {
                                // Show persistent reminder after undo snackbar is dismissed.
                                saveReminderSetPreference(true);
                                saveReminderMessagePreference(event.persistentSnackbarMessage);
                                displayPersistentReminderSnackbar(event.persistentSnackbarMessage);
                                Timber.d(event.undoSnackbarMessage);
                            }
                        })
                        .actionLabel("UNDO")
                        .actionColorResource(R.color.light_blue)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                mAlarmManager.cancel(event.alarmPendingIntent);
                                saveReminderSetPreference(false);
                                Timber.d("Undo Snackbar - Reminder cancelled.");
                            }
                        }),
                this
        );
    }

    /**
     * Display a persistent reminder snackbar.
     *
     * @param persistentReminderMessage Message to display in snackbar.
     */
    private void displayPersistentReminderSnackbar(String persistentReminderMessage) {
        SnackbarManager.show(
                Snackbar.with(this)
                        .text(persistentReminderMessage)
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .actionLabel("CANCEL")
                        .actionColorResource(R.color.light_blue)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                mAlarmManager.cancel(mCancelAlarmPendingIntent);
                                saveReminderSetPreference(false);
                                Timber.d("Persistent snackbar - Reminder cancelled.");
                            }
                        }),
                this
        );
    }

    /**
     * Save whether reminder has been set or not, to ensure that it is not lost when MainActivity is
     * recreated.
     *
     * @param reminderSet true if set, false otherwise
     */
    private void saveReminderSetPreference(boolean reminderSet) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, reminderSet);
        editor.apply();
    }

    /**
     * Save persistent snackbar message to ensure that it is not lost when MainActivity is recreated.
     *
     * @param persistentReminderMessage Message to save.
     */
    private void saveReminderMessagePreference(String persistentReminderMessage) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SettingsFragment.KEY_PREF_REMINDER_TIME_MESSAGE, persistentReminderMessage);
        editor.apply();
    }
}