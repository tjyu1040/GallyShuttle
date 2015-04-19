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

package com.ephemeraldreams.gallyshuttle.util;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test JodaTime and DateUtils.
 */
public class DateUtilsTest {

    private LocalDateTime midnight, fiveAfterMidnight, fifteenAfterMidnight;

    @Before
    public void setUp() throws Exception {
        midnight = DateUtils.parseToLocalDateTime("12:00 AM");
        fiveAfterMidnight = DateUtils.parseToLocalDateTime("12:05 AM");
        fifteenAfterMidnight = DateUtils.parseToLocalDateTime("12:15 AM");
    }

    @Test
    public void testParseToLocalDateTime() throws Exception {
        System.out.println(midnight.toLocalTime().toString());
        System.out.println(fiveAfterMidnight.toLocalTime().toString());
        System.out.println(fifteenAfterMidnight.toLocalTime().toString());
        System.out.println();
        System.out.println(midnight.toLocalTime().toDateTimeToday().plusDays(1).toString());
        System.out.println(fiveAfterMidnight.toLocalTime().toDateTimeToday().toString());
        System.out.println(fifteenAfterMidnight.toLocalTime().toDateTimeToday().toString());
    }

    @Test
    public void testFormatTime() throws Exception {

    }

    @Test
    public void testConvertMillisecondsToMinutes() throws Exception {

    }
}
