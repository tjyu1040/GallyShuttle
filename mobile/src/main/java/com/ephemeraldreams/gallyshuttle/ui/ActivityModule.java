package com.ephemeraldreams.gallyshuttle.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;
import com.ephemeraldreams.gallyshuttle.net.NetworkModule;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

/**
 * A module for activity-specific or fragment-specific dependencies.
 */
@Module()
public class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    AppCompatActivity activity() {
        return activity;
    }

    @Provides
    @ActivityScope
    FragmentManager fragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }
}
