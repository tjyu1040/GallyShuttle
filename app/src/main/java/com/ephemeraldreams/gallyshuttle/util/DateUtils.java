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

import android.annotation.SuppressLint;

import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * An utility class to handle formatting and parsing of times and dates.
 */
public class DateUtils {

    /**
     * Formatter to convert a String time into correct 12-hours time format for users to view.
     */
    @SuppressLint("SimpleDateFormat") public static final DateFormat TIME_TWELVE_HOURS_FORMATTER = new SimpleDateFormat("h:mm aa");

    /**
     * Formatter to convert a String time into correct 24-hours time format for processing.
     */
    @SuppressLint("SimpleDateFormat") public static final SimpleDateFormat TIME_TWENTY_FOUR_HOURS_FORMATTER = new SimpleDateFormat("hh:mm aa");

    /**
     * Parse a string time into a {@link Date} object.
     *
     * @param time String time to parse.
     * @return Date object with time.
     */
    @DebugLog
    public static Date parseToDate(String time) {
        try {
            LocalTime localTime = new LocalTime(TIME_TWENTY_FOUR_HOURS_FORMATTER.parse(time));
            return localTime.toDateTimeToday().toDate();
        } catch (ParseException e) {
            Timber.e("Parsing failed.", e);
            return new Date();
        }
    }

    /**
     * Strip and format a String time.
     *
     * @param time String time to trim and format.
     * @return Trimmed and formatted time.
     */
    @DebugLog
    public static String trimAndFormat(String time) {
        return time.toUpperCase()
                .replace("*", "")           // Remove * characters
                .replaceAll("\u00a0", " ")  // Remove &nbsp characters
                .replaceAll("\\.", "")      // Remove period characters
                .replace("NOON", "PM")      // Replace NOON with PM
                .trim();
    }
}
