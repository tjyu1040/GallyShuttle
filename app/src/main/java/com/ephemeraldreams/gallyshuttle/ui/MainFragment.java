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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.api.ShuttleApiService;
import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.data.models.Schedule;
import com.ephemeraldreams.gallyshuttle.util.DateUtils;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
    @InjectView(R.id.time_countdown_text_view) TextView timeCountDownTextView;

    @Inject ShuttleApiService shuttleApiService;
    @Inject CacheManager cacheManager;
    private BroadcastReceiver networkStateBroadCastReceiver;
    private boolean isNetworkStateBroadcastReceiverRegistered;

    private int scheduleId;
    private Schedule schedule;
    @Inject Resources resources;

    private LocalDateTime arrivalTime;

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

        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        if (hour >= 10) {
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        currentScheduleTextView.setText(Schedule.getScheduleTitle(scheduleId, getResources()));

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), scheduleId, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        loadSchedule();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadSchedule();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO: find next time for specified station.
        //startCountDown(0);
    }

    private void calculateTimeToArrival(int position) {
        List<LocalTime> stationTimes = schedule.getTimes(position);
        LocalDateTime now = LocalDateTime.now();

        for (LocalTime time : stationTimes) {
            if (time.toDateTimeToday().toLocalDateTime().isAfter(now)) {
                arrivalTime = time.toDateTimeToday().toLocalDateTime();
            }
        }

        if (arrivalTime == null) {
            arrivalTime = new LocalDateTime();
        }

        long millisInFuture = Period.fieldDifference(now, arrivalTime).getMillis();

        startCountDown(millisInFuture);
    }

    private void startCountDown(long millisInFuture) {
        new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCountDownTextView.setText("Minutes remaining: " + DateUtils.convertMillisecondsToMinutes(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //calculateTimeToArrival(storedPosition);
                timeCountDownTextView.setText("Done!");
            }
        }.start();
    }

    /**
     * Attempt to load from cache or download schedule from web.
     */
    private void loadSchedule() {

        String title = Schedule.getScheduleTitle(scheduleId, resources);

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
        //TODO: startCountDown()
        calculateTimeToArrival(0);
        Timber.d("Download complete.");
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error downloading schedule.");
        Toast.makeText(getActivity(), "Error loading schedule. Please check your Internet connection.", Toast.LENGTH_LONG).show();
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
