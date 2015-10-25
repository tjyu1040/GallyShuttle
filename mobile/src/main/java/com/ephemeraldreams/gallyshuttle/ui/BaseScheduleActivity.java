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

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Inject;

import butterknife.Bind;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * Base activity to handle schedule loading.
 */
public abstract class BaseScheduleActivity extends BaseActivity implements Callback<Schedule> {

    @Bind(R.id.progress_layout) RelativeLayout progressLayout;

    private Schedule schedule;
    @Inject CacheManager cacheManager;
    @Inject GallyShuttleApiService gallyShuttleApiService;
    private Call<Schedule> scheduleCall;

    @Override
    protected void onStart() {
        super.onStart();
        loadSchedule(getSchedulePathId());
    }

    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Attempt to reload from cache first, then download from network if cache is not available.
     */
    public void reloadSchedule() {
        loadSchedule(getSchedulePathId());
    }

    /**
     * Re-download schedule.
     */
    public void refresh() {
        updateUiOnRefreshStart();
        progressLayout.setVisibility(View.VISIBLE);
        if (scheduleCall != null) {
            scheduleCall.cancel();
        }
        download(getSchedulePathId());
    }

    private void loadSchedule(@StringRes int schedulePathId) {
        progressLayout.setVisibility(View.VISIBLE);

        schedule = loadCache(schedulePathId);
        if (schedule == null) {
            download(schedulePathId);
        } else {
            updateUiOnResponse(schedule);
            progressLayout.setVisibility(View.GONE);
        }
    }

    private Schedule loadCache(@StringRes int schedulePathId) {
        try {
            schedule = cacheManager.loadScheduleCache(getString(schedulePathId));
        } catch (IOException e) {
            Timber.e(e, "Error loading schedule %s from cache", getString(schedulePathId));
            schedule = null;
        }
        return schedule;
    }

    private void download(@StringRes int schedulePathId) {
        scheduleCall = gallyShuttleApiService.loadSchedule(getString(schedulePathId));
        scheduleCall.enqueue(this);
    }

    @Override
    public void onResponse(Response<Schedule> response, Retrofit retrofit) {
        schedule = response.body();
        Timber.d("%s", schedule.toString());
        cacheManager.cacheSchedule(schedule);
        progressLayout.setVisibility(View.GONE);
        updateUiOnResponse(schedule);
    }

    @Override
    public void onFailure(Throwable t) {
        progressLayout.setVisibility(View.GONE);
        if (t instanceof SocketTimeoutException) {
            Snackbar.make(coordinatorLayout, R.string.connection_timed_out_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    }).show();
        } else {
            Snackbar.make(coordinatorLayout, R.string.no_connection_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    }).show();
        }
    }

    /**
     * Update UI based on load response.
     */
    abstract protected void updateUiOnResponse(Schedule schedule);

    /**
     * Update UI based on refresh start.
     */
    abstract protected void updateUiOnRefreshStart();

    /**
     * @return Schedule path id to load for this {@link BaseScheduleActivity}.
     */
    @StringRes
    abstract protected int getSchedulePathId();
}
