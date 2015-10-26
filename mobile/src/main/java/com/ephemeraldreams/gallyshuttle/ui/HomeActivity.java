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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.DateUtils;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.StationsSpinnerAdapter;
import com.ephemeraldreams.gallyshuttle.ui.receivers.ArrivalNotificationReceiver;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.LocalDateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import timber.log.Timber;

public class HomeActivity extends BaseScheduleActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Keys to remember whether user has rated the application yet.
    private static final String APP_RATED_KEY = "base.app.rated";
    private static final String APP_OPENED_COUNT_KEY = "base.app.opened.count";

    // Flag to open rate dialog on every 7th app opened count.
    private static final int APP_OPENED_COUNT_FLAG = 7;

    // Count down interval of 1 second.
    private static final long COUNT_DOWN_INTERVAL = 1000;

    @Bind(R.id.countdown_card_view) CardView countdownCardView;
    @Bind(R.id.schedule_title_text_view) TextView scheduleTitleTextView;
    @Bind(R.id.countdown_timer_text_view) TextView countdownTimerTextView;
    @Bind(R.id.arrival_time_text_view) TextView arrivalTimeTextView;
    @Bind(R.id.station_spinner) Spinner stationSpinner;
    @Bind(R.id.arrival_notification_switch) SwitchCompat notificationSwitch;
    private StationsSpinnerAdapter stationAdapter;
    private int currentSelectedStationPosition = 0;
    private Schedule schedule;
    private long currentArrivalTimeMillis;
    private CountDownTimer countDownTimer;

    @Inject AlarmManager alarmManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject GoogleApiClient googleApiClient;
    private Intent cachedInvitationIntent;
    private BroadcastReceiver referralReceiver;
    private PendingIntent notificationPendingIntent;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getComponent().inject(this);

        stationSpinner.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            processReferral(getIntent());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReferralReceiver();
        checkAppRated();
        stationSpinner.setSelection(currentSelectedStationPosition);
        googleApiClient.registerConnectionCallbacks(this);
        googleApiClient.registerConnectionFailedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        unregisterReferralReceiver();
        updateAppOpenedCount();
        if (googleApiClient.isConnectionCallbacksRegistered(this)) {
            googleApiClient.unregisterConnectionCallbacks(this);
        }
        if (googleApiClient.isConnectionFailedListenerRegistered(this)) {
            googleApiClient.unregisterConnectionFailedListener(this);
        }
    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        return R.id.nav_home;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.nav_home);
    }

    @Override
    protected int getSchedulePathId() {
        return getCurrentSchedulePathId();
    }

    @StringRes
    private int getCurrentSchedulePathId() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        int minute = now.getMinuteOfHour();
        if (hour >= 21 || (hour == 0 && minute <= 15)) {
            return R.string.path_late_night;
        } else {
            int day = now.getDayOfWeek();
            switch (day) {
                case DateUtils.SATURDAY:
                case DateUtils.SUNDAY:
                    return R.string.path_weekend;
                default:
                    return R.string.path_continuous;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSelectedStationPosition = position;
        updateUI(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void updateUiOnResponse(Schedule schedule) {
        this.schedule = schedule;
        stationAdapter = new StationsSpinnerAdapter(this, schedule.getStations());
        stationAdapter.notifyDataSetChanged();
        stationSpinner.setAdapter(stationAdapter);
        updateUI(stationSpinner.getSelectedItemPosition());
        countdownCardView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUiOnRefreshStart() {
        countdownCardView.setVisibility(View.GONE);
    }

    private void updateUI(int stationIndex) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        scheduleTitleTextView.setText(getString(R.string.schedule_name_fmt, schedule.name));
        LocalDateTime currentArrivalTime = getArrivalTimeAfterNow(stationIndex);
        if (currentArrivalTime != null) {
            arrivalTimeTextView.setText(getString(R.string.arrival_time_fmt, DateUtils.formatTime(currentArrivalTime)));
            long millisInFuture = DateUtils.calculateDuration(LocalDateTime.now(), currentArrivalTime);
            currentArrivalTimeMillis = currentArrivalTime.toDate().getTime();
            startCountDown(millisInFuture);
        } else {
            Timber.e("Arrival time is null...");
        }
    }

    @Nullable
    private LocalDateTime getArrivalTimeAfterNow(int stationIndex) {
        List<String> times = schedule.getTimes(stationIndex);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime arrivalTime;
        for (String time : times) {
            arrivalTime = DateUtils.parseToLocalDateTime(time);
            if (arrivalTime.getHourOfDay() == 0 && now.getHourOfDay() != 0) {
                arrivalTime = arrivalTime.plusDays(1);
            }
            if (now.isBefore(arrivalTime)) {
                return arrivalTime;
            }
        }
        return null;
    }

    private void startCountDown(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTimerTextView.setText(DateUtils.convertMillisecondsToTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                reloadSchedule();
            }
        }.start();
        setArrivalNotificationReceiver(currentArrivalTimeMillis, notificationSwitch.isChecked());
    }

    @OnCheckedChanged(R.id.arrival_notification_switch)
    public void onNotificationSwitched(boolean switched) {
        setArrivalNotificationReceiver(currentArrivalTimeMillis, switched);
    }

    private void setArrivalNotificationReceiver(long notificationMillis, boolean notificationsEnabled) {
        if (notificationsEnabled) {
            Intent notificationIntent = new Intent(this, ArrivalNotificationReceiver.class);
            String stationName = stationAdapter.getStationName(stationSpinner.getSelectedItemPosition());
            notificationIntent.putExtra(ArrivalNotificationReceiver.STATION_NAME_EXTRA, stationName);
            notificationPendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationMillis, notificationPendingIntent);
            Timber.d("Arrival notification receiver set.");
        } else {
            alarmManager.cancel(notificationPendingIntent);
            Timber.d("Arrival notification receiver canceled.");
        }
    }

    private void checkAppRated() {
        boolean isAppRated = sharedPreferences.getBoolean(APP_RATED_KEY, false);
        if (!isAppRated) {
            int appOpenedCount = sharedPreferences.getInt(APP_OPENED_COUNT_KEY, 1);
            Timber.d("Opened app count: %s", appOpenedCount);
            if (appOpenedCount % APP_OPENED_COUNT_FLAG == 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.rate_dialog_title)
                        .setMessage(R.string.rate_dialog_message)
                        .setPositiveButton(R.string.rate_now_dialog_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences.edit().putBoolean(APP_RATED_KEY, true).apply();
                            }
                        })
                        .setNeutralButton(R.string.no_thanks_dialog_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences.edit().putBoolean(APP_RATED_KEY, true).apply();
                            }
                        })
                        .setNegativeButton(R.string.rate_later_dialog_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            }
        }
    }

    private void updateAppOpenedCount() {
        boolean isAppRated = sharedPreferences.getBoolean(APP_RATED_KEY, false);
        if (!isAppRated) {
            int appOpenedCount = sharedPreferences.getInt(APP_OPENED_COUNT_KEY, 1);
            sharedPreferences.edit().putInt(APP_OPENED_COUNT_KEY, ++appOpenedCount).apply();
            Timber.d("Closed app count: " + appOpenedCount);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("googleApiClient#onConnected()");
        if (cachedInvitationIntent != null) {
            updateInvitationStatus(cachedInvitationIntent);
            cachedInvitationIntent = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("googleApiClient#onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.d("googleApiClient#onConnectionFailed(): %s", connectionResult.getErrorCode());
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Timber.w("googleApiClient#onConnectionFailed() because an API was unavailable.");
        }
    }

    /**
     * Process app invitation referral.
     *
     * @param intent Invitation intent to process.
     */
    private void processReferral(Intent intent) {
        if (AppInviteReferral.hasReferral(intent)) {

            String invitationId = AppInviteReferral.getInvitationId(intent);
            String deepLink = AppInviteReferral.getDeepLink(intent);

            //TODO: Update dialog message.
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.invite_dialog_title))
                    .setMessage("Invitation Id=" + invitationId + ", Deep link=" + deepLink)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            alertDialog.show();

            if (googleApiClient.isConnected()) {
                updateInvitationStatus(intent);
            } else {
                Timber.w("googleApiClient not connected, cannot update invitation.");
                cachedInvitationIntent = intent;
            }
        }
    }

    /**
     * Mark invitation as successful.
     *
     * @param invitationIntent Intent to update invitation installation success.
     */
    private void updateInvitationStatus(Intent invitationIntent) {
        String invitationId = AppInviteReferral.getInvitationId(invitationIntent);
        if (AppInviteReferral.isOpenedFromPlayStore(invitationIntent)) {
            AppInvite.AppInviteApi.updateInvitationOnInstall(googleApiClient, invitationId);
        }
        AppInvite.AppInviteApi.convertInvitation(googleApiClient, invitationId);
    }

    /**
     * Register a local broadcast receiver to listen for invitation referral.
     */
    private void registerReferralReceiver() {
        Timber.d("referralReceiver registered.");
        referralReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReferral(intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.action_deep_link));
        LocalBroadcastManager.getInstance(this).registerReceiver(referralReceiver, intentFilter);
    }

    /**
     * Unregister a local broadcast receiver to stop listening for invitation referral.
     */
    private void unregisterReferralReceiver() {
        if (referralReceiver != null) {
            Timber.d("referralReceiver unregistered.");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(referralReceiver);
        }
    }
}
