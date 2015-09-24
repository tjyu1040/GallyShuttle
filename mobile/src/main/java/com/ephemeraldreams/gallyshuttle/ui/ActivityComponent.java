package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.ephemeraldreams.gallyshuttle.ApplicationComponent;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;
import com.ephemeraldreams.gallyshuttle.ui.SettingsActivity.SettingsFragment;

import dagger.Component;

/**
 * A component whose lifetime is constrained to an {@link Activity}'s or a
 * {@link android.app.Fragment}'s lifecycle.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);
    void inject(SettingsActivity settingsActivity);
    void inject(ScheduleFragment scheduleFragment);
    void inject(CountDownFragment countDownFragment);
    void inject(TimesFragment timesFragment);
    void inject(SettingsFragment settingsFragment);

    AppCompatActivity activity();
}
