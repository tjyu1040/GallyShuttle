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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.util.GooglePlayServicesUtils;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

/**
 * Activity to handle user's settings for the application. Holds a {@link SettingsFragment} instance.
 */
public class SettingsActivity extends BaseActivity {

    private static final int INVITE_REQUEST_CODE = 0;

    /**
     * Launch a {@link SettingsActivity} instance.
     *
     * @param activity The activity opening the {@link SettingsActivity}.
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getComponent().inject(this);
    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        return R.id.nav_settings;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.nav_settings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == INVITE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Timber.d(getString(R.string.sent_invitations_fmt, ids.length));
                Snackbar.make(coordinatorLayout, getString(R.string.send_success), Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, getString(R.string.send_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public static class SettingsFragment extends BasePreferenceFragment {

        @Inject SharedPreferences sharedPreferences;
        @Inject CacheManager cacheManager;

        private ListPreference reminderListPreference;
        private RingtonePreference alarmRingtonePreference;
        private RingtonePreference notificationRingtonePreference;

        private Preference cachePreference;
        private Preference sharePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivityComponent().inject(this);
            addPreferencesFromResource(R.xml.preferences);

            reminderListPreference = (ListPreference) findPreference(getString(R.string.pref_key_alarm_reminder));
            bindOnPreferenceChange(reminderListPreference);

            alarmRingtonePreference = (RingtonePreference) findPreference(getString(R.string.pref_key_alarm_ringtone));
            bindOnPreferenceChange(alarmRingtonePreference);

            notificationRingtonePreference = (RingtonePreference) findPreference(getString(R.string.pref_key_notification_ringtone));
            bindOnPreferenceChange(notificationRingtonePreference);

            setCachePreference();
            setSharePreference();
        }

        private void bindOnPreferenceChange(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference, sharedPreferences.getString(preference.getKey(), ""));
        }

        private void setCachePreference() {
            cachePreference = findPreference(getString(R.string.pref_key_cache));
            cachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.dialog_title_clear_cache))
                            .setMessage(getString(R.string.dialog_message_clear_cache))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cacheManager.clearCache();
                                    updateCacheSummary();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                    return true;
                }
            });
            updateCacheSummary();
        }

        private void updateCacheSummary() {
            int cacheFileCount = cacheManager.getCachedFileCount();
            long cacheSize = cacheManager.getCacheSize() / 1000;
            String cacheSummary = cacheFileCount + " files (" + cacheSize + " KB)";
            cachePreference.setSummary(cacheSummary);
        }

        private void setSharePreference() {
            sharePreference = findPreference(getString(R.string.pref_key_share));
            sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (GooglePlayServicesUtils.isGooglePlayServicesAvailable(getActivity())){
                        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                                .setMessage(getString(R.string.invitation_message))
                                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                                .setCallToActionText(getString(R.string.invitation_cta))
                                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                                .build();
                        startActivityForResult(intent, SettingsActivity.INVITE_REQUEST_CODE);
                    } else {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.pref_title_share)));
                    }
                    return true;
                }
            });
        }
    }
}
