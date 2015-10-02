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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.DateUtils;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.StationsSpinnerAdapter;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.LocalDateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

public class ArrivalCountdownActivity extends BaseScheduleActivity implements AdapterView.OnItemSelectedListener {

    // Keys to remember whether user has rated the application yet.
    private static final String APP_RATED_KEY = "base.app.rated";
    private static final String APP_OPENED_COUNT_KEY = "base.app.opened.count";

    // Flag to open rate dialog on every 7th app opened count.
    private static final int APP_OPENED_COUNT_FLAG = 7;

    // Days constants.
    private static final int SATURDAY = 6;
    private static final int SUNDAY = 7;

    // Count down interval of 1 second.
    private static final long COUNT_DOWN_INTERVAL = 1000;

    @Bind(R.id.arrival_card_view) CardView arrivalCardView;
    @Bind(R.id.card_title_text_view) TextView timerCardTitleTextView;
    @Bind(R.id.timer_text_view) TextView timerTextView;
    @Bind(R.id.arrival_time_text_view) TextView arrivalTimeTextView;
    @Bind(R.id.station_spinner) Spinner stationSpinner;
    private StationsSpinnerAdapter adapter;
    private Schedule schedule;
    private long millisInFuture;
    private CountDownTimer countDownTimer;

    @Inject SharedPreferences sharedPreferences;
    private GoogleApiClient googleApiClient;
    private Intent cachedInvitationIntent;
    private BroadcastReceiver referralReceiver;

    public static void launch(Activity activity){
        Intent intent = new Intent(activity, ArrivalCountdownActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrival_countdown);
        getComponent().inject(this);

        stationSpinner.setOnItemSelectedListener(this);
        /*
        if (savedInstanceState == null) {
            processReferral(getIntent());
        }
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReferralReceiver();
        checkAppRated();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        unregisterReferralReceiver();
        updateAppOpenedCount();
    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        return R.id.nav_home;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.nav_arrival_countdown);
    }

    @Override
    protected int getSchedulePathId() {
        return getCurrentSchedulePathId();
    }

    @StringRes
    private int getCurrentSchedulePathId(){
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        int minute = now.getMinuteOfHour();
        if (hour >= 21 || (hour == 0 && minute <= 15)) {
            return R.string.path_late_night;
        } else {
            int day = now.getDayOfWeek();
            switch (day) {
                case SATURDAY:
                case SUNDAY:
                    return R.string.path_weekend;
                default:
                    return R.string.path_continuous;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateUI(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void updateUiOnResponse(Schedule schedule) {
        this.schedule = schedule;
        adapter = new StationsSpinnerAdapter(this, schedule.getStations());
        adapter.notifyDataSetChanged();
        stationSpinner.setAdapter(adapter);
        updateUI(stationSpinner.getSelectedItemPosition());
        arrivalCardView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUiOnRefreshStart() {
        arrivalCardView.setVisibility(View.GONE);
    }

    private void updateUI(int stationIndex){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerCardTitleTextView.setText(getString(R.string.schedule_name_fmt, schedule.name));
        LocalDateTime arrivalTime = getArrivalTimeAfterNow(stationIndex);
        if (arrivalTime != null) {
            arrivalTimeTextView.setText(getString(R.string.arrival_time_fmt, DateUtils.formatTime(arrivalTime)));
            millisInFuture = DateUtils.calculateDuration(LocalDateTime.now(), arrivalTime);
            startCountDown();
        } else {
            Timber.e("Arrival time is null...");
        }
    }

    private LocalDateTime getArrivalTimeAfterNow(int stationIndex){
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

    private void startCountDown() {
        countDownTimer = new CountDownTimer(millisInFuture, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(DateUtils.convertMillisecondsToTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //TODO: handle finish
                Timber.d("Completed.");
            }
        }.start();
    }

    private void checkAppRated(){
        boolean isAppRated = sharedPreferences.getBoolean(APP_RATED_KEY, false);
        if (!isAppRated) {
            int appOpenedCount = sharedPreferences.getInt(APP_OPENED_COUNT_KEY, 1);
            Timber.d("Opened app count:" + appOpenedCount);
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

    private void updateAppOpenedCount(){
        boolean isAppRated = sharedPreferences.getBoolean(APP_RATED_KEY, false);
        if (!isAppRated) {
            int appOpenedCount = sharedPreferences.getInt(APP_OPENED_COUNT_KEY, 1);
            sharedPreferences.edit().putInt(APP_OPENED_COUNT_KEY, ++appOpenedCount).apply();
            Timber.d("Closed app count: " + appOpenedCount);
        }
    }

    /**
     * Process app invitation referral.
     *
     * @param intent Invitation intent to process.
     */
    private void processReferral(Intent intent) {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Timber.d("googleApiClient#onConnected");
                        if (cachedInvitationIntent != null) {
                            updateInvitationStatus(cachedInvitationIntent);
                            cachedInvitationIntent = null;
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Timber.d("googleApiClient#onConnectionSuspended");
                    }
                })
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Timber.d("googleApiClient#onConnectionFailed:" + connectionResult.getErrorCode());
                        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                            Timber.w("onConnectionFailed because an API was unavailable.");
                        }
                    }
                })
                .addApi(AppInvite.API)
                .build();

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
