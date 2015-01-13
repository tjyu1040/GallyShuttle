/*
 * Copyright (C) 2014 Timothy Yu
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

package com.ephemeraldreams.gallyshuttle.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.ephemeraldreams.gallyshuttle.events.OnLoadHtmlFileEvent;
import com.squareup.otto.Bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

/**
 * AsyncTask to load HTML data from a raw resource.
 */
public class LoadHtmlFileTask extends AsyncTask<Void, Void, String> {

    private Activity mActivity;
    private int mId;
    private Bus mBus;

    public LoadHtmlFileTask(Activity activity, int id, Bus bus) {
        mActivity = activity;
        mId = id;
        mBus = bus;
    }

    @Override
    protected String doInBackground(Void... params) {
        Timber.d("Loading HTML file...");
        InputStream rawResource = mActivity.getResources().openRawResource(mId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rawResource));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            Timber.e("Error loading HTML file.", e);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String html) {
        super.onPostExecute(html);
        if (!isCancelled()) {
            mBus.post(new OnLoadHtmlFileEvent(html));
            Timber.d("Load complete.");
        }
    }
}