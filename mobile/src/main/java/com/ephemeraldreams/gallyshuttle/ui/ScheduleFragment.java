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

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesFragmentPagerAdapter;
import com.ephemeraldreams.gallyshuttle.ui.events.NetworkStateChangedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ScheduleFragment extends BaseFragment implements Observer<Schedule> {

    private static final String EXTRA_SCHEDULE_PATH = "schedule path name";

    @Bind(R.id.view_pager) ViewPager viewPager;
    private TabLayout tabLayout;
    private ActionBar actionBar;
    private TimesFragmentPagerAdapter adapter;
    private ProgressDialog progressDialog;

    private String path;
    private Schedule schedule;

    @Inject Bus bus;
    @Inject CacheManager cacheManager;
    @Inject GallyShuttleApiService gallyShuttleApiService;
    @Inject ConnectivityManager connectivityManager;

    public static ScheduleFragment newInstance(String path) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SCHEDULE_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getActivityComponent().inject(this);

        if (getArguments() != null) {
            path = getArguments().getString(EXTRA_SCHEDULE_PATH);
        } else {
            Timber.d("Failed path.");
            path = "continuous";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);

        actionBar = getSupportActionBar();
        tabLayout = ButterKnife.findById(getActivity(), R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        loadSchedule();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
        tabLayout.setVisibility(View.GONE);
        tabLayout = null;
    }

    private void loadSchedule() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.show();

        try {
            schedule = cacheManager.loadScheduleCache(path);
            onCompleted();
        } catch (FileNotFoundException e) {
            loadFromWeb();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromWeb() {
        Observable<Schedule> scheduleObservable = gallyShuttleApiService.schedule(path);
        scheduleObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        if (actionBar != null) {
            actionBar.setTitle(schedule.name + " Schedule");
        }
        adapter = new TimesFragmentPagerAdapter(getChildFragmentManager(), schedule);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        progressDialog.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        progressDialog.dismiss();
        //TODO: handle error.
        Timber.e(e, "Failed to download.");
    }

    @Override
    public void onNext(Schedule schedule) {
        this.schedule = schedule;
        cacheManager.cacheSchedule(schedule);
    }

    @Subscribe
    public void onNetworkDisconnected(NetworkStateChangedEvent event) {
        //TODO: handle event
        if (!event.isConnected) {
            Timber.d("Disconnected info from ScheduleFragment.");
        } else {
            Timber.d("Connected from ScheduleFragment.");
        }
    }
}
