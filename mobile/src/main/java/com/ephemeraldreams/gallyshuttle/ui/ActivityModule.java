package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * A module for activity-specific or fragment-specific dependencies.
 */
@Module()
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity getActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    FragmentManager getFragmentManager(Activity activity) {
        return ((AppCompatActivity) activity).getSupportFragmentManager();
    }
}
