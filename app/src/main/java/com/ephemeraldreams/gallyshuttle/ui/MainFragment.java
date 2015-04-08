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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ephemeraldreams.gallyshuttle.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment to show schedule options for users to select.
 */
public class MainFragment extends Fragment {

    /**
     * Required empty public constructor
     */
    public MainFragment() {

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.continuous_button, R.id.alt_continuous_button, R.id.late_night_button,
            R.id.weekend_button, R.id.modified_button})
    public void buildScheduleFragment(Button button) {
        switch (button.getId()) {
            case R.id.continuous_button:
                ScheduleActivity.launchActivity(getActivity(), ScheduleActivity.CONTINUOUS);
                break;
            case R.id.alt_continuous_button:
                ScheduleActivity.launchActivity(getActivity(), ScheduleActivity.ALT_CONTINUOUS);
                break;
            case R.id.late_night_button:
                ScheduleActivity.launchActivity(getActivity(), ScheduleActivity.LATE_NIGHT);
                break;
            case R.id.weekend_button:
                ScheduleActivity.launchActivity(getActivity(), ScheduleActivity.WEEKEND);
                break;
            case R.id.modified_button:
                ScheduleActivity.launchActivity(getActivity(), ScheduleActivity.MODIFIED);
                break;
        }
    }
}
