package com.ephemeraldreams.gallyshuttle.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;
import com.ephemeraldreams.gallyshuttle.R;
import com.ephemeraldreams.gallyshuttle.ui.util.CustomTabsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Base activity for dagger injection and navigation drawer setup.
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Delay to launch navigation drawer to allow close animation to play.
    private static final long NAV_DRAWER_LAUNCH_DELAY = 250;

    // Item id for an activity not defined within navigation drawer.
    protected static final int INVALID_NAVIGATION_DRAWER_ITEM_ID = -1;

    // Fade in and out animations for main content while switching between different activities of
    // the app through the navigation drawer.
    private static final int MAIN_CONTENT_FADE_OUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADE_IN_DURATION = 250;

    private static final String POLICIES_URL = "http://www.gallaudet.edu/transportation/shuttle-bus-services.html";
    private static final String FAQ_URL = "http://www.gallaudet.edu/transportation/faq.html";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.main_coordinator_layout) CoordinatorLayout coordinatorLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Handler drawerHandler;

    private ActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerActivityComponent.builder()
                .applicationComponent(GallyShuttleApplication.getComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(getSelfNavigationDrawerItemId());
        coordinatorLayout.setAlpha(0);
        coordinatorLayout.animate().alpha(1).setDuration(MAIN_CONTENT_FADE_IN_DURATION);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setNavigationDrawer();
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Retrieve current activity component.
     *
     * @return Current activity component to inject objects.
     */
    public ActivityComponent getComponent() {
        return component;
    }

    private void setNavigationDrawer() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getActionBarTitle());
            actionBar.setDisplayShowHomeEnabled(true);
        }
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open_desc,
                R.string.nav_close_desc
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        if (getSelfNavigationDrawerItemId() != INVALID_NAVIGATION_DRAWER_ITEM_ID) {
            navigationView.setCheckedItem(getSelfNavigationDrawerItemId());
        }
        drawerHandler = new Handler();
        coordinatorLayout.setAlpha(0);
        coordinatorLayout.animate().alpha(1).setDuration(MAIN_CONTENT_FADE_IN_DURATION);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        if (menuItem.getItemId() == getSelfNavigationDrawerItemId()) {
            drawerLayout.closeDrawers();
            return false;
        }
        drawerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateTo(menuItem.getItemId());
            }
        }, NAV_DRAWER_LAUNCH_DELAY);
        if (menuItem.getItemId() != R.id.nav_policies && menuItem.getItemId() != R.id.nav_faq) {
            coordinatorLayout.animate().alpha(0).setDuration(MAIN_CONTENT_FADE_OUT_DURATION);
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
        return false;
    }

    private void navigateTo(@IdRes int navigationItemId) {
        switch (navigationItemId) {
            case R.id.nav_home:
                ArrivalCountdownActivity.launch(this);
                break;
            case R.id.nav_continuous:
                ScheduleActivity.launch(this, R.string.path_continuous);
                break;
            case R.id.nav_late_night:
                ScheduleActivity.launch(this, R.string.path_late_night);
                break;
            case R.id.nav_weekend:
                ScheduleActivity.launch(this, R.string.path_weekend);
                break;
            case R.id.nav_alt_continuous:
                ScheduleActivity.launch(this, R.string.path_alt_continuous);
                break;
            case R.id.nav_modified:
                ScheduleActivity.launch(this, R.string.path_modified);
                break;
            case R.id.nav_policies:
                CustomTabsUtils.openCustomTab(this, POLICIES_URL);
                break;
            case R.id.nav_faq:
                CustomTabsUtils.openCustomTab(this, FAQ_URL);
                break;
            case R.id.nav_settings:
                SettingsActivity.launch(this);
                break;
            case R.id.nav_about:
                AboutActivity.launch(this);
                break;
        }
    }

    /**
     * @return Navigation drawer item id to set for this {@link android.app.Activity}.
     */
    @IdRes
    abstract protected int getSelfNavigationDrawerItemId();

    /**
     * @return Action bar title to set for this {@link android.app.Activity}.
     */
    abstract protected String getActionBarTitle();

    /**
     * @param title Action bar title to set for this {@link android.app.Activity}.
     */
    public void setToolbarTitle(String title) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
