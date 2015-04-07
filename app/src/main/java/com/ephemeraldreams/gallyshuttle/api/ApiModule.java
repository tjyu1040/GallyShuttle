/*
 *  Copyright (C) 2014 Timothy Yu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.api;

import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

/**
 * A module for api-specific dependencies.
 */
@Module
public class ApiModule {

    private static final String API_URL = "https://spreadsheets.google.com/feeds/list/1P5VNOEitcInyNUUGCN7FNcSzvevpeZ530MP76uu5qB4/";

    /**
     * Provide a REST client.
     *
     * @param okHttpClient Client to use.
     * @return HTTP client instance.
     */
    @Provides
    @ApplicationScope
    Client provideClient(OkHttpClient okHttpClient) {
        return new OkClient(okHttpClient);
    }

    /**
     * Provide a REST adapter.
     *
     * @param client REST client to use.
     * @return REST adapter instance.
     */
    @Provides
    @ApplicationScope
    public RestAdapter provideRestAdapter(Client client) {
        return new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(client)
                .build();
    }

    /**
     * Provide a REST api service.
     *
     * @param restAdapter REST adapter to build the service.
     * @return REST api service instance.
     */
    @Provides
    @ApplicationScope
    public ShuttleApiService provideShuttleApiService(RestAdapter restAdapter) {
        return restAdapter.create(ShuttleApiService.class);
    }
}
