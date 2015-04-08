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

package com.ephemeraldreams.gallyshuttle.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.RingtoneChoice;
import com.ephemeraldreams.gallyshuttle.data.CacheManager;
import com.ephemeraldreams.gallyshuttle.data.preferences.StringPreference;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Activity to handle user's settings for the application. Holds a {@link SettingsFragment} instance.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * Fragment to handle user's settings for the application.
     */
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference reminderPreference;
        private RingtonePreference ringtonePreference;
        private CheckBoxPreference vibratePreference;
        private Preference clearCachePreference;
        private Preference sharePreference;

        @Inject Activity activity;
        @Inject CacheManager cacheManager;
        @Inject SharedPreferences sharedPreferences;
        @Inject @RingtoneChoice StringPreference ringtoneStringPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ((BaseActivity) getActivity()).inject(this);

            setRetainInstance(true);
            addPreferencesFromResource(R.xml.preferences);
            setPreferences();
        }

        private void setPreferences() {
            reminderPreference = (ListPreference) findPreference(getString(R.string.pref_key_reminder_length));
            ringtonePreference = (RingtonePreference) findPreference(getString(R.string.pref_key_ringtone));
            vibratePreference = (CheckBoxPreference) findPreference(getString(R.string.pref_key_vibrate));
            clearCachePreference = findPreference(getString(R.string.pref_key_clear_cache));
            sharePreference = findPreference(getString(R.string.pref_key_share));

            updateReminderSummary();
            setSharePreference();
            setCacheClearPreference();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                ((PreferenceGroup) findPreference(getString(R.string.pref_key_alarms))).removePreference(ringtonePreference);
                ((PreferenceGroup) findPreference(getString(R.string.pref_key_alarms))).removePreference(vibratePreference);
            } else {
                updateRingtoneSummary();
                updateVibrateSummary();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateRingtoneSummary();
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_key_reminder_length))) {
                updateReminderSummary();
            } else if (key.equals(getString(R.string.pref_key_ringtone))) {
                updateRingtoneSummary();
            } else if (key.equals(getString(R.string.pref_key_vibrate))) {
                updateVibrateSummary();
            } else if (key.equals(getString(R.string.pref_key_clear_cache))) {
                updateCacheSummary();
            }
        }

        /**
         * Update reminder length in minutes.
         */
        private void updateReminderSummary() {
            reminderPreference.setSummary(reminderPreference.getEntry());
        }

        /**
         * Update ringtone name.
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private void updateRingtoneSummary() {
            String strRingtonePreference = ringtoneStringPreference.get();
            String ringtoneName;
            if (TextUtils.isEmpty(strRingtonePreference)) {
                ringtoneName = getString(R.string.ringtone_silent);
            } else {
                Timber.d(strRingtonePreference);
                Uri ringtoneUri = Uri.parse(strRingtonePreference);
                Ringtone ringtone = RingtoneManager.getRingtone(activity, ringtoneUri);
                ringtoneName = ringtone.getTitle(activity);
            }
            ringtonePreference.setSummary(ringtoneName);
        }

        /**
         * Update enabled or disabled vibration.
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private void updateVibrateSummary() {
            if (vibratePreference.isChecked()) {
                vibratePreference.setSummary(getString(R.string.pref_vibrate_summary_enabled));
            } else {
                vibratePreference.setSummary(getString(R.string.pref_vibrate_summary_disabled));
            }
        }

        /**
         * Update cache file count and size.
         */
        private void updateCacheSummary() {
            String cacheSummary = cacheManager.getCacheFilesLength() +
                    " files (" + cacheManager.getCacheSize() + " KB)";
            clearCachePreference.setSummary(cacheSummary);
        }

        /**
         * Set up click listener for clearing cache.
         */
        private void setCacheClearPreference() {
            clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.dialog_title_clear_cache))
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
                            });
                    AlertDialog clearCacheDialog = builder.create();
                    clearCacheDialog.show();
                    return true;
                }
            });
            updateCacheSummary();
        }

        /**
         * Set up click listener for sharing.
         */
        private void setSharePreference() {
            sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.pref_title_share)));
                    return true;
                }
            });
        }
    }
}
