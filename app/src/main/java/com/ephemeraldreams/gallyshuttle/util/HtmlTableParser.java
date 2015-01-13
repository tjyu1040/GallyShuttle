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

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * An utility class to parse and store HTML table data into manageable ArrayLists.
 */
public class HtmlTableParser {

    private Elements tableHeaders;
    private Elements tableData;

    public HtmlTableParser(String html) {
        Document document = Jsoup.parse(html);
        Elements tableElements = document.select("table");
        tableHeaders = tableElements.select("thead tr th");
        tableData = tableElements.select(":not(thead) tr");
    }

    public ArrayList<String> getHeaders() {
        if (tableHeaders.isEmpty()) {
            return null;
        } else {
            ArrayList<String> headers = new ArrayList<>();
            for (Element header : tableHeaders) {
                headers.add(header.text());
            }
            return headers;
        }
    }

    public ArrayList<ArrayList<String>> getData() {
        if (tableData.isEmpty()) {
            return null;
        } else {
            ArrayList<ArrayList<String>> data = new ArrayList<>();

            ArrayList<String> column;
            for (int i = 0; i < tableHeaders.size(); i++) {
                column = getColumn(i);
                column.remove(0);
                data.add(column);
            }
            return data;
        }
    }

    public ArrayList<String> getColumn(int columnIndex) {
        if (tableHeaders.isEmpty() && tableData.isEmpty()) {
            return null;
        } else {
            ArrayList<String> columnList = new ArrayList<>();
            columnList.add(tableHeaders.get(columnIndex).text());
            for (Element row : tableData) {
                Elements rowItems = row.select("td");
                try {
                    String item = rowItems.get(columnIndex).text();
                    if (!TextUtils.isEmpty(item) && item.length() > 4) {
                        columnList.add(ScheduleTimeFormatter.format(item));
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Misformatted HTML attributes/tags were used interfering with parsing.
                    Timber.e("Colspan attribute used in table.", e);
                }
            }
            return columnList;
        }
    }
}