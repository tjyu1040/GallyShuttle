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

package com.ephemeraldreams.gallyshuttle.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Adapter class to handle and display schedule in a {@link android.support.v4.view.ViewPager}.
 */
public class NavigationDrawerAdapter extends BaseExpandableListAdapter {

    private final String[] headers;
    private final HashMap<String, List<String>> items;

    private final LayoutInflater layoutInflater;

    public NavigationDrawerAdapter(LayoutInflater layoutInflater, String[] headers, HashMap<String, List<String>> items) {
        this.headers = headers;
        this.items = items;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getGroupCount() {
        return headers.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(headers[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(headers[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerText = headers[groupPosition];
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.navigation_list_group, parent, false);
        }
        TextView headerTextView = ButterKnife.findById(convertView, R.id.navigation_drawer_header_text_view);
        headerTextView.setTypeface(null, Typeface.BOLD);
        headerTextView.setText(headerText);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String itemText = items.get(headers[groupPosition]).get(childPosition);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.navigation_list_item, parent, false);
        }
        TextView itemTextView = ButterKnife.findById(convertView, R.id.navigation_drawer_item_text_view);
        itemTextView.setText(itemText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
