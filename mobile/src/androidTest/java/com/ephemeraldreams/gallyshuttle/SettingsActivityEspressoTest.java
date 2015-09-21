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

package com.ephemeraldreams.gallyshuttle;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.contrib.AccessibilityChecks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.content.preferences.BooleanPreference;
import com.ephemeraldreams.gallyshuttle.content.preferences.StringPreference;
import com.ephemeraldreams.gallyshuttle.ui.ActivityComponent;
import com.ephemeraldreams.gallyshuttle.ui.SettingsActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO: update tests.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityEspressoTest {

    private static ApplicationComponent component;

    @Rule
    public ActivityTestRule<SettingsActivity> activityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Before
    public void setUp() {
        component.getSharedPreferences().edit().clear().apply();
    }

    @BeforeClass
    public static void enableAccessibilityChecks() {
        component = GallyShuttleApplication.getComponent();
        AccessibilityChecks.enable().setRunChecksFromRootView(true);
    }

    @Test
    public void testSettingsFragmentInflated() {
        onView(withId(R.id.settings_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void toggleAlarmVibratePreference() {
        BooleanPreference alarmVibrationPreference = component.getAlarmVibrationBooleanPreference();
        assertThat(alarmVibrationPreference.get()).isTrue();
        onView(withText(R.string.title_alarm_vibrate_preference)).perform(click());
        assertThat(alarmVibrationPreference.get()).isFalse();
    }

    @Test
    public void toggleNotificationVibratePreference() {
        BooleanPreference notificationVibrationPreference = component.getNotificationVibrationBooleanPreference();
        assertThat(notificationVibrationPreference.get()).isTrue();
        onView(withText(R.string.title_notification_vibrate_preference)).perform(click());
        assertThat(notificationVibrationPreference.get()).isFalse();
    }
}
