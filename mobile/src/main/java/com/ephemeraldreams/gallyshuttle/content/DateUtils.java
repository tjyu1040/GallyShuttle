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

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

/**
 * An utility class to handle formatting and parsing of times and dates.
 */
public class DateUtils {

    private DateUtils() {
        // No instance.
    }

    /**
     * Formatter to convert a String time into correct 24-hours time format for processing.
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("hh:mm aa");

    /**
     * Parse a string time into a {@link LocalDateTime} object.
     *
     * @param time String time to parse.
     * @return Today's date with time.
     */
    public static LocalDateTime parseToLocalDateTime(String time) {
        return LocalTime.parse(time, DATE_FORMAT).toDateTimeToday().toLocalDateTime();
    }

    /**
     * Convert milliseconds to "mm:ss" format.
     *
     * @param millis Milliseconds to convert.
     * @return String format in "mm:ss""
     */
    public static String convertMillisecondsToTime(long millis) {
        long hour = TimeUnit.MILLISECONDS.toHours(millis);
        long minute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long second = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
