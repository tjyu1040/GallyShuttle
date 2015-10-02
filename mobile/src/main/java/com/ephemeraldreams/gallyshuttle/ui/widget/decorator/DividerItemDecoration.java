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

package com.ephemeraldreams.gallyshuttle.ui.widget.decorator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * An {@link android.support.v7.widget.RecyclerView.ItemDecoration} that draws a list style divider
 * between items in a {@link RecyclerView}. Pulled from Android support demos and modified.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;
    private boolean mShowFirstDivider = false;
    private boolean mShowLastDivider = false;

    public DividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
        divider = a.getDrawable(0);
        a.recycle();
    }

    public DividerItemDecoration(Context context, AttributeSet attrs, boolean showFirstDivider, boolean showLastDivider) {
        this(context, attrs);
        mShowFirstDivider = showFirstDivider;
        mShowLastDivider = showLastDivider;
    }

    public DividerItemDecoration(Drawable divider) {
        this.divider = divider;
    }

    public DividerItemDecoration(Drawable divider, boolean showFirstDivider, boolean showLastDivider) {
        this(divider);
        mShowFirstDivider = showFirstDivider;
        mShowLastDivider = showLastDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (divider == null) {
            return;
        }
        if (parent.getChildLayoutPosition(view) <= 1) {
            return;
        }
        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = divider.getIntrinsicHeight();
        } else {
            outRect.left = divider.getIntrinsicWidth();
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (divider == null) {
            super.onDrawOver(c, parent, state);
            return;
        }

        // Initialization needed to avoid compiler warning
        int left = 0, right = 0, top = 0, bottom = 0, size;
        int orientation = getOrientation(parent);
        int childCount = parent.getChildCount();

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = divider.getIntrinsicHeight();
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
        } else { // HORIZONTAL
            size = divider.getIntrinsicWidth();
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
        }

        // Show first divider.
        for (int i = mShowFirstDivider ? 0 : 1; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.getTop() - params.topMargin;
                bottom = top + size;
            } else { // HORIZONTAL
                left = child.getLeft() - params.leftMargin;
                right = left + size;
            }
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }

        // Show last divider.
        if (mShowLastDivider && childCount > 0) {
            View child = parent.getChildAt(childCount - 1);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.getBottom() + params.bottomMargin;
                bottom = top + size;
            } else { // HORIZONTAL
                left = child.getRight() + params.rightMargin;
                right = left + size;
            }
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}
