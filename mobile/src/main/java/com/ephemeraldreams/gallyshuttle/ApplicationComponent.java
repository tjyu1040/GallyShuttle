package com.ephemeraldreams.gallyshuttle;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(GallyShuttleApplication gallyShuttleApplication);

    Application application();
}
