package com.ephemeraldreams.gallyshuttle.net;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ActivityScope;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.api.ListSchedulesDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.ListStationsDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.StationsTimesMapDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.net.api.models.Station;
import com.ephemeraldreams.gallyshuttle.net.gcm.RegistrationApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * A module for network-specific dependencies.
 */
@Module
public class NetworkModule {

    private static final String GALLY_SHUTTLE_API_BASE_URL = "https://gallyshuttle.appspot.com/";

    @Provides
    @ActivityScope
    OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @ActivityScope
    GallyShuttleApiService gallyShuttleApiService(OkHttpClient okHttpClient) {

        //TODO: decide on whether to include list deserializers.
        Type stationsTimesType = new TypeToken<LinkedHashMap<Station, ArrayList<String>>>() {}.getType();
        Type stationListType = new TypeToken<List<Station>>() {}.getType();
        Type scheduleListType = new TypeToken<List<Schedule>>() {}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(stationsTimesType, new StationsTimesMapDeserializer())
                .registerTypeAdapter(stationListType, new ListStationsDeserializer())
                .registerTypeAdapter(scheduleListType, new ListSchedulesDeserializer())
                .create();

        return new Retrofit.Builder()
                .baseUrl(GALLY_SHUTTLE_API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(GallyShuttleApiService.class);
    }

    //TODO: update registration api url.
    @Provides
    @ActivityScope
    RegistrationApiService registrationApiService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("")
                .client(okHttpClient)
                .build()
                .create(RegistrationApiService.class);
    }
}
