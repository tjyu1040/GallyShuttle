package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;

import com.ephemeraldreams.gallyshuttle.ApplicationComponent;
import com.ephemeraldreams.gallyshuttle.annotations.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    Activity activity();
}
