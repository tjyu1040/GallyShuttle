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

import android.support.test.rule.ActivityTestRule;

import com.ephemeraldreams.gallyshuttle.action.TimesRecyclerViewActions;
import com.ephemeraldreams.gallyshuttle.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

public class ScheduleFragmentEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickAlarmButton() {
        navigateToContinuousFragment();
        checkAlarmSnackBarIsDisplayed();

        navigateToLateNightFragment();
        checkAlarmSnackBarIsDisplayed();

        navigateToWeekendFragment();
        checkAlarmSnackBarIsDisplayed();

        navigateToAlternateContinuousFragment();
        checkAlarmSnackBarIsDisplayed();

        navigateToModifiedFragment();
        checkAlarmSnackBarIsDisplayed();
    }

    private void navigateToContinuousFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_continuous)).perform(click()).check(matches(isEnabled()));
    }

    private void navigateToLateNightFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_late_night)).perform(click()).check(matches(isEnabled()));
    }

    private void navigateToWeekendFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_weekend)).perform(click()).check(matches(isEnabled()));
    }

    private void navigateToAlternateContinuousFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_alt_continuous)).perform(click()).check(matches(isEnabled()));
    }

    private void navigateToModifiedFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_modified)).perform(click()).check(matches(isEnabled()));
    }

    private void checkAlarmSnackBarIsDisplayed() {
        onView(allOf(withId(R.id.times_recycler_view), isDisplayed())).perform(actionOnItemAtPosition(0, TimesRecyclerViewActions.clickAlarmButton()));
        onView(withText("CLOSE")).check(matches(isFocusable()))
                .check(matches(isClickable()))
                .check(matches(isEnabled()));
    }
}
