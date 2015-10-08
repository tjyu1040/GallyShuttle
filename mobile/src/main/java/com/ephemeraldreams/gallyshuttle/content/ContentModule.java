/*
 * Copyright 2014 Timothy Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.content;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.ephemeraldreams.gallyshuttle.ApplicationModule;
import com.ephemeraldreams.gallyshuttle.annotations.scopes.ApplicationScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * A module for content-specific dependencies.
 */
@Module(includes = ApplicationModule.class)
public class ContentModule {

    @Provides
    @ApplicationScope
    Resources resources(Application application) {
        return application.getResources();
    }

    @Provides
    @ApplicationScope
    SharedPreferences sharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @ApplicationScope
    Gson gson() {
        return new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
    }

    @Provides
    @ApplicationScope
    CacheManager cacheManager(Application application, Gson gson) {
        try {
            return new CacheManager(application, gson);
        } catch (IOException e) {
            Timber.e(e, "Null cache manager.");
            return null;
        }
    }
}
