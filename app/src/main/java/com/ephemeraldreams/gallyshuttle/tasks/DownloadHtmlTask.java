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
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.ephemeraldreams.gallyshuttle.events.OnDownloadScheduleEvent;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;

import java.io.IOException;

import timber.log.Timber;

/**
 * AsyncTask to download HTML data from over the Internet.
 */
public class DownloadHtmlTask extends AsyncTask<String, Void, String> {

    private ProgressDialog mProgressDialog;
    private Bus mBus;
    private String scheduleName;

    public DownloadHtmlTask(Activity activity, Bus bus) {
        mProgressDialog = new ProgressDialog(activity);
        mBus = bus;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Timber.d("Download starting...");
        if (params.length < 2) {
            Timber.e("No URL and schedule name defined.");
            return null;
        }

        String url = params[0];
        scheduleName = params[1];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Timber.e("Error downloading data.", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String html) {
        mProgressDialog.dismiss();
        mBus.post(new OnDownloadScheduleEvent(html, scheduleName));
        Timber.d("Download complete.");
    }
}