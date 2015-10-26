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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class LateNightTimeComparatorTest {

    private ArrayList<String> times;
    private ArrayList<String> sortedTimes;
    private TreeSet<String> timesSet;
    private TreeSet<String> sortedTimesSet;

    @Before
    public void setUp() {
        times = new ArrayList<>();
        times.add("10:30 PM");
        times.add("11:00 PM");
        times.add("11:30 PM");
        times.add("12:00 AM");
        times.add("10:00 PM");

        sortedTimes = new ArrayList<>();
        sortedTimes.add("10:00 PM");
        sortedTimes.add("10:30 PM");
        sortedTimes.add("11:00 PM");
        sortedTimes.add("11:30 PM");
        sortedTimes.add("12:00 AM");
        sortedTimesSet = new TreeSet<>();
        sortedTimesSet.addAll(sortedTimes);
    }

    @Test
    public void testCompare() throws Exception {
        timesSet = new TreeSet<>(new LateNightTimeComparator());
        timesSet.addAll(times);
        System.out.println(timesSet.toString());
        assertThat(timesSet).isEqualTo(sortedTimesSet);
    }
}
