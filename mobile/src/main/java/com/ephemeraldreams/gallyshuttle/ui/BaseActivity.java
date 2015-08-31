package com.ephemeraldreams.gallyshuttle.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ephemeraldreams.gallyshuttle.GallyShuttleApplication;

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

    public ActivityComponent getComponent(){
        return component;
    }
}
