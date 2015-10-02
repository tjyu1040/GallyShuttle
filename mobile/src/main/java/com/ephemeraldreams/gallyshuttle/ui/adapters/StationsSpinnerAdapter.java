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

package com.ephemeraldreams.gallyshuttle.ui.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.net.api.models.Station;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom {@link ArrayAdapter} to inflate stations into spinner items.
 */
public class StationsSpinnerAdapter extends ArrayAdapter<Station> {

    public StationsSpinnerAdapter(Context context, List<Station> objects) {
        super(context, R.layout.station_spinner_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getStationViewHolder(position, convertView, parent, R.layout.station_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getStationViewHolder(position, convertView, parent, R.layout.station_spinner_dropdown_item);
    }

    private View getStationViewHolder(int position, View convertView, ViewGroup parent, @LayoutRes int textViewId) {
        StationViewHolder stationViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(textViewId, parent, false);
            stationViewHolder = new StationViewHolder(convertView);
            convertView.setTag(stationViewHolder);
        } else {
            stationViewHolder = (StationViewHolder) convertView.getTag();
        }
        Station station = getItem(position);
        stationViewHolder.textView.setText(station.name);
        return convertView;
    }

    static class StationViewHolder {
        @Bind(R.id.station_text_view) TextView textView;

        private StationViewHolder(View rootView) {
            ButterKnife.bind(this, rootView);
        }
    }
}
