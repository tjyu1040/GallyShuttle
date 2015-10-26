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
import android.support.customtabs.CustomTabsIntent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AboutActivityTest {

    private static final Intent CUSTOM_TABS_INTENT = new CustomTabsIntent.Builder().build().intent;

    private static Resources resources;

    @Rule
    public IntentsTestRule<AboutActivity> activityTestRule = new IntentsTestRule<>(AboutActivity.class);

    @BeforeClass
    public static void initialize() {
        resources = GallyShuttleApplication.getComponent().getResources();
    }

    @Test
    public void testRateApplicationAction() {
        onView(withId(R.id.rate_button)).perform(click());
        intended(hasAction(Intent.ACTION_VIEW));
    }

    @Test
    public void testSupportEmailAction() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(AboutActivity.EMAIL_URI);
        onView(withId(R.id.support_button)).perform(click());
        intended(hasAction(Intent.ACTION_CHOOSER));
        /* TODO: wait for espresso intent chooser test support.
        intended(allOf(
                        hasAction(Intent.ACTION_CHOOSER),
                        hasExtras(anyOf(
                                        hasEntry(Intent.EXTRA_INTENT, emailIntent),
                                        hasEntry(Intent.EXTRA_TITLE, resources.getString(R.string.send_to_chooser_title)))
                        ))
        );
        */
    }

    /*
    //TODO: wait for Custom Tabs test support.
    @Test
    public void testCreditsAction() {
        onView(withId(R.id.credits_button)).perform(click());
        intended(hasAction(CUSTOM_TABS_INTENT.getAction()));
    }

    @Test
    public void testOpenSourceAction() {
        onView(withId(R.id.open_source_button)).perform(click());
        intended(hasAction(CUSTOM_TABS_INTENT.getAction()));
    }

    @Test
    public void testTosAction() {
        onView(withId(R.id.tos_button)).perform(click());
        intended(hasAction(CUSTOM_TABS_INTENT.getAction()));
    }

    @Test
    public void testPrivacyAction() {
        onView(withId(R.id.privacy_button)).perform(click());
        intended(hasAction(CUSTOM_TABS_INTENT.getAction()));
    }
    */
}
