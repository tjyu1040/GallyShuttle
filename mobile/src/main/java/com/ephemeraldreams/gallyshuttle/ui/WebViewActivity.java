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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ephemeraldreams.gallyshuttle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity is used as a fallback when there is no browser installed that supports
 * Chrome Custom Tabs.
 */
public class WebViewActivity extends BaseActivity {

    public static final String EXTRA_URL = "extra.url";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.webview) WebView webView;

    private String url;

    /**
     * @param activity The activity that wants to open the Uri.
     * @param uri      The uri to be opened.
     */
    public static void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra(EXTRA_URL);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(url);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        webView.loadUrl(url);
    }

    @Override
    protected int getSelfNavigationDrawerItemId() {
        return INVALID_NAVIGATION_DRAWER_ITEM_ID;
    }

    @Override
    protected String getActionBarTitle() {
        return url;
    }
}
