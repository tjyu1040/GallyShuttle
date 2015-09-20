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

package com.ephemeraldreams.gallyshuttle.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.ui.events.NetworkStateChangedEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Broadcast receiver to monitor network state.
 */
public class NetworkStateBroadCastReceiver extends BroadcastReceiver {

    @Inject Bus bus;
    @Inject ConnectivityManager connectivityManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        GallyShuttleApplication.getComponent().inject(this);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            bus.post(new NetworkStateChangedEvent(true));
            Timber.d("Connection established.");
        } else {
            bus.post(new NetworkStateChangedEvent(false));
            Timber.d("Connection disconnected.");
        }
    }
}
