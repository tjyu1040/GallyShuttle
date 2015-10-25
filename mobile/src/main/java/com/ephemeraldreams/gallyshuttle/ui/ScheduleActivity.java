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
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.DateUtils;
import com.ephemeraldreams.gallyshuttle.content.database.models.Alarm;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesFragmentPagerAdapter;
import com.ephemeraldreams.gallyshuttle.ui.events.OnAlarmClickEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.LocalDateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Activity to display schedule.
 */
public class ScheduleActivity extends BaseScheduleActivity implements TimePickerDialog.OnTimeSetListener {

    public static final String PATH_EXTRA = "schedule.path.extra";

    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.schedule_view_pager) ViewPager scheduleViewPager;
    @Inject FragmentManager fragmentManager;
    @Inject NotificationManagerCompat notificationManagerCompat;
    private TimesFragmentPagerAdapter adapter;

    @Inject Bus bus;
    @Inject AlarmManager alarmManager;
    @Inject SharedPreferences sharedPreferences;
    private LocalDateTime arrivalTime;
    private LocalDateTime alarmTime;

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

        pathId = getIntent().getIntExtra(PATH_EXTRA, R.string.path_continuous);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
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
        scheduleViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(scheduleViewPager);
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

    @Subscribe
    public void setAlarm(OnAlarmClickEvent event) {
        arrivalTime = event.arrivalTime;
        alarmTime = event.arrivalTime;
        int reminder = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_key_alarm_reminder), "5"));
        alarmTime = alarmTime.minusMinutes(reminder);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                alarmTime.getHourOfDay(),
                alarmTime.getMinuteOfHour(),
                DateFormat.is24HourFormat(this)
        );
        timePickerDialog.setTitle("Set Alarm");
        timePickerDialog.setMessage("Pick an alarm time to set.");
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        LocalDateTime now = LocalDateTime.now();
        alarmTime = LocalDateTime.fromCalendarFields(calendar);

        // Special case where 12:00 AM is selected.
        if (alarmTime.isBefore(now) && pathId == R.string.path_late_night) {
            if (alarmTime.getHourOfDay() == 0) {
                alarmTime = alarmTime.plusDays(1);
            }
        }

        if (now.isBefore(alarmTime)) {
            // Set alarm for today.
            showAlarmDialog("Set for today?", "Alarm will be set for today.");
        } else {
            if (now.getDayOfWeek() == DateUtils.FRIDAY) {
                // Set alarm for next Monday.
                alarmTime = alarmTime.plusDays(2);
                showAlarmDialog("Set for next Monday?", "Alarm will be set for next Monday.");
            } else if (now.getDayOfWeek() == DateUtils.SUNDAY) {
                // Set alarm for next Saturday.
                alarmTime = alarmTime.plusDays(6);
                showAlarmDialog("Set for next Saturday?", "Alarm will be set for next Saturday.");
            } else {
                alarmTime = alarmTime.plusDays(1);
                showAlarmDialog("Set for tomorrow?", "Alarm will be set for tomorrow.");
            }
        }
    }

    private void showAlarmDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setAlarm(alarmTime);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private void setAlarm(LocalDateTime alarmTime) {

        Alarm alarm = new Alarm.Builder(this)
                .setArrivalTime(DateUtils.formatTime(arrivalTime))
                .setStationName(scheduleViewPager.getAdapter().getPageTitle(scheduleViewPager.getCurrentItem()).toString())
                .setTriggerTime(alarmTime.toDate().getTime())
                .build();

        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, alarm.getStationName() + " arrival reminder.");
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, alarmTime.getHourOfDay());
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, alarmTime.getMinuteOfHour());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmIntent.putExtra(AlarmClock.EXTRA_VIBRATE, sharedPreferences.getBoolean(getString(R.string.pref_key_alarm_vibrate), true));
            String ringtone = sharedPreferences.getString(getString(R.string.pref_key_alarm_ringtone), "");
            if (TextUtils.isEmpty(ringtone)) {
                alarmIntent.putExtra(AlarmClock.VALUE_RINGTONE_SILENT, true);
            } else {
                alarmIntent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtone);
            }
        }

        startActivity(Intent.createChooser(alarmIntent, "Set alarm"));
        Snackbar.make(coordinatorLayout, "Alarm set for " + DateUtils.formatTime(alarmTime), Snackbar.LENGTH_LONG).show();
    }
}
