/*
 *  Copyright (C) 2014 Timothy Yu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle;

import android.test.ActivityInstrumentationTestCase2;

import com.ephemeraldreams.gallyshuttle.ui.ScheduleActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created on 4/8/2015.
 */
public class ScheduleActivityTest extends ActivityInstrumentationTestCase2<ScheduleActivity> {

    public ScheduleActivityTest() {
        super(ScheduleActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testScrollRight() {
        onView(withId(R.id.schedule_view_pager)).perform(swipeRight());
    }
}
