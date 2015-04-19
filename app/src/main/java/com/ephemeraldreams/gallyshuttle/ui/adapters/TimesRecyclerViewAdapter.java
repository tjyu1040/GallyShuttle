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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Adapter class to handle and display times in a {@link android.support.v7.widget.RecyclerView}.
 */
public class TimesRecyclerViewAdapter extends RecyclerView.Adapter<TimesRecyclerViewAdapter.TimeViewHolder> {

    private final List<String> times;
    private OnTimeClickListener onTimeClickListener;

    public TimesRecyclerViewAdapter(List<String> times) {
        this.times = times;
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_time, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimeViewHolder holder, int position) {
        String time = times.get(position);
        holder.timeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public void setOnTimeClickListener(final OnTimeClickListener onTimeClickListener) {
        this.onTimeClickListener = onTimeClickListener;
    }

    /**
     * Time view holder.
     */
    public class TimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.time_text_view) public TextView timeTextView;

        public TimeViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onTimeClickListener != null) {
                onTimeClickListener.onTimeClick(times.get(getAdapterPosition()));
            }
        }
    }

    /**
     * Interface to handle click listener on time selected.
     */
    public interface OnTimeClickListener {
        void onTimeClick(String time);
    }
}
