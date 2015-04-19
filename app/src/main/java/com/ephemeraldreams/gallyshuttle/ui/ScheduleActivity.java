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
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmVibration;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;
import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.data.ScheduleUtils;
import com.ephemeraldreams.gallyshuttle.data.models.Schedule;
import com.ephemeraldreams.gallyshuttle.data.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesFragmentPagerAdapter;
import com.ephemeraldreams.gallyshuttle.ui.events.PrepareAlarmReminderEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Activity to handle preparation and displaying schedule.
 */
public class ScheduleActivity extends BaseActivity implements Observer<ApiResponse> {

    public static final int[] SCHEDULE_IDS = {
            R.array.continuous_stations,
            R.array.alt_continuous_stations,
            R.array.late_night_stations,
            R.array.weekend_stations,
            R.array.modified_stations
    };

    public static final String EXTRA_SCHEDULE = "schedule";

    @InjectView(R.id.schedule_pager_tabs_strip) PagerTabStrip schedulePagerTabStrip;
    @InjectView(R.id.schedule_view_pager) ViewPager scheduleViewPager;
    private TimesFragmentPagerAdapter timesFragmentPagerAdapter;

    @Inject ShuttleApiService shuttleApiService;
    @Inject CacheManager cacheManager;
    private ProgressDialog progressDialog;
    private BroadcastReceiver networkStateBroadCastReceiver;
    private boolean isNetworkStateBroadcastReceiverRegistered;

    private int scheduleId;
    private Schedule schedule;
    @Inject Resources resources;

    @Inject Bus bus;
    @Inject FragmentManager fragmentManager;
    @Inject @AlarmRingtoneChoice StringPreference alarmRingtoneChoiceStringPreference;
    @Inject @AlarmVibration BooleanPreference alarmVibrationBooleanPreference;

    /**
     * Launch ScheduleActivity with specified schedule id.
     *
     * @param activity   Activity to launch from.
     * @param scheduleId Schedule to display.
     */
    public static void launchActivity(Activity activity, int scheduleId) {
        Intent intent = new Intent(activity, ScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE, SCHEDULE_IDS[scheduleId]);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        inject(this);

        if (getIntent() != null) {
            scheduleId = getIntent().getIntExtra(EXTRA_SCHEDULE, SCHEDULE_IDS[0]);
        } else {
            scheduleId = SCHEDULE_IDS[0];
        }

        schedulePagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.buff));
        schedulePagerTabStrip.setBackgroundColor(getResources().getColor(R.color.blue));

        loadSchedule();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (networkStateBroadCastReceiver != null && !isNetworkStateBroadcastReceiverRegistered) {
            registerNetworkBroadcastReceiver();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        if (networkStateBroadCastReceiver != null && isNetworkStateBroadcastReceiverRegistered) {
            unregisterNetworkBroadcastReceiver();
        }
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
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attempt to load from cache or download schedule from web.
     */
    private void loadSchedule() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        String title = ScheduleUtils.getScheduleTitle(scheduleId, resources);

        if (cacheManager.scheduleCacheFileExists(cacheManager.getScheduleFile(title))) {
            try {
                schedule = cacheManager.readScheduleCacheFile(title);
                onCompleted();
            } catch (FileNotFoundException e) {
                Timber.e(e, "Cached file for " + title + " schedule not found.");
                loadScheduleFromWeb();
            }
        } else {
            loadScheduleFromWeb();
        }
    }

    /**
     * Download schedule data from web.
     */
    private void loadScheduleFromWeb() {
        Observable<ApiResponse> apiResponseObservable;
        switch (scheduleId) {
            case R.array.continuous_stations:
                apiResponseObservable = shuttleApiService.getContinuousSchedule();
                break;
            case R.array.alt_continuous_stations:
                apiResponseObservable = shuttleApiService.getAlternativeContinuousSchedule();
                break;
            case R.array.late_night_stations:
                apiResponseObservable = shuttleApiService.getLateNightSchedule();
                break;
            case R.array.weekend_stations:
                apiResponseObservable = shuttleApiService.getWeekendSchedule();
                break;
            case R.array.modified_stations:
                apiResponseObservable = shuttleApiService.getModifiedSchedule();
                break;
            default:
                apiResponseObservable = shuttleApiService.getContinuousSchedule();
                break;
        }
        apiResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onNext(ApiResponse apiResponse) {
        schedule = new Schedule(scheduleId, apiResponse, resources);
        cacheManager.createScheduleCacheFile(schedule);
    }

    @Override
    public void onCompleted() {
        getSupportActionBar().setTitle(schedule.title);
        timesFragmentPagerAdapter = new TimesFragmentPagerAdapter(fragmentManager, schedule);
        scheduleViewPager.setAdapter(timesFragmentPagerAdapter);
        progressDialog.dismiss();
        Timber.d("Download complete.");
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error downloading schedule.");
        Toast.makeText(this, "Error loading schedule. Please check your Internet connection.", Toast.LENGTH_LONG).show();
        registerNetworkBroadcastReceiver();
    }

    /**
     * Register a {@link #networkStateBroadCastReceiver}, which monitor for Internet connection
     * re-establishment.
     */
    private void registerNetworkBroadcastReceiver() {
        networkStateBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Internet connection has been re-established.
                unregisterNetworkBroadcastReceiver();
            }
        };
        registerReceiver(networkStateBroadCastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        isNetworkStateBroadcastReceiverRegistered = true;
    }

    /**
     * Unregister {@link #networkStateBroadCastReceiver} and try loading schedule from web.
     */
    private void unregisterNetworkBroadcastReceiver() {
        loadScheduleFromWeb();
        unregisterReceiver(networkStateBroadCastReceiver);
        isNetworkStateBroadcastReceiverRegistered = false;
        networkStateBroadCastReceiver = null;
        Timber.d("Network broadcast receiver dismissed.");
    }

    /**
     * Display reminder confimation dialog and set alarm.
     *
     * @param event Event to get {@link PrepareAlarmReminderEvent#hour}, {@link PrepareAlarmReminderEvent#minute},
     *              and {@link PrepareAlarmReminderEvent#reminderDialogMessage}.
     */
    @Subscribe
    public void setAlarm(PrepareAlarmReminderEvent event) {

        final Intent alarmIntent = buildAlarmIntent(event.hour, event.minute);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Alarm Reminder")
                .setMessage(event.reminderDialogMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(Intent.createChooser(alarmIntent, "Set alarm"));
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
     * Build an alarm intent to pass to other activities or alarm applications.
     *
     * @param hour   Alarm hour to set.
     * @param minute Alarm minute to set
     * @return Alarm intent.
     */
    private Intent buildAlarmIntent(int hour, int minute) {
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        CharSequence station = scheduleViewPager.getAdapter().getPageTitle(scheduleViewPager.getCurrentItem());
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, station + " arrival reminder.");
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmIntent.putExtra(AlarmClock.EXTRA_VIBRATE, alarmVibrationBooleanPreference.get());
            if (TextUtils.isEmpty(alarmRingtoneChoiceStringPreference.get())) {
                alarmIntent.putExtra(AlarmClock.VALUE_RINGTONE_SILENT, true);
            } else {
                alarmIntent.putExtra(AlarmClock.EXTRA_RINGTONE, alarmRingtoneChoiceStringPreference.get());
            }
        }
        return alarmIntent;
    }
}
