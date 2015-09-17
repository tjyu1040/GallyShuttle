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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.AlarmRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.annotations.qualifiers.NotificationRingtoneChoice;
import com.ephemeraldreams.gallyshuttle.content.CacheManager;
import com.ephemeraldreams.gallyshuttle.content.preferences.StringPreference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity to handle user's settings for the application. Holds a {@link SettingsFragment} instance.
 */
public class SettingsActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getComponent().inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fragment to handle user's settings for the application.
     */
    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        private static final int REQUEST_CODE_RINGTONE = 0;
        private static final int REQUEST_CODE_ALARM_RINGTONE = 1;
        private static final int REQUEST_CODE_NOTIFICATION_RINGTONE = 2;

        private ListPreference alarmReminderLengthPreference;
        private Preference alarmRingtonePreference;
        private CheckBoxPreference alarmVibrateCheckBoxPreference;
        private Preference notificationRingtonePreference;
        private CheckBoxPreference notificationVibrateCheckBoxPreference;
        private Preference cachePreference;
        private Preference sharePreference;

        @Inject CacheManager cacheManager;
        @Inject SharedPreferences sharedPreferences;
        @Inject @AlarmRingtoneChoice StringPreference alarmRingtoneChoiceStringPreference;
        @Inject @NotificationRingtoneChoice StringPreference notificationRingtoneChoiceStringPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((BaseActivity) getActivity()).getComponent().inject(this);
            addPreferencesFromResource(R.xml.preferences);

            setAlarmReminderLengthPreference();
            setAlarmRingtonePreference();
            setAlarmVibrateCheckBoxPreference();
            setNotificationRingtonePreference();
            setNotificationVibrateCheckBoxPreference();
            setCachePreference();
            setSharePreference();
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
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.key_alarm_reminder_length_preference))) {
                updateAlarmReminderLengthSummary();
            }
        }

        /**
         * Set up alarm reminder length preference.
         */
        private void setAlarmReminderLengthPreference() {
            alarmReminderLengthPreference = (ListPreference) findPreference(getString(R.string.key_alarm_reminder_length_preference));
            updateAlarmReminderLengthSummary();
        }

        /**
         * Update reminder length preference summary.
         */
        private void updateAlarmReminderLengthSummary() {
            alarmReminderLengthPreference.setSummary(
                    String.format(
                            getString(R.string.summary_set_alarm_preference),
                            alarmReminderLengthPreference.getEntry()
                    )
            );
        }

        /**
         * Set up alarm ringtone preference.
         */
        private void setAlarmRingtonePreference() {
            alarmRingtonePreference = findPreference(
                    getString(R.string.key_alarm_ringtone_preference));
            updateRingtoneSummary(alarmRingtonePreference, alarmRingtoneChoiceStringPreference);
        }

        /**
         * Set up notification ringtone preference.
         */
        private void setNotificationRingtonePreference() {
            notificationRingtonePreference = findPreference(
                    getString(R.string.key_notification_ringtone_preference)
            );
            updateRingtoneSummary(notificationRingtonePreference, notificationRingtoneChoiceStringPreference);
        }

        /**
         * Update ringtone name summary.
         *
         * @param ringtonePreference       Preference to update summary.
         * @param ringtoneChoicePreference String preference to retrieve current ringtone choice.
         */
        private void updateRingtoneSummary(Preference ringtonePreference, StringPreference ringtoneChoicePreference) {
            String ringtoneUriString = ringtoneChoicePreference.get("");
            if (ringtoneUriString.length() == 0) {
                ringtonePreference.setSummary("Silent");
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(ringtoneUriString));
                ringtonePreference.setSummary(ringtone.getTitle(getActivity()));
            }
        }

        /**
         * Overridden to handle ringtone preferences.
         */
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            if (preference.getKey().equals(getString(R.string.key_alarm_ringtone_preference))) {
                startRingtonePickerActivity(R.string.key_alarm_ringtone_preference, alarmRingtoneChoiceStringPreference);
                return true;
            } else if (preference.getKey().equals(getString(R.string.key_notification_ringtone_preference))) {
                startRingtonePickerActivity(R.string.key_notification_ringtone_preference, notificationRingtoneChoiceStringPreference);
                return true;
            } else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }

        /**
         * Start a ringtone activity to allow user to choose ringtone.
         *
         * @param keyPrefStringRes         Preference key string resource id.
         * @param ringtoneChoicePreference String preference to retrieve current ringtone choice.
         */
        private void startRingtonePickerActivity(@StringRes int keyPrefStringRes, StringPreference ringtoneChoicePreference) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

            int requestCode;
            Uri defaultUri;
            switch (keyPrefStringRes) {
                case R.string.key_alarm_ringtone_preference:
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                    requestCode = REQUEST_CODE_ALARM_RINGTONE;
                    defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                    break;
                case R.string.key_notification_ringtone_preference:
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                    requestCode = REQUEST_CODE_NOTIFICATION_RINGTONE;
                    defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                    break;
                default:
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                    requestCode = REQUEST_CODE_RINGTONE;
                    defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                    break;
            }
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, defaultUri);

            String currentRingtone = ringtoneChoicePreference.get(null);
            if (currentRingtone != null) {
                if (currentRingtone.length() == 0) {
                    // Select "Silent" ringtone
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(currentRingtone));
                }
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, defaultUri);
            }
            startActivityForResult(intent, requestCode);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data != null) {
                switch (requestCode) {
                    case REQUEST_CODE_ALARM_RINGTONE:
                        saveRingtonePreference(alarmRingtonePreference, alarmRingtoneChoiceStringPreference, data);
                        break;
                    case REQUEST_CODE_NOTIFICATION_RINGTONE:
                        saveRingtonePreference(notificationRingtonePreference, notificationRingtoneChoiceStringPreference, data);
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        /**
         * Store ringtone choice preference.
         *
         * @param ringtonePreference       Preference to update ringtone summary.
         * @param ringtoneChoicePreference String preference to store ringtone choice.
         * @param data                     Intent received from activity result.
         */
        private void saveRingtonePreference(Preference ringtonePreference, StringPreference ringtoneChoicePreference, Intent data) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                ringtoneChoicePreference.set(ringtone.toString());
            } else {
                // "Silent" selected.
                ringtoneChoicePreference.set("");
            }
            updateRingtoneSummary(ringtonePreference, ringtoneChoicePreference);
        }

        /**
         * Set up alarm vibrate preference.
         */
        private void setAlarmVibrateCheckBoxPreference() {
            alarmVibrateCheckBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.key_alarm_vibrate_preference));
            toggleVibrateSummary(alarmVibrateCheckBoxPreference);
        }

        /**
         * Set up notification vibrate preference.
         */
        private void setNotificationVibrateCheckBoxPreference() {
            notificationVibrateCheckBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.key_notification_vibrate_preference));
            toggleVibrateSummary(notificationVibrateCheckBoxPreference);
        }

        /**
         * Toggle vibration preference summary.
         *
         * @param vibratePreference Checkbox vibrate preference to update summary.
         */
        private void toggleVibrateSummary(CheckBoxPreference vibratePreference) {
            if (vibratePreference.isChecked()) {
                vibratePreference.setSummary(getString(R.string.summary_vibrate_enabled_preference));
            } else {
                vibratePreference.setSummary(getString(R.string.summary_vibrate_disabled_preference));
            }
        }

        /**
         * Set up click listener for cache clear preference.
         */
        private void setCachePreference() {
            cachePreference = findPreference(getString(R.string.key_clear_cache_preference));
            cachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.title_clear_cache_dialog))
                            .setMessage(getString(R.string.message_clear_cache_dialog))
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
                    builder.create().show();
                    return true;
                }
            });
            updateCacheSummary();
        }

        /**
         * Update cache file count and size.
         */
        private void updateCacheSummary() {
            String cacheSummary = cacheManager.getCachedFileCount() + " files (" +
                    cacheManager.getCacheSize() + " bytes)";
            cachePreference.setSummary(cacheSummary);
        }

        /**
         * Set up click listener for share preference.
         */
        private void setSharePreference() {
            sharePreference = findPreference(getString(R.string.key_share_preference));
            sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share_preference)));
                    return true;
                }
            });
        }
    }
}
