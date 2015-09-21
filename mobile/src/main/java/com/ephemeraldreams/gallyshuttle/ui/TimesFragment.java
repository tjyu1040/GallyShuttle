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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ui.adapters.TimesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimesFragment extends BaseFragment implements TimesRecyclerViewAdapter.OnTimeClickListener {

    private static final String ARG_TIMES = "times";

    @Bind(R.id.times_recycler_view) RecyclerView timesRecyclerView;
    private TimesRecyclerViewAdapter adapter;

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
        getActivityComponent().inject(this);
        List<String> times;
        if (getArguments() != null) {
            times = getArguments().getStringArrayList(ARG_TIMES);
        } else {
            times = new ArrayList<>();
        }

        adapter = new TimesRecyclerViewAdapter(times);
        adapter.setOnTimeClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_times, container, false);
        ButterKnife.bind(this, view);

        timesRecyclerView.setHasFixedSize(true);
        timesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        timesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onTimeClick(final String time) {
        Snackbar.make(timesRecyclerView, time, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: handle alarm click
                    }
                })
                .show();
    }
}
