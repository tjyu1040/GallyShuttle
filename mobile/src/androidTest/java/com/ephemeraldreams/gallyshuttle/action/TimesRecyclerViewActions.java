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

package com.ephemeraldreams.gallyshuttle.action;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import com.ephemeraldreams.gallyshuttle.R;

import org.hamcrest.Matcher;

/**
 * Espresso actions for using a times RecyclerView.
 */
public final class TimesRecyclerViewActions {

    private TimesRecyclerViewActions() {
        // No instance
    }

    /**
     * Click the alarm button in the time view holder
     *
     * @return A {@link ViewAction} that clicks the alarm button in the time view holder.
     */
    public static ViewAction clickAlarmButton() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(R.id.set_alarm_button);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }
}
