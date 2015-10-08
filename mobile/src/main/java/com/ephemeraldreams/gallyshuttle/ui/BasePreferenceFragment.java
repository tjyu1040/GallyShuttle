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

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.ephemeraldreams.gallyshuttle.R;

/**
 * Base preference fragment for dagger injection.
 */
public abstract class BasePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    /**
     * Retrieve current activity component.
     *
     * @return Current activity component to inject objects.
     */
    public ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getComponent();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else if (preference instanceof RingtonePreference) {
            if (TextUtils.isEmpty(value)) {
                preference.setSummary(R.string.pref_summary_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(value));
                if (ringtone != null) {
                    preference.setSummary(ringtone.getTitle(preference.getContext()));
                } else {
                    preference.setSummary(null);
                }
            }
        } else {
            preference.setSummary(value);
        }
        return true;
    }
}
