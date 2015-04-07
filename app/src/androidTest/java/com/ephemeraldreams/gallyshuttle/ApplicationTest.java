package com.ephemeraldreams.gallyshuttle;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Class to test whether the application's dependency injection framework works.
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
        assertNotNull(ShuttleApplication.getAppComponent());
    }
}
