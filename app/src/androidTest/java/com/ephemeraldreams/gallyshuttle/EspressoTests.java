package com.ephemeraldreams.gallyshuttle;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.ephemeraldreams.gallyshuttle.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Still learning to use Espresso, but there still isn't much documentation due to it being a young
 * library, thus the reason for almost no tests here in this class.
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@LargeTest
public class EspressoTests extends ActivityInstrumentationTestCase2<MainActivity> {

    public EspressoTests(){
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    /**
     * Simple test to confirm schedule has been downloaded and displayed.
     */
    public void testScheduleFragment(){
        onView(withId(R.id.continuousButton)).perform(click());
        onView(withId(R.id.schedulePagerTabsStrip)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleViewpager)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.lateNightButton)).perform(click());
        onView(withId(R.id.schedulePagerTabsStrip)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleViewpager)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.weekendButton)).perform(click());
        onView(withId(R.id.schedulePagerTabsStrip)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleViewpager)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.modifiedButton)).perform(click());
        onView(withId(R.id.schedulePagerTabsStrip)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleViewpager)).check(matches(isDisplayed()));
    }
}