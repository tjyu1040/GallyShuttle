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

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.events.OnLoadHtmlFileEvent;
import com.ephemeraldreams.gallyshuttle.tasks.LoadHtmlFileTask;
import com.ephemeraldreams.gallyshuttle.ui.base.BaseActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutDialogFragment extends DialogFragment {

    public static final String ARG_TITLE_ID = "string title id";
    public static final String ARG_RAW_RESOURCE_ID = "raw resource id";

    @InjectView(R.id.dialogWebView) WebView mDialogWebView;
    @InjectView(R.id.dialogProgressBar) ProgressBar mDialogProgressBar;
    private LoadHtmlFileTask mLoadHtmlFileTask;

    @Inject Bus mBus;

    private int mResStringTitleId;
    private int mResRawResourceId;

    private static final String FRAGMENT_TAG = "com.ephemeraldreams.gallyshuttle.ui.AboutDialogFragment";

    /**
     * Required empty public constructor
     */
    public AboutDialogFragment() {

    }

    public static AboutDialogFragment newInstance(int resStringTitleId, int resRawResourceId) {
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
        if (getArguments() != null) {
            mResStringTitleId = getArguments().getInt(ARG_TITLE_ID);
            mResRawResourceId = getArguments().getInt(ARG_RAW_RESOURCE_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadHtmlFileTask != null) {
            mLoadHtmlFileTask.cancel(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getResources().getText(mResStringTitleId));
        View rootView = inflater.inflate(R.layout.fragment_about_dialog, container, false);
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
        ((BaseActivity) getActivity()).inject(this);
        mLoadHtmlFileTask = new LoadHtmlFileTask(getActivity(), mResRawResourceId, mBus);
        mLoadHtmlFileTask.execute();
    }

    public static void displayDialogFragment(FragmentManager fragmentManager, int resStringTitleId, int resRawResourceId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = AboutDialogFragment.newInstance(resStringTitleId, resRawResourceId);
        newFragment.show(fragmentTransaction, FRAGMENT_TAG);
    }

    @Subscribe
    public void onHtmlFileLoad(OnLoadHtmlFileEvent event) {
        mDialogProgressBar.setVisibility(View.INVISIBLE);
        mDialogWebView.setVisibility(View.VISIBLE);
        mDialogWebView.loadDataWithBaseURL(null, event.html, "text/html", "utf-8", null);
    }
}