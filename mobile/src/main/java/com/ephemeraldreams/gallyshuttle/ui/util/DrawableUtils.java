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

package com.ephemeraldreams.gallyshuttle.ui.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * {@link Drawable} utility class.
 */
public final class DrawableUtils {

    private DrawableUtils() {
        // No instance.
    }

    /**
     * Retrieve and tint a drawable.
     *
     * @param context    Parent context to provide drawable and color resources.
     * @param drawableId Drawable resource id.
     * @param colorId    Tint color resource id.
     * @return Tinted drawable.
     */
    public static Drawable getTintedDrawable(Context context, @DrawableRes int drawableId, @ColorRes int colorId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));
        return drawable;
    }
}
