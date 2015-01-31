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

package com.ephemeraldreams.gallyshuttle.ui.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Listener class to handle {@link android.support.v7.widget.RecyclerView} clicks.
 */
public class RecyclerViewListener implements RecyclerView.OnItemTouchListener {

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    private GestureDetector mGestureDetector;
    private OnItemClickListener mOnItemClickListener;

    public RecyclerViewListener(Context context, OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        // Get child view of recyclerview at the tapped position
        View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (childView != null && mOnItemClickListener != null && mGestureDetector.onTouchEvent(motionEvent)) {
            mOnItemClickListener.onItemClick(childView, recyclerView.getChildPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }
}