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

package com.ephemeraldreams.gallyshuttle.data;

import android.app.Application;

import com.ephemeraldreams.gallyshuttle.data.models.Schedule;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Class to manage data cache.
 */
public class CacheManager {

    private Application application;
    private OkHttpClient okHttpClient;

    public CacheManager(Application application, OkHttpClient okHttpClient) {
        this.application = application;
        this.okHttpClient = okHttpClient;
    }

    /**
     * Create cached file for schedule.
     *
     * @param schedule Schedule to cache.
     */
    public void createScheduleCacheFile(Schedule schedule) {
        File file = new File(application.getCacheDir(), schedule.title.replace(' ', '_') + ".json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(Schedule.toJsonString(schedule));
            fileWriter.close();
            Timber.d("Cached file " + file.getCanonicalPath() + " successfully created.");
        } catch (IOException e) {
            Timber.e(e, "Error creating cache file " + file.getName() + ".");
        }
    }

    /**
     * Read schedule cache file.
     *
     * @param scheduleTitle Title of schedule to read.
     * @return Schedule POJO
     * @throws FileNotFoundException
     */
    @DebugLog
    public Schedule readScheduleCacheFile(String scheduleTitle) throws FileNotFoundException {
        File file = new File(application.getCacheDir(), scheduleTitle.replace(' ', '_') + ".json");
        if (scheduleCacheFileExists(file)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            return Schedule.fromJsonFile(bufferedReader);
        } else {
            return null;
        }
    }

    /**
     * Check whether file exists.
     *
     * @param file File to check.
     * @return true if file exists, otherwise false.
     */
    public boolean scheduleCacheFileExists(File file) {
        return file.exists() && !file.isDirectory();
    }

    /**
     * Get number of cached files.
     *
     * @return Number of cached files.
     */
    public int getCacheFilesLength() {
        return application.getCacheDir().listFiles().length;
    }

    /**
     * Get cache size.
     *
     * @return Cache size in kilobytes.
     */
    public long getCacheSize() {
        long directorySize = 0;
        File[] files = application.getCacheDir().listFiles();
        for (File file : files) {
            if (file.getName().equals("http")) {
                try {
                    directorySize += okHttpClient.getCache().getSize();
                } catch (IOException e) {
                    Timber.e(e, "Failed to retrieve OkHttp cache.");
                }
            } else {
                directorySize += file.length();
            }
        }
        return directorySize / 1000;
    }

    /**
     * Clear all cache.
     */
    public void clearCache() {
        File[] files = application.getCacheDir().listFiles();
        for (File file : files) {
            if (file.delete()) {
                Timber.d(file.getName() + " deletion successful.");
            } else {
                try {
                    Timber.d(file.getName() + " deletion unsuccessful.");
                    okHttpClient.getCache().evictAll();
                    Timber.d(file.getName() + " OkHttp cache deletion successful.");
                } catch (IOException e) {
                    Timber.d("OkHttp cache file deletion failed.");
                }
            }
        }
    }
}
