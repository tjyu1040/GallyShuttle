package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;

import com.ephemeraldreams.gallyshuttle.annotations.ActivityScope;
import com.ephemeraldreams.gallyshuttle.net.NetworkModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = NetworkModule.class)
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity provideActivityContext(){
        return activity;
    }
}
