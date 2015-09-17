package com.ephemeraldreams.gallyshuttle.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;

/**
 * Base activity for dagger injection.
 */
public class BaseActivity extends AppCompatActivity {

    private ActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerActivityComponent.builder()
                .applicationComponent(GallyShuttleApplication.getComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    /**
     * Retrieve current activity component.
     * @return Current activity component to inject objects.
     */
    public ActivityComponent getComponent() {
        return component;
    }
}
