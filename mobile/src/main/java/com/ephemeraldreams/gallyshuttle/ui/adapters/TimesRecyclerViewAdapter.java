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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter class to handle and display times in a {@link android.support.v7.widget.RecyclerView}.
 */
public class TimesRecyclerViewAdapter extends RecyclerView.Adapter<TimesRecyclerViewAdapter.TimeViewHolder> {

    public interface OnAlarmClickListener {
        void onTimeClick(String time);
    }

    private List<String> times;
    private OnAlarmClickListener onAlarmClickListener;

    public TimesRecyclerViewAdapter() {
        this(new ArrayList<String>());
    }

    public TimesRecyclerViewAdapter(ArrayList<String> times) {
        this.times = times;
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_view_holder, parent, false);
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

    public void addTime(int position, String time) {
        times.add(position, time);
        notifyItemInserted(position);
    }

    public void addTimes(List<String> times) {
        int position = 0;
        for (String time : times) {
            addTime(position, time);
            position++;
        }
    }

    public void removeTime(int position) {
        times.remove(position);
        notifyItemRemoved(position);
    }

    public void clearTimes(){
        int position = times.size() - 1;
        while (!times.isEmpty()) {
            removeTime(position);
            position--;
        }
    }

    public void setOnAlarmClickListener(final OnAlarmClickListener onAlarmClickListener) {
        this.onAlarmClickListener = onAlarmClickListener;
    }

    /**
     * Time view holder.
     */
    class TimeViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.time_text_view) TextView timeTextView;

        public TimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.set_alarm_button)
        public void onClickAlarmButton() {
            if (onAlarmClickListener != null) {
                onAlarmClickListener.onTimeClick(times.get(getAdapterPosition()));
            }
        }
    }
}
