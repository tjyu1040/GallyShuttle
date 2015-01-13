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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ephemeraldreams.gallyshuttle.Constants;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.events.OnScheduleStartEvent;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseFragment;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment to show schedule options for users to select.
 */
public class MainFragment extends BaseFragment {

    @Inject Bus mBus;

    /**
     * Required empty public constructor
     */
    public MainFragment() {

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.main_fragment_title);
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

    @OnClick({R.id.continuousButton, R.id.lateNightButton, R.id.weekendButton, R.id.modifiedButton})
    public void buildScheduleFragment(Button button) {
        String url = "";
        String scheduleName = "";
        switch (button.getId()) {
            case R.id.continuousButton:
                url = Constants.CONTINUOUS_URL;
                scheduleName = Constants.CONTINUOUS_NAME;
                break;
            case R.id.lateNightButton:
                url = Constants.LATE_NIGHT_URL;
                scheduleName = Constants.LATE_NIGHT_NAME;
                break;
            case R.id.weekendButton:
                url = Constants.WEEKEND_URL;
                scheduleName = Constants.WEEKEND_NAME;
                break;
            case R.id.modifiedButton:
                url = Constants.MODIFIED_URL;
                scheduleName = Constants.MODIFIED_NAME;
                break;
        }
        mBus.post(new OnScheduleStartEvent(url, scheduleName));
    }
}