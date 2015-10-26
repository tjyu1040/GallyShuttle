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

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.ApplicationComponent;
import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anything;

/**
 * TODO: update tests.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    private static final int ALARM_REMINDER_INDEX = 1;
    private static final int ALARM_RINGTONE_INDEX = 2;
    private static final int ALARM_VIBRATE_INDEX = 3;
    private static final int NOTIFICATION_RINGTONE_INDEX = 5;
    private static final int NOTIFICATION_VIBRATE_INDEX = 6;
    private static final int CACHE_INDEX = 8;
    private static final int SHARE_INDEX = 10;

    private static Resources resources;
    private static SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<SettingsActivity> activityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @BeforeClass
    public static void initialize() {
        ApplicationComponent component = GallyShuttleApplication.getComponent();
        resources = component.getResources();
        sharedPreferences = component.getSharedPreferences();
    }

    @Before
    public void setUp() {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testSettingsFragmentInflated() {
        onView(withId(R.id.settings_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void toggleAlarmVibratePreference() {
        boolean alarmVibrateEnabled = getBooleanPreference(R.string.pref_key_alarm_vibrate, true);
        assertThat(alarmVibrateEnabled).isTrue();
        clickPreference(ALARM_VIBRATE_INDEX);
        alarmVibrateEnabled = getBooleanPreference(R.string.pref_key_alarm_vibrate, true);
        assertThat(alarmVibrateEnabled).isFalse();
    }

    @Test
    public void toggleNotificationVibratePreference() {
        boolean notificationVibrateEnabled = getBooleanPreference(R.string.pref_key_notification_vibrate, true);
        assertThat(notificationVibrateEnabled).isTrue();
        clickPreference(NOTIFICATION_VIBRATE_INDEX);
        notificationVibrateEnabled = getBooleanPreference(R.string.pref_key_notification_vibrate, true);
        assertThat(notificationVibrateEnabled).isFalse();
    }

    private void clickPreference(int index) {
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(index).perform(click());
    }

    private boolean getBooleanPreference(@StringRes int keyId, boolean defaultValue) {
        return sharedPreferences.getBoolean(resources.getString(keyId), defaultValue);
    }
}
