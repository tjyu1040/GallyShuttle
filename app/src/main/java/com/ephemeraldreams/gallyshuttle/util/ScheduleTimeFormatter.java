/*
 * Copyright (C) 2014 Timothy Yu
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

package com.ephemeraldreams.gallyshuttle.util;

import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * An utility class to handle formatting of times and dates. Used in {@link com.ephemeraldreams.gallyshuttle.util.HtmlTableParser}
 * class specifically.
 */
public class ScheduleTimeFormatter {

    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm aa");

    /**
     * Format a string time value retrieved from HTML.
     *
     * @param time Time to format.
     * @return formatted time.
     */
    public static String format(String time) {
        return time.toUpperCase()
                .replaceAll("\u00a0", " ")  // Remove &nbsp characters
                .replaceAll("\\.", "")      // Remove period characters
                .replace("NOON", "PM")      // Replace NOON with PM
                .trim();
    }

    /**
     * Parse a string time value into a Date object.
     *
     * @param time Time to parse.
     * @return formatted date or default date.
     */
    public static Date parseToDate(String time) {
        try {
            LocalTime localTime = new LocalTime(TIME_FORMATTER.parse(time));
            return localTime.toDateTimeToday().toDate();
        } catch (ParseException e) {
            Timber.e("Parsing failed.", e);
            return new Date();
        }
    }
}