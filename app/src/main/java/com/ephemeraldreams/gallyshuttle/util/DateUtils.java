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
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * An utility class to handle formatting and parsing of times and dates.
 */
public class DateUtils {

    /**
     * Formatter to convert a String time into correct 12-hours time format for users to view.
     */
    private static final DateTimeFormatter TIME_TWELVE_HOURS_FORMATTER = DateTimeFormat.forPattern("h:mm aa");


    /**
     * Formatter to convert a String time into correct 24-hours time format for processing.
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("hh:mm aa");

    /**
     * Parse a string time into a {@link Date} object.
     *
     * @param time String time to parse.
     * @return Date object with time.
     */
    public static LocalDateTime parseToLocalDateTime(String time) {
        return LocalTime.parse(trimAndFormat(time), DATE_FORMAT).toDateTimeToday().toLocalDateTime();
    }

    /**
     * Strip and format a String time.
     *
     * @param time String time to trim and format.
     * @return Trimmed and formatted time.
     */
    public static String trimAndFormat(String time) {
        return time.toUpperCase()
                .replace("*", "")           // Remove * characters
                .replaceAll("\u00a0", " ")  // Remove &nbsp characters
                .replaceAll("\\.", "")      // Remove period characters
                .replace("NOON", "PM")      // Replace NOON with PM
                .trim();
    }

    /**
     * Format into "h:mm aa" date format.
     *
     * @param localDateTime LocalDateTime to format.
     * @return String format in "h:mm aa"
     */
    public static String formatTime(LocalDateTime localDateTime) {
        return TIME_TWELVE_HOURS_FORMATTER.print(localDateTime);
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
