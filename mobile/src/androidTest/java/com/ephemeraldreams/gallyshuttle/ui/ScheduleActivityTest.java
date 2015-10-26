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

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.action.TimesRecyclerViewActions;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ephemeraldreams.gallyshuttle.action.TimesRecyclerViewActions.clickAlarmButton;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {

    private static Resources resources;

    @Rule
    public ActivityTestRule<ScheduleActivity> activityActivityTestRule = new ActivityTestRule<>(ScheduleActivity.class, true, false);

    @BeforeClass
    public static void initialize() {
        resources = GallyShuttleApplication.getComponent().getResources();
    }

    @Test
    public void testLaunchContinuous(){
        launchScheduleIntent(R.string.path_continuous);
        testCorrectScheduleTitleIsDisplayed(R.string.nav_continuous);
    }

    @Test
    public void testLaunchLateNight(){
        launchScheduleIntent(R.string.path_late_night);
        testCorrectScheduleTitleIsDisplayed(R.string.nav_late_night);
    }

    @Test
    public void testLaunchWeekend(){
        launchScheduleIntent(R.string.path_weekend);
        testCorrectScheduleTitleIsDisplayed(R.string.nav_weekend);
    }

    @Test
    public void testLaunchAlternateContinuous(){
        launchScheduleIntent(R.string.path_alt_continuous);
        testCorrectScheduleTitleIsDisplayed(R.string.nav_alt_continuous);
    }

    @Test
    public void testLaunchModified(){
        launchScheduleIntent(R.string.path_modified);
        testCorrectScheduleTitleIsDisplayed(R.string.nav_modified);
    }

    @Test
    public void testAlarmButton(){
        launchScheduleIntent(R.string.path_continuous);
        onView(allOf(withId(R.id.times_recycler_view), isDisplayed()))
                .perform(actionOnItemAtPosition(0, clickAlarmButton()));
        /*
        onView(withText("CLOSE")).check(matches(isFocusable()))
                .check(matches(isClickable()))
                .check(matches(isEnabled()));
                */
    }

    private void launchScheduleIntent(@StringRes int schedulePathId){
        Intent intent = new Intent();
        intent.putExtra(ScheduleActivity.PATH_EXTRA, schedulePathId);
        activityActivityTestRule.launchActivity(intent);
    }

    private void testCorrectScheduleTitleIsDisplayed(@StringRes int scheduleNameId) {
        String scheduleName = resources.getString(scheduleNameId);
        String formattedTitle = resources.getString(R.string.schedule_name_fmt, scheduleName);
        onView(withText(formattedTitle)).check(matches(isDisplayed()));
    }
}
