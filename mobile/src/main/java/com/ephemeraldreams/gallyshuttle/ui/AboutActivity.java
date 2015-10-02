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

package com.ephemeraldreams.gallyshuttle.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.ephemeraldreams.gallyshuttle.BuildConfig;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ui.services.CustomTabsWarmUpService;
import com.ephemeraldreams.gallyshuttle.ui.util.CustomTabsUtils;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

public class AboutActivity extends BaseActivity implements CustomTabsWarmUpService.CustomTabsConnectionCallback {

    private static final String BASE_URL = "https://gallyshuttle.appspot.com/";
    private static final String CREDITS_URL = "contributors.html";
    private static final String OPEN_SOURCE_URL = "open_source_licenses.html";
    private static final String TOS_URL = "terms_of_services.html";
    private static final String PRIVACY_URL = "privacy.html";

    @Bind(R.id.version_text_view) TextView versionTextView;
    private CustomTabsWarmUpService warmUpService;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getComponent().inject(this);
        warmUpService = new CustomTabsWarmUpService();
        versionTextView.setText(getString(R.string.version_fmt, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onResume() {
        super.onResume();
        warmUpService.bindCustomTabsService(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        warmUpService.unbindCustomTabsService(this);
    }

    @Override
    public void onCustomTabsConnected() {
        String url = BASE_URL + TOS_URL;
        if (warmUpService.mayLaunchUrl(Uri.parse(url), null, null)) {
            Timber.d("Successfully called mayLaunchUrl()");
        } else {
            Timber.d("Failed to call mayLaunchUrl()");
        }
    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        return R.id.nav_about;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.nav_about);
    }

    @OnClick(R.id.credits_text_view)
    public void displayCredits() {
        CustomTabsUtils.openCustomTab(this, BASE_URL + CREDITS_URL);
    }

    @OnClick(R.id.rate_text_view)
    public void rateApplication() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.ephemeraldreams.gallyshuttle"));
        startActivity(intent);
    }

    @OnClick(R.id.support_text_view)
    public void sendSupportEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String emailUriText = "mailto:" + Uri.encode("ephemeral.dreams.mobile@gmail.com") +
                "?subject=" + Uri.encode("Gally Shuttle Android Application Support") +
                "&body=" + Uri.encode("Type your question or feedback here.");
        Uri emailUri = Uri.parse(emailUriText);
        emailIntent.setData(emailUri);
        startActivity(Intent.createChooser(emailIntent, "Send email to developer"));
    }

    @OnClick(R.id.open_source_text_view)
    public void displayOpenSourceLicenses() {
        CustomTabsUtils.openCustomTab(this, BASE_URL + OPEN_SOURCE_URL);
    }

    @OnClick(R.id.tos_text_view)
    public void displayTermsOfServices() {
        CustomTabsUtils.openCustomTab(this, BASE_URL + TOS_URL);
    }

    @OnClick(R.id.privacy_text_view)
    public void displayPrivacyPolicy() {
        CustomTabsUtils.openCustomTab(this, BASE_URL + PRIVACY_URL);
    }
}
