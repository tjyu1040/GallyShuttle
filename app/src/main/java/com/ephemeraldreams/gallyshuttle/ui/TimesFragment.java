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
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.events.OnReminderSetEvent;
import com.ephemeraldreams.gallyshuttle.receivers.AlarmReceiver;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesRecyclerViewAdapter;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseFragment;
import com.ephemeraldreams.gallyshuttle.ui.listeners.RecyclerViewListener;
import com.ephemeraldreams.gallyshuttle.util.ScheduleTimeFormatter;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.otto.Bus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Fragment to display times for a specific stop.
 */
public class TimesFragment extends BaseFragment implements RecyclerViewListener.OnItemClickListener {

    private static final String ARG_TIMES = "times";

    @InjectView(R.id.timesRecyclerView) RecyclerView mTimesRecyclerView;
    private TimesRecyclerViewAdapter mTimesRecyclerViewAdapter;
    private ArrayList<String> mTimes;

    @Inject Bus mBus;
    @Inject SharedPreferences mSharedPreferences;
    @Inject AlarmManager mAlarmManager;
    private PendingIntent mAlarmPendingIntent;
    private int mPrefReminderMinutes;
    private String mSetDay;
    private Calendar mCalendarAlarm;

    private DateFormat mDateFormat = new SimpleDateFormat("h:mm aa");

    /**
     * Required empty public constructor
     */
    public TimesFragment() {

    }

    public static TimesFragment newInstance(ArrayList<String> times) {
        TimesFragment fragment = new TimesFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TIMES, times);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTimes = getArguments().getStringArrayList(ARG_TIMES);
        }
        mTimesRecyclerViewAdapter = new TimesRecyclerViewAdapter(mTimes);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_times, container, false);
        ButterKnife.inject(this, rootView);

        mTimesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTimesRecyclerView.setLayoutManager(layoutManager);
        mTimesRecyclerView.setAdapter(mTimesRecyclerViewAdapter);
        RecyclerViewListener timesRecyclerViewListener = new RecyclerViewListener(getActivity(), this);
        mTimesRecyclerView.addOnItemTouchListener(timesRecyclerViewListener);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SettingsFragment.REMINDER_PREFERENCES, Context.MODE_PRIVATE);
        boolean reminderSet = sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, false);
        if (!reminderSet){
            TextView timeTextView = ButterKnife.findById(view, R.id.timeTextView);
            String time = timeTextView.getText().toString();

            mCalendarAlarm = Calendar.getInstance();
            mCalendarAlarm.setTime(ScheduleTimeFormatter.parseToDate(time));
            prepareReminder();
        }
    }

    /**
     * Prepare to build reminder dialog.
     */
    private void prepareReminder() {
        mPrefReminderMinutes = Integer.parseInt(mSharedPreferences.getString(SettingsFragment.KEY_PREF_REMINDER_LENGTH, "5"));
        mCalendarAlarm.add(Calendar.MINUTE, -mPrefReminderMinutes);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        String reminderDialogMessage = "Set reminder for ";
        if (mCalendarAlarm.before(calendar)){
            mCalendarAlarm.add(Calendar.DATE, 1);
            mSetDay = " tomorrow";
        } else {
            mSetDay = " today";
        }
        reminderDialogMessage += mDateFormat.format(mCalendarAlarm.getTime()) + mSetDay + "?";

        displayReminderDialog(reminderDialogMessage);
    }

    /**
     * Display reminder dialog.
     * @param reminderDialogMessage Message to display in dialog.
     */
    private void displayReminderDialog(String reminderDialogMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Reminder")
                .setMessage(reminderDialogMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setAlarm();
                        displayReminderSetSnackBar();
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
     * Set an alarm for reminder time.
     */
    private void setAlarm(){
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra(AlarmReceiver.EXTRA_REMINDER, mPrefReminderMinutes);
        mAlarmPendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(getActivity(), AlarmReceiver.class), 0);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendarAlarm.getTimeInMillis(), mAlarmPendingIntent);
    }

    /**
     * Display a snackbar confirming set reminder with an undo button.
     */
    private void displayReminderSetSnackBar(){
        final String timeMessage = mDateFormat.format(mCalendarAlarm.getTime()) + mSetDay;
        final String reminderMessage = "Reminder set for " + timeMessage + "!";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SettingsFragment.REMINDER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, true);
        editor.apply();
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text(reminderMessage)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
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
                                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                                boolean reminderSet = sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, true);
                                if (reminderSet) {
                                    mBus.post(new OnReminderSetEvent(getActivity(), timeMessage, mAlarmPendingIntent));
                                }
                            }
                        })
                        .actionLabel("UNDO")
                        .actionColorResource(R.color.light_blue)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                mAlarmManager.cancel(mAlarmPendingIntent);
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SettingsFragment.REMINDER_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(SettingsFragment.KEY_PREF_REMINDER_SET, false);
                                editor.apply();
                            }
                        }),
                getActivity()
        );

        Timber.d(reminderMessage);
    }
}