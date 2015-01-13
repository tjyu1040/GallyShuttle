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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Fragment to handle user's settings for the application.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String KEY_PREF_REMINDER_LENGTH = "pref_reminder_length";
    public final static String KEY_PREF_RINGTONE = "pref_ringtone";
    public final static String KEY_PREF_VIBRATE = "pref_vibrate";
    public final static String KEY_PREF_REMINDER_SET = "pref_reminder_set";
    public final static String KEY_PREF_REMINDER_TIME_MESSAGE = "pref_reminder_time_message";
    public final static String KEY_PREF_CLEAR_CACHE = "pref_clear_cache";
    public final static String KEY_PREF_SHARE = "pref_share";

    @Inject SharedPreferences mSharedPreferences;
    @Inject Context mContext;
    private CharSequence mTitle;

    private ListPreference mReminderPreference;
    private RingtonePreference mRingtonePreference;
    private CheckBoxPreference mVibratePreference;
    private Preference mClearCachePreference;
    private Preference mSharePreference;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        setRetainInstance(true);

        getActivity().setTitle("Settings");
        addPreferencesFromResource(R.xml.preferences);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removePreference(mRingtonePreference);
            preferenceScreen.removePreference(mVibratePreference);
        } else {
            updateRingtoneSummary();
            updateVibrateSummary();
        }

        updateReminderSummary();
        setCacheClearPreference();
        setSharePreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mTitle = ((BaseActivity) getActivity()).getSupportActionBar().getTitle();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings_fragment_title);
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Workaround for ringtone summary not updating bug.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateRingtoneSummary();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_REMINDER_LENGTH)) {
            updateReminderSummary();
        }
        if (key.equals(KEY_PREF_RINGTONE)) {
            updateRingtoneSummary();
        }
        if (key.equals(KEY_PREF_VIBRATE)) {
            updateVibrateSummary();
        }
        if (key.equals(KEY_PREF_CLEAR_CACHE)) {
            updateCacheSummary();
        }
    }

    /**
     * Update reminder length in minutes.
     */
    private void updateReminderSummary() {
        mReminderPreference = (ListPreference) findPreference(KEY_PREF_REMINDER_LENGTH);
        mReminderPreference.setSummary(mReminderPreference.getEntry());
    }

    /**
     * Update ringtone name.
     */
    private void updateRingtoneSummary() {
        String strRingtonePreference = mSharedPreferences.getString(KEY_PREF_RINGTONE, "content://settings/system/alarm_alert");
        String ringtoneName;
        if (TextUtils.isEmpty(strRingtonePreference)) {
            ringtoneName = "Silent";
        } else {
            Timber.d(strRingtonePreference);
            Uri ringtoneUri = Uri.parse(strRingtonePreference);
            Ringtone ringtone = RingtoneManager.getRingtone(mContext, ringtoneUri);
            ringtoneName = ringtone.getTitle(mContext);
        }
        mRingtonePreference = (RingtonePreference) findPreference(KEY_PREF_RINGTONE);
        mRingtonePreference.setSummary(ringtoneName);
    }

    /**
     * Update enabled or disabled vibration.
     */
    private void updateVibrateSummary() {
        mVibratePreference = (CheckBoxPreference) findPreference(KEY_PREF_VIBRATE);
        if (mVibratePreference.isChecked()) {
            mVibratePreference.setSummary(getResources().getString(R.string.pref_vibrate_summary_enabled));
        } else {
            mVibratePreference.setSummary(getResources().getString(R.string.pref_vibrate_summary_disabled));
        }
    }

    /**
     * Update cache file count and size.
     */
    private void updateCacheSummary() {
        long directorySize = 0;
        File[] files = getActivity().getCacheDir().listFiles();
        for (File file : files) {
            directorySize += file.length();
        }
        String cacheSummary = files.length + " files (" + (directorySize / 1000) + " KB)";

        mClearCachePreference = findPreference(KEY_PREF_CLEAR_CACHE);
        mClearCachePreference.setSummary(cacheSummary);
    }

    /**
     * Set up click listener for clearing cache.
     */
    private void setCacheClearPreference() {
        mClearCachePreference = findPreference(KEY_PREF_CLEAR_CACHE);
        mClearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Clear Cache")
                        .setMessage("Clear cache?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File[] files = getActivity().getCacheDir().listFiles();
                                for (File file : files) {
                                    if (file.delete()) {
                                        Timber.d("Cache file deletion successful.");
                                    } else {
                                        Timber.d("Cache file deletion failed.");
                                    }
                                }
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
        mSharePreference = findPreference(KEY_PREF_SHARE);
        mSharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Resources resources = getResources();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.share_subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.share_message));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share)));
                return true;
            }
        });
    }
}