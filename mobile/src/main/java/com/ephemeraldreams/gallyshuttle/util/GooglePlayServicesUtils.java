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

package com.ephemeraldreams.gallyshuttle.util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import timber.log.Timber;

/**
 * Google Play Services utility class.
 */
public class GooglePlayServicesUtils {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST_CODE = 9000;

    /**
     * Verify that Google Play services is installed and enabled on this device. If Google Play services
     * is not installed and can be resolvable by user, prompt user to install or update. Otherwise,
     * finish the activity.
     *
     * @param context Parent context to display error dialog or notification.
     * @return true if Google Play services is available, false otherwise.
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                if (context instanceof Activity) {
                    googleApiAvailability.getErrorDialog((Activity) context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST_CODE).show();
                } else {
                    googleApiAvailability.showErrorNotification(context, resultCode);
                }
            } else {
                Timber.i("This device is not supported.");
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
