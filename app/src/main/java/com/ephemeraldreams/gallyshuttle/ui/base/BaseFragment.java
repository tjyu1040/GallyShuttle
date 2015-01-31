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

package com.ephemeraldreams.gallyshuttle.ui.base;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Base fragment for dagger usage.
 */
public class BaseFragment extends Fragment {

    public CharSequence activityTitle;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject(this);
    }

    public void inject(Object object) {
        ((BaseActivity) getActivity()).inject(object);
    }

    @Override
    public void onResume() {
        super.onResume();
        activityTitle = ((BaseActivity) getActivity()).getSupportActionBar().getTitle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(activityTitle);
    }
}