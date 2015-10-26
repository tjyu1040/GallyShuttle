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

package com.ephemeraldreams.gallyshuttle.backend.scraper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

/**
 * A comparator to sort times at late night. Includes special case where midnight is handled.
 */
public class LateNightTimeComparator implements Comparator<String> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("hh:mm aa");

    @Override
    public int compare(String o1, String o2) {
        DateTime time1 = LocalTime.parse(o1, DATE_FORMAT).toDateTimeToday();
        DateTime time2 = LocalTime.parse(o2, DATE_FORMAT).toDateTimeToday();
        if (time1.getHourOfDay() == 0) {
            time1 = time1.plusDays(1).withTimeAtStartOfDay();
        }
        if (time2.getHourOfDay() == 0) {
            time2 = time2.plusDays(1).withTimeAtStartOfDay();
        }
        return time1.compareTo(time2);
    }
}
