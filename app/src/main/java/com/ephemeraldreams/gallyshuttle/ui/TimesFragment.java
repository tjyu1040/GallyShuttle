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

import android.content.Context;
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
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesRecyclerViewAdapter;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseFragment;
import com.ephemeraldreams.gallyshuttle.ui.listeners.RecyclerViewListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment to display times for a specific stop.
 */
public class TimesFragment extends BaseFragment implements RecyclerViewListener.OnItemClickListener {

    private static final String ARG_TIMES = "times";

    @InjectView(R.id.timesRecyclerView) RecyclerView mTimesRecyclerView;
    private TimesRecyclerViewAdapter mTimesRecyclerViewAdapter;
    private ArrayList<String> mTimes;

    @Inject Bus mBus;

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
            mBus.post(new OnReminderSetEvent(getActivity(), time));
        }
    }
}