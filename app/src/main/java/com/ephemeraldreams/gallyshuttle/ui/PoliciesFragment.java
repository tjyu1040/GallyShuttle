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

package com.ephemeraldreams.gallyshuttle.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.events.OnLoadHtmlFileEvent;
import com.ephemeraldreams.gallyshuttle.tasks.LoadHtmlFileTask;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Fragment to display Gallaudet University's shuttle policies. Policies can be found in
 * "/res/raw/policies.html"
 */
public class PoliciesFragment extends BaseFragment {

    @InjectView(R.id.policiesWebView) WebView mPoliciesWebView;
    @InjectView(R.id.licensesProgressBar) ProgressBar mLicensesProgressBar;

    @Inject Bus mBus;
    private LoadHtmlFileTask mLoadPoliciesTask;

    public PoliciesFragment() {
        // Required empty public constructor
    }

    public static PoliciesFragment newInstance() {
        return new PoliciesFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.policies_fragment_title);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadPoliciesTask != null) {
            mLoadPoliciesTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_policies, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadPoliciesTask = new LoadHtmlFileTask(getActivity(), R.raw.policies, mBus);
        mLoadPoliciesTask.execute();
    }

    @Subscribe
    public void onHtmlFileLoad(OnLoadHtmlFileEvent event) {
        Timber.d("Event received.");
        if (!TextUtils.isEmpty(event.html)) {
            mLicensesProgressBar.setVisibility(View.INVISIBLE);
            mPoliciesWebView.setVisibility(View.VISIBLE);
            mPoliciesWebView.loadDataWithBaseURL(null, event.html, "text/html", "utf-8", null);
        }
    }
}