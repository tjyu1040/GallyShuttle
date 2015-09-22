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

package com.ephemeraldreams.gallyshuttle.ui.services;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import com.ephemeraldreams.gallyshuttle.ui.util.CustomTabsUtils;

import java.util.List;

import timber.log.Timber;

/**
 * Service to warm up Custom Tabs service.
 */
public class CustomTabsWarmUpService {

    private CustomTabsSession session;
    private CustomTabsClient client;
    private CustomTabsServiceConnection connection;
    private CustomTabsConnectionCallback connectionCallback;

    /**
     * Binds the activity to the Custom Tabs service.
     *
     * @param activity Activity to be connected to Custom Tabs service.
     */
    public void bindCustomTabsService(Activity activity) {
        if (client == null) {
            String packageName = CustomTabsUtils.getPackageNameToUse(activity);
            if (packageName != null) {
                connection = new CustomTabsServiceConnection() {
                    @Override
                    public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                        client = customTabsClient;
                        if (client.warmup(0)) {
                            Timber.d("Client warmed up.");
                        } else {
                            Timber.d("Client not warmed up.");
                        }
                        getSession();
                        if (connectionCallback != null) {
                            connectionCallback.onCustomTabsConnected();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        client = null;
                        if (connectionCallback != null) {
                            connectionCallback.onCustomTabsDisconnected();
                        }
                    }
                };
                CustomTabsClient.bindCustomTabsService(activity, packageName, connection);
            }
            Timber.d("Bound Custom Tabs service.");
        }
    }

    /**
     * Unbinds the activity from the Custom Tabs service.
     *
     * @param activity Activity connected to the Custom Tabs service.
     */
    public void unbindCustomTabsService(Activity activity) {
        if (connection != null) {
            activity.unbindService(connection);
            client = null;
            session = null;
            Timber.d("Unbound Custom Tabs service.");
        }
    }

    /**
     * Creates or retrieves an existing {@link CustomTabsSession}.
     *
     * @return A {@link CustomTabsSession}.
     */
    public CustomTabsSession getSession() {
        if (client == null) {
            session = null;
        } else if (session == null) {
            session = client.newSession(null);
        }
        return session;
    }

    /**
     * Register a callback to be called when connected or disconnected from the Custom Tabs service.
     */
    public void setConnectionCallback(CustomTabsConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    /**
     * @return True if call to mayLaunchUrl was accepted.
     * @see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}.
     */
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (client == null) {
            Timber.d("Null client.");
            return false;
        }
        CustomTabsSession session = getSession();
        if (session == null) {
            Timber.d("Null session.");
            return false;
        }
        return session.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }

    /**
     * A callback for when the Custom Tabs service is connected or disconnected. Use this callback
     * to handle UI changes when the service is connected or disconnected.
     */
    public interface CustomTabsConnectionCallback {

        /**
         * Called when the service is connected.
         */
        void onCustomTabsConnected();

        /**
         * Called when the service is disconnected.
         */
        void onCustomTabsDisconnected();
    }
}
