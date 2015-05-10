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
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.BuildConfig;
import com.ephemeraldreams.gallyshuttle.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Fragment to display details about the application.
 */
public class AboutFragment extends Fragment {

    @InjectView(R.id.versionTextView) TextView versionTextView;

    @Inject FragmentManager fragmentManager;

    /**
     * Required empty public constructor
     */
    public AboutFragment() {

    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.getActivityComponent().inject(this);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, rootView);

        String buildType;
        if (BuildConfig.DEBUG) {
            buildType = "Debug ";
        } else {
            buildType = "Release ";
        }

        versionTextView.setText(buildType + "Version " + BuildConfig.VERSION_NAME);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Display credits.
     */
    @OnClick(R.id.creditsTextView)
    public void displayCredits() {
        AboutDialogFragment.displayDialogFragment(fragmentManager, R.string.dialog_credits_title, R.raw.credits);
    }

    /**
     * Send user to Google Play listing for application rating.
     */
    @OnClick(R.id.rateApplicationTextView)
    public void rateApplication() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.ephemeraldreams.gallyshuttle"));
        startActivity(intent);
    }

    /**
     * Send a support email to developer.
     */
    @OnClick(R.id.supportTextView)
    public void sendSupportEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String emailUriText = "mailto:" + Uri.encode("ephemeral.dreams.mobile@gmail.com") +
                "?subject=" + Uri.encode("Gally Shuttle Android Application Support") +
                "&body=" + Uri.encode("Type your question or feedback here.");
        Uri emailUri = Uri.parse(emailUriText);
        emailIntent.setData(emailUri);
        startActivity(Intent.createChooser(emailIntent, "Send email to developer"));
    }

    /**
     * Display open source licenses.
     */
    @OnClick(R.id.openSourceLicensesTextView)
    public void displayOpenSourceLicenses() {
        AboutDialogFragment.displayDialogFragment(fragmentManager, R.string.dialog_licenses_title, R.raw.licenses);
    }

    /**
     * Display terms of services.
     */
    @OnClick(R.id.termsOfServiceTextView)
    public void displayTermsOfServices() {
        AboutDialogFragment.displayDialogFragment(fragmentManager, R.string.dialog_terms_of_service_title, R.raw.terms_of_use);
    }

    /**
     * Display privacy policy.
     */
    @OnClick(R.id.privacyTextView)
    public void displayPrivacyPolicy() {
        AboutDialogFragment.displayDialogFragment(fragmentManager, R.string.dialog_privacy_policy_title, R.raw.privacy);
    }
}
