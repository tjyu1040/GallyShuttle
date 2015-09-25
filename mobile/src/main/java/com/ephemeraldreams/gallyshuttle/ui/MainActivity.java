package com.ephemeraldreams.gallyshuttle.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.net.api.GallyShuttleApiService;
import com.ephemeraldreams.gallyshuttle.ui.events.NetworkStateChangedEvent;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final Handler drawerHandler = new Handler();
    private Snackbar networkSnackBar;

    @Inject Bus bus;
    @Inject FragmentManager fragmentManager;
    @Inject GallyShuttleApiService gallyShuttleApiService;
    @Inject SharedPreferences sharedPreferences;
    @Inject Resources resources;
    @Inject Gson gson;

    private GoogleApiClient googleApiClient;
    private Intent cachedInvitationIntent;
    private BroadcastReceiver referralReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getComponent().inject(this);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        setSelectedNavigationItem(R.id.nav_home);

        if (savedInstanceState == null) {
            processReferral(getIntent());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReferralReceiver();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReferralReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.launch(this);
                return true;
            default:
                return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setChecked(true);
        closeDrawers(new Runnable() {
            @Override
            public void run() {
                setSelectedNavigationItem(menuItem.getItemId());
            }
        });
        return true;
    }

    private void closeDrawers(Runnable runnable) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            drawerHandler.removeCallbacksAndMessages(null);
            drawerHandler.postDelayed(runnable, 250);
        }
    }

    private void setSelectedNavigationItem(@IdRes final int id) {
        switch (id) {
            case R.id.nav_home:
                Timber.d("Starting count down fragment.");
                setCurrentFragment(CountDownFragment.newInstance());
                break;
            case R.id.nav_continuous:
                Timber.d("Starting continuous fragment.");
                setCurrentFragment(ScheduleFragment.newInstance(resources.getString(R.string.path_continuous)));
                break;
            case R.id.nav_late_night:
                Timber.d("Starting late night fragment.");
                setCurrentFragment(ScheduleFragment.newInstance(resources.getString(R.string.path_late_night)));
                break;
            case R.id.nav_weekend:
                Timber.d("Starting weekend fragment.");
                setCurrentFragment(ScheduleFragment.newInstance(resources.getString(R.string.path_weekend)));
                break;
            case R.id.nav_alt_continuous:
                Timber.d("Starting alt continuous fragment.");
                setCurrentFragment(ScheduleFragment.newInstance(resources.getString(R.string.path_alt_continuous)));
                break;
            case R.id.nav_modified:
                Timber.d("Starting modified fragment.");
                setCurrentFragment(ScheduleFragment.newInstance(resources.getString(R.string.path_modified)));
                break;
            case R.id.nav_policies:
                //TODO: add policies fragment
                break;
            case R.id.nav_about:
                setCurrentFragment(AboutFragment.newInstance());
                break;
        }
    }

    private void setCurrentFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Subscribe
    public void onNetworkDisconnected(NetworkStateChangedEvent event) {
        if (networkSnackBar == null) {
            networkSnackBar = Snackbar.make(drawerLayout, "Network disconnected", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: refresh if needed
                        }
                    });
        }
        if (event.isConnected) {
            networkSnackBar.dismiss();
        } else {
            networkSnackBar.show();
        }
    }

    /**
     * Process app invitation referral.
     *
     * @param intent Invitation intent to process.
     */
    private void processReferral(Intent intent) {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Timber.d("googleApiClient#onConnected");
                        if (cachedInvitationIntent != null) {
                            updateInvitationStatus(cachedInvitationIntent);
                            cachedInvitationIntent = null;
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Timber.d("googleApiClient#onConnectionSuspended");
                    }
                })
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Timber.d("googleApiClient#onConnectionFailed:" + connectionResult.getErrorCode());
                        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                            Timber.w("onConnectionFailed because an API was unavailable.");
                        }
                    }
                })
                .addApi(AppInvite.API)
                .build();

        if (AppInviteReferral.hasReferral(intent)) {

            String invitationId = AppInviteReferral.getInvitationId(intent);
            String deepLink = AppInviteReferral.getDeepLink(intent);

            //TODO: Update dialog message.
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.invite_dialog_title))
                    .setMessage("Invitation Id=" + invitationId + ", Deep link=" + deepLink)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            alertDialog.show();

            if (googleApiClient.isConnected()) {
                updateInvitationStatus(intent);
            } else {
                Timber.w("googleApiClient not connected, cannot update invitation.");
                cachedInvitationIntent = intent;
            }
        }
    }

    /**
     * Mark invitation as successful.
     *
     * @param invitationIntent Intent to update invitation installation success.
     */
    private void updateInvitationStatus(Intent invitationIntent) {
        String invitationId = AppInviteReferral.getInvitationId(invitationIntent);
        if (AppInviteReferral.isOpenedFromPlayStore(invitationIntent)) {
            AppInvite.AppInviteApi.updateInvitationOnInstall(googleApiClient, invitationId);
        }
        AppInvite.AppInviteApi.convertInvitation(googleApiClient, invitationId);
    }

    /**
     * Register a local broadcast receiver to listen for invitation referral.
     */
    private void registerReferralReceiver() {
        Timber.d("referralReceiver registered.");
        referralReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReferral(intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.action_deep_link));
        LocalBroadcastManager.getInstance(this).registerReceiver(referralReceiver, intentFilter);
    }

    /**
     * Unregister a local broadcast receiver to stop listening for invitation referral.
     */
    private void unregisterReferralReceiver() {
        if (referralReceiver != null) {
            Timber.d("referralReceiver unregistered.");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(referralReceiver);
        }
    }
}
