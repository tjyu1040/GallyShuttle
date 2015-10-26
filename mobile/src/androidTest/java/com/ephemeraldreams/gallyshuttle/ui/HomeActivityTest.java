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
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.ApplicationComponent;
import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;
import com.google.android.gms.appinvite.AppInviteReferral;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    private static ApplicationComponent component;

    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void initialize() {
        component = GallyShuttleApplication.getComponent();
    }

    @Before
    public void setUp() throws Exception {
        component.getSharedPreferences().edit().clear().apply();
    }

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
    public void testAppInvite() {
        Intent deepLinkIntent = new Intent();
        String invitationId = "12345";
        String deepLink = component.getResources().getString(R.string.invitation_deep_link);
        AppInviteReferral.addReferralDataToIntent(invitationId, deepLink, deepLinkIntent);

        activityTestRule.launchActivity(deepLinkIntent);
        onView(withText(R.string.invite_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.invite_dialog_message)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRateDialogIsDisplayed() {
        launchHomeActivity(HomeActivity.APP_OPENED_COUNT_FLAG);
        onView(withText(R.string.rate_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.rate_dialog_message)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRateDialogIsNotDisplayedAfterRateButtonIsClicked() {
        launchHomeActivity(HomeActivity.APP_OPENED_COUNT_FLAG);
        onView(withId(android.R.id.button1)).perform(click());

        launchHomeActivity(HomeActivity.APP_OPENED_COUNT_FLAG);
        onView(withText(R.string.rate_dialog_title)).check(doesNotExist());
        onView(withText(R.string.rate_dialog_message)).check(doesNotExist());
    }

    @Test
    public void checkRateDialogIsDisplayedAfterRateLaterButtonIsClicked() {
        launchHomeActivity(HomeActivity.APP_OPENED_COUNT_FLAG);
        onView(withId(android.R.id.button2)).perform(click());

        launchHomeActivity(HomeActivity.APP_OPENED_COUNT_FLAG);
        onView(withText(R.string.rate_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.rate_dialog_message)).check(matches(isDisplayed()));
    }

    private void launchHomeActivity(int launchCount) {
        for (int i = 1; i <= launchCount; i++) {
            activityTestRule.launchActivity(new Intent());
        }
    }
}
