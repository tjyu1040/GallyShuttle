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

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;
import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.data.ScheduleUtils;
import com.ephemeraldreams.gallyshuttle.data.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.receivers.ArrivalNotificationReceiver;
import com.ephemeraldreams.gallyshuttle.util.DateUtils;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Fragment to show schedule options for users to select.
 */
public class MainFragment extends Fragment implements Observer<ApiResponse>, AdapterView.OnItemSelectedListener {

    private static final int SATURDAY = 6;
    private static final int SUNDAY = 7;

    @InjectView(R.id.current_schedule_text_view) TextView currentScheduleTextView;
    @InjectView(R.id.station_spinner) Spinner spinner;
    @InjectView(R.id.timer_countdown_text_view) TextView timeCountDownTextView;
    @InjectView(R.id.arrival_time_text_view) TextView arrivalTimeTextView;
    @InjectView(R.id.notifications_enabled_checkbox) CheckBox notificationsEnabledCheckBox;

    @Inject ShuttleApiService shuttleApiService;
    @Inject CacheManager cacheManager;
    private BroadcastReceiver networkStateBroadCastReceiver;
    private boolean isNetworkStateBroadcastReceiverRegistered;

    private int scheduleId;
    private Schedule schedule;
    @Inject Resources resources;

    private int stationIndex;
    private ArrayAdapter<CharSequence> stationStopsAdapter;
    private LocalDateTime arrivalTime;
    private CountDownTimer countDownTimer;
    @Inject AlarmManager alarmManager;
    @Inject NotificationManager notificationManager;
    private PendingIntent notificationPendingIntent;

    /**
     * Required empty public constructor
     */
    public MainFragment() {

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        setScheduleId();
        currentScheduleTextView.setText(ScheduleUtils.getScheduleTitle(scheduleId, getResources()));

        stationStopsAdapter = ArrayAdapter.createFromResource(getActivity(), scheduleId, R.layout.support_simple_spinner_dropdown_item);
        stationStopsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(stationStopsAdapter);
        spinner.setOnItemSelectedListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadSchedule();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (networkStateBroadCastReceiver != null && !isNetworkStateBroadcastReceiverRegistered) {
            registerNetworkBroadcastReceiver();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (networkStateBroadCastReceiver != null && isNetworkStateBroadcastReceiverRegistered) {
            unregisterNetworkBroadcastReceiver();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            Timber.d("Count down timer canceled.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        stationIndex = position;
        loadSchedule();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @OnCheckedChanged(R.id.notifications_enabled_checkbox)
    public void onNotificationsEnabledCheckBoxClicked(boolean checked) {
        setArrivalNotificationTimer(checked);
    }

    private void setScheduleId() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        int minute = now.getMinuteOfHour();
        if (hour >= 21 || (hour == 0 && minute <= 15)) {
            scheduleId = R.array.late_night_stations;
        } else {
            int day = now.getDayOfWeek();
            switch (day) {
                case SATURDAY:
                case SUNDAY:
                    scheduleId = R.array.weekend_stations;
                    break;
                default:
                    scheduleId = R.array.continuous_stations;
                    break;
            }
        }
    }

    /**
     * Start counting down to next arrival time.
     */
    private void startCountDown() {
        long millisInFuture = calculateTimeFromNowToNextArrivalAtStation(stationIndex);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (millisInFuture <= 0) {
            timeCountDownTextView.setText(getString(R.string.timer_countdown_error));
        } else {
            countDownTimer = new CountDownTimer(millisInFuture, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (timeCountDownTextView != null) {
                        timeCountDownTextView.setText(DateUtils.convertMillisecondsToTime(millisUntilFinished));
                    }
                }

                @Override
                public void onFinish() {
                    loadSchedule();
                }
            }.start();
            setArrivalNotificationTimer(notificationsEnabledCheckBox.isChecked());
        }
    }

    /**
     * Calculate time difference in milliseconds between now and next available shuttle arrival time.
     *
     * @param stationId Station id to get times from.
     * @return Duration between now and next arrival time in milliseconds.
     */
    private long calculateTimeFromNowToNextArrivalAtStation(int stationId) {
        List<String> times = schedule.getTimes(stationId);
        LocalDateTime now = LocalDateTime.now();
        arrivalTime = null;
        LocalDateTime stationTime;
        for (String time : times) {
            stationTime = DateUtils.parseToLocalDateTime(time);
            Timber.d(stationTime.toString());

            //Workaround midnight exception case where station time was converted to midnight of current day instead of next day.
            if (stationTime.getHourOfDay() == 0 && now.getHourOfDay() != 0) {
                stationTime = stationTime.plusDays(1);
            }
            if (now.isBefore(stationTime)) {
                arrivalTime = stationTime;
                break;
            }
        }
        if (arrivalTime == null) {
            arrivalTimeTextView.setText(getString(R.string.arrival_not_available_message));
            return -1;
        } else {
            arrivalTimeTextView.setText(String.format(getString(R.string.until_arrival_time_message), DateUtils.formatTime(arrivalTime)));

            Duration duration = new Duration(now.toDateTime(), arrivalTime.toDateTime());
            long milliseconds = duration.getMillis();

            Timber.d("Now: " + DateUtils.formatTime(now));
            Timber.d("Arrival: " + DateUtils.formatTime(arrivalTime));
            Timber.d("Time difference between now and arrival: " + DateUtils.convertMillisecondsToTime(milliseconds));

            return milliseconds;
        }
    }

    /**
     * Set a timer for arrival notification.
     *
     * @param notificationsBoxChecked Notification check box's checked status.
     */
    private void setArrivalNotificationTimer(boolean notificationsBoxChecked) {
        if (notificationsBoxChecked) {
            if (arrivalTime != null) {
                Intent notificationIntent = new Intent(getActivity(), ArrivalNotificationReceiver.class);
                notificationIntent.putExtra(ArrivalNotificationReceiver.EXTRA_STATION_NAME, stationStopsAdapter.getItem(stationIndex));
                notificationPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Duration duration = new Duration(LocalDateTime.now().toDateTime(), arrivalTime.toDateTime());
                long millis = duration.getMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, notificationPendingIntent);
                Timber.d("Arrival Notification receiver set: " + DateUtils.convertMillisecondsToTime(millis));
            } else {
                Timber.d("Arrival Notification receiver not set. Arrival time is null.");
            }
        } else {
            alarmManager.cancel(notificationPendingIntent);
            Timber.d("Arrival Notification receiver cancelled.");
        }
    }

    @Override
    public void onNext(ApiResponse apiResponse) {
        schedule = new Schedule(scheduleId, apiResponse, resources);
        cacheManager.createScheduleCacheFile(schedule);
    }

    @Override
    public void onCompleted() {
        startCountDown();
        Timber.d("Download complete. Starting countdown...");
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error downloading schedule.");
        registerNetworkBroadcastReceiver();
    }

    /**
     * Attempt to load from cache or download schedule from web.
     */
    private void loadSchedule() {

        setScheduleId();

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
        getActivity().registerReceiver(networkStateBroadCastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        isNetworkStateBroadcastReceiverRegistered = true;
    }

    /**
     * Unregister {@link #networkStateBroadCastReceiver} and try loading schedule from web.
     */
    private void unregisterNetworkBroadcastReceiver() {
        loadScheduleFromWeb();
        getActivity().unregisterReceiver(networkStateBroadCastReceiver);
        isNetworkStateBroadcastReceiverRegistered = false;
        networkStateBroadCastReceiver = null;
        Timber.d("Network broadcast receiver dismissed.");
    }
}
