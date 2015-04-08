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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ShuttleApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

import static com.ephemeraldreams.gallyshuttle.ui.SettingsActivity.SettingsFragment;

/**
 * Base activity for dagger usage.
 */
public class BaseActivity extends ActionBarActivity implements ActivityComponent {

    @Optional @InjectView(R.id.toolbar) Toolbar toolbar;

    private static ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComponent = DaggerActivityComponent.builder()
                .appComponent(ShuttleApplication.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public static ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override
    public void inject(MainActivity mainActivity) {
        activityComponent.inject(mainActivity);
    }

    @Override
    public void inject(ScheduleActivity scheduleActivity) {
        activityComponent.inject(scheduleActivity);
    }

    @Override
    public void inject(SettingsActivity settingsActivity) {
        activityComponent.inject(settingsActivity);
    }

    @Override
    public void inject(MainFragment mainFragment) {
        activityComponent.inject(mainFragment);
    }

    @Override
    public void inject(TimesFragment timesFragment) {
        activityComponent.inject(timesFragment);
    }

    @Override
    public void inject(PoliciesFragment policiesFragment) {
        activityComponent.inject(policiesFragment);
    }

    @Override
    public void inject(SettingsFragment settingsFragment) {
        activityComponent.inject(settingsFragment);
    }

    @Override
    public void inject(AboutDialogFragment aboutDialogFragment) {
        activityComponent.inject(aboutDialogFragment);
    }

    @Override
    public void inject(AboutFragment aboutFragment) {
        activityComponent.inject(aboutFragment);
    }
}
