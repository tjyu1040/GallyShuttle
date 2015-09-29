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

import com.ephemeraldreams.gallyshuttle.BuildConfig;
import com.ephemeraldreams.gallyshuttle.net.api.models.Schedule;
import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * Class to manage content cache.
 */
public class CacheManager {

    private static final int APP_VERSION = BuildConfig.VERSION_CODE;
    private static final int VALUE_COUNT = 1;
    private static final long MAX_SIZE = 1024 * 10;  // 10 MB

    private final Application application;
    private final Gson gson;

    private DiskLruCache diskLruCache;

    public CacheManager(Application application, Gson gson) throws IOException {
        this.application = application;
        this.gson = gson;
        this.diskLruCache = DiskLruCache.open(application.getCacheDir(), APP_VERSION, VALUE_COUNT, MAX_SIZE);
    }

    /**
     * Create schedule cache.
     *
     * @param schedule Schedule to cache.
     */
    public void cacheSchedule(Schedule schedule) {
        try {
            if (diskLruCache.isClosed()) {
                diskLruCache = DiskLruCache.open(application.getCacheDir(), APP_VERSION, VALUE_COUNT, MAX_SIZE);
            }
            DiskLruCache.Editor editor = diskLruCache.edit(schedule.path);
            OutputStream outputStream = editor.newOutputStream(0);
            outputStream.write(gson.toJson(schedule).getBytes());
            editor.commit();
            outputStream.close();
            Timber.d("Successfully cached " + schedule.path + " schedule.");
        } catch (IOException e) {
            Timber.e(e, "Failed to cache " + schedule.path + " schedule.");
        }
    }

    /**
     * Load schedule cache.
     *
     * @param path Path name of schedule to load.
     * @return Cached schedule. Null if no such cached schedule.
     * @throws IOException
     */
    public Schedule loadScheduleCache(String path) throws IOException {
        if (diskLruCache.isClosed()) {
            diskLruCache = DiskLruCache.open(application.getCacheDir(), APP_VERSION, VALUE_COUNT, MAX_SIZE);
        }
        DiskLruCache.Snapshot snapshot = diskLruCache.get(path);
        if (snapshot != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(snapshot.getInputStream(0));
            Schedule schedule = gson.fromJson(inputStreamReader, Schedule.class);
            snapshot.close();
            return schedule;
        } else {
            return null;
        }
    }

    /**
     * Get count of cached files.
     *
     * @return Count of cached files.
     */
    public int getCachedFileCount() {
        return diskLruCache.getDirectory().listFiles().length;
    }

    /**
     * Get cache size.
     *
     * @return Cache size in bytes.
     */
    public long getCacheSize() {
        long size = 0;
        File[] cacheDirectory = diskLruCache.getDirectory().listFiles();
        for (File file : cacheDirectory){
            size += file.length();
        }
        return size;
    }

    /**
     * Clear all cache.
     */
    public void clearCache() {
        try {
            diskLruCache.delete();
            Timber.d("Cache deletion successful.");
        } catch (IOException e) {
            Timber.e(e, "Cache deletion failed.");
        }
    }
}
