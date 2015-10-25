package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;

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

    void inject(HomeActivity homeActivity);
    void inject(ScheduleActivity scheduleActivity);
    void inject(TimesFragment timesFragment);
    void inject(SettingsActivity settingsActivity);
    void inject(SettingsFragment settingsFragment);
    void inject(AboutActivity aboutActivity);
}
