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

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
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

public class AboutDialogFragment extends DialogFragment {

    private static final String FRAGMENT_TAG = "com.ephemeraldreams.gallyshuttle.ui.AboutDialogFragment";
    private static final String ARG_TITLE_ID = "string title resource id";
    private static final String ARG_RAW_RESOURCE_ID = "raw resource id";

    @InjectView(R.id.dialog_web_view) WebView webView;
    @InjectView(R.id.dialog_progress_bar) ProgressBar progressBar;
    private LoadHtmlFileTask loadHtmlFileTask;

    @Inject Bus bus;

    @StringRes private int resStringTitleId;
    @RawRes private int resRawResourceId;

    public AboutDialogFragment() {

    }

    private static AboutDialogFragment newInstance(@StringRes int resStringTitleId, @RawRes int resRawResourceId) {
        AboutDialogFragment fragment = new AboutDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_ID, resStringTitleId);
        args.putInt(ARG_RAW_RESOURCE_ID, resRawResourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        setRetainInstance(true);

        if (getArguments() != null) {
            resStringTitleId = getArguments().getInt(ARG_TITLE_ID);
            resRawResourceId = getArguments().getInt(ARG_RAW_RESOURCE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_dialog, container, false);
        ButterKnife.inject(this, rootView);
        getDialog().setTitle(getResources().getText(resStringTitleId));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadHtmlFileTask = new LoadHtmlFileTask(getActivity(), bus, resRawResourceId);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Subscribe
    public void onHtmlFileLoaded(OnHtmlFileLoadedEvent event) {
        if (!TextUtils.isEmpty(event.html)) {
            progressBar.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.loadData(event.html, "text/html", "utf-8");
        }
    }

    /**
     * Display dialog fragment.
     *
     * @param fragmentManager  Fragment manager to display dialog fragment.
     * @param resStringTitleId String title resource id.
     * @param resRawResourceId HTML raw resource id.
     */
    public static void displayDialogFragment(FragmentManager fragmentManager, @StringRes int resStringTitleId, @RawRes int resRawResourceId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);
        AboutDialogFragment.newInstance(resStringTitleId, resRawResourceId).show(fragmentTransaction, FRAGMENT_TAG);
    }
}
