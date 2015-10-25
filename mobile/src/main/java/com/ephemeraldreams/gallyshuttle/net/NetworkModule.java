package com.ephemeraldreams.gallyshuttle.net;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.net.api.ListSchedulesDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.ListStationsDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.StationsTimesMapDeserializer;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.ephemeraldreams.gallyshuttle.net.api.models.Station;
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

/**
 * A module for network-specific dependencies.
 */
@Module
public class NetworkModule {

    private static final String API_BASE_URL = "https://gallyshuttle.appspot.com/";
    private static final String GALLYSHUTTLE_API_URL = "_ah/api/gallyshuttle/v1/";
    private static final String REGISTRATION__API_URL = "_ah/api/registration/v1/";

    @Provides
    @ApplicationScope
    OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @ApplicationScope
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
                .baseUrl(API_BASE_URL + GALLYSHUTTLE_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(GallyShuttleApiService.class);
    }
}
