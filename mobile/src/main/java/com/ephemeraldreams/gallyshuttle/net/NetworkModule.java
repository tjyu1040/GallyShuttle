package com.ephemeraldreams.gallyshuttle.net;

import com.ephemeraldreams.gallyshuttle.annotations.ActivityScope;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.Registration;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;

@Module
public class NetworkModule {

    @Provides
    @ActivityScope
    OkHttpClient provideOkHttpClient(){
        return new OkHttpClient();
    }
}
