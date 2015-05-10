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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ui.events.OnHtmlFileLoadedEvent;
import com.ephemeraldreams.gallyshuttle.ui.tasks.LoadHtmlFileTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment to display Gallaudet University's shuttle policies. Policies can be found in
 * "/res/raw/policies.html"
 */
public class PoliciesFragment extends Fragment {

    @InjectView(R.id.policies_web_view) WebView webView;
    @InjectView(R.id.licenses_progress_bar) ProgressBar progressBar;

    @Inject Bus bus;
    private LoadHtmlFileTask loadHtmlFileTask;

    public PoliciesFragment() {
        // Required empty public constructor
    }

    public static PoliciesFragment newInstance() {
        return new PoliciesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.getActivityComponent().inject(this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_policies, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadHtmlFileTask = new LoadHtmlFileTask(getActivity(), bus, R.raw.policies);
        loadHtmlFileTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadHtmlFileTask != null) {
            loadHtmlFileTask.cancel(true);
        }
    }

    @Subscribe
    public void onHtmlFileLoaded(OnHtmlFileLoadedEvent event) {
        if (!TextUtils.isEmpty(event.html)) {
            progressBar.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.loadData(event.html, "text/html", "utf-8");
        }
    }
}
