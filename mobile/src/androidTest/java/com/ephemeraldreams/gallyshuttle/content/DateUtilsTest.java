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

package com.ephemeraldreams.gallyshuttle.content;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import com.ephemeraldreams.gallyshuttle.ui.HomeActivity;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DateUtilsTest {

    @Rule
    public ActivityTestRule<HomeActivity> activityActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testParseToLocalDateTime() throws Exception {
        String time = "10:10 AM";
        LocalDateTime expectedLocalDateTime = new LocalTime(10, 10).toDateTimeToday().toLocalDateTime();
        LocalDateTime localDateTime = DateUtils.parseToLocalDateTime(time);
        assertThat(localDateTime).isEqualTo(expectedLocalDateTime);
    }

    @Test
    public void testFormatTime() throws Exception {
        LocalDateTime localDateTime = new LocalTime(10, 10).toDateTimeToday().toLocalDateTime();
        String expectedTimeFormat = "10:10 AM";
        String time = DateUtils.formatTime(localDateTime);
        assertThat(time).isEqualTo(expectedTimeFormat);
    }

    @Test
    public void testCalculateDuration() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusMinutes(1);
        long expectedMillis = future.toDate().getTime() - now.toDate().getTime();
        long millis = DateUtils.calculateDuration(now, future);
        assertThat(millis).isEqualTo(expectedMillis);
    }

    @Test
    public void testConvertMillisecondsToTime() throws Exception {
        String expectedTime = "1:30:00";
        String calculatedTime = DateUtils.convertMillisecondsToTime(5400000);
        assertThat(calculatedTime).isEqualTo(expectedTime);
    }
}
