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

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.ui.MainActivity;
import com.google.android.gms.appinvite.AppInviteReferral;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationDrawerIsHidden() {
        onView(withId(R.id.navigation_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void openNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.navigation_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openAndCloseNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(isRoot()).perform(swipeLeft());
        onView(withId(R.id.navigation_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(isRoot()).perform(pressBack());
        onView(withId(R.id.navigation_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void navigateToHomeFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_home)).perform(click()).check(matches(isEnabled()));
        onView(withId(R.id.tab_layout)).check(matches(not(isDisplayed())));
    }

    @Test
    public void navigateToContinuousFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_continuous)).perform(click()).check(matches(isEnabled()));
        checkScheduleFragmentIsDisplayed();
    }

    @Test
    public void navigateToLateNightFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_late_night)).perform(click()).check(matches(isEnabled()));
        checkScheduleFragmentIsDisplayed();
    }

    @Test
    public void navigateToWeekendFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_weekend)).perform(click()).check(matches(isEnabled()));
        checkScheduleFragmentIsDisplayed();
    }

    @Test
    public void navigateToAlternateContinuousFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_alt_continuous)).perform(click()).check(matches(isEnabled()));
        checkScheduleFragmentIsDisplayed();
    }

    @Test
    public void navigateToModifiedFragment() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.nav_modified)).perform(click()).check(matches(isEnabled()));
        checkScheduleFragmentIsDisplayed();
    }

    private void checkScheduleFragmentIsDisplayed(){
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.times_recycler_view), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void sendInvitationIntent(){
        Intent invitationIntent = new Intent();
        String invitationId = "12345";
        String deepLink = "http://example.com/12345";
        AppInviteReferral.addReferralDataToIntent(invitationId, deepLink, invitationIntent);
        activityTestRule.launchActivity(invitationIntent);
        onView(withText(R.string.invite_dialog_title)).check(matches(isDisplayed()));
    }
}
