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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmReminderLength;
import com.ephemeraldreams.gallyshuttle.data.models.StationTimes;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesRecyclerViewAdapter;
import com.ephemeraldreams.gallyshuttle.ui.events.PrepareAlarmReminderEvent;
import com.squareup.otto.Bus;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment to display times for a specific stop.
 */
public class TimesFragment extends Fragment implements TimesRecyclerViewAdapter.OnTimeClickListener {

    private static final String ARG_TIMES = "times";

    @InjectView(R.id.times_recycler_view) RecyclerView timesRecyclerView;
    private TimesRecyclerViewAdapter timesRecyclerViewAdapter;

    @Inject Bus bus;
    @Inject @AlarmReminderLength StringPreference reminderLengthStringPreference;

    public static TimesFragment newInstance(ArrayList<LocalTime> times) {
        TimesFragment fragment = new TimesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TIMES, StationTimes.create(times));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        setRetainInstance(true);

        List<LocalTime> times;
        if (getArguments() != null) {
            StationTimes stationTimes = getArguments().getParcelable(ARG_TIMES);
            times = stationTimes.times();
        } else {
            times = new ArrayList<>();
        }
        timesRecyclerViewAdapter = new TimesRecyclerViewAdapter(times);
        timesRecyclerViewAdapter.setOnTimeClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_times, container, false);
        ButterKnife.inject(this, rootView);

        timesRecyclerView.setHasFixedSize(true);
        timesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        timesRecyclerView.setAdapter(timesRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onTimeClick(LocalTime time) {
        int prefReminderLength = Integer.parseInt(reminderLengthStringPreference.get());
        bus.post(new PrepareAlarmReminderEvent(time, prefReminderLength));
    }
}
