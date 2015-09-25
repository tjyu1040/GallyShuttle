/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.ephemeraldreams.gallyshuttle.backend.endpoints;

import com.ephemeraldreams.gallyshuttle.backend.models.RegistrationRecord;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
        name = "registration",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.gallyshuttle.ephemeraldreams.com",
                ownerName = "backend.gallyshuttle.ephemeraldreams.com",
                packagePath = ""
        ),
        description = "API for Google Cloud Messaging device registration."
)
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend.
     *
     * @param registrationRecord The Google Cloud Messaging registration record to add.
     */
    @ApiMethod(name = "register")
    public void registerDevice(RegistrationRecord registrationRecord) {
        if (findRecord(registrationRecord) != null) {
            log.info("Device " + registrationRecord.getToken() + " already registered, skipping register");
            return;
        }
        ofy().save().entity(registrationRecord).now();
    }

    /**
     * Unregister a device from the backend.
     *
     * @param registrationRecord The Google Cloud Messaging record to remove.
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(RegistrationRecord registrationRecord) {
        RegistrationRecord record = findRecord(registrationRecord);
        if (record == null) {
            log.info("Device " + registrationRecord.getToken() + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }

    /**
     * Return a collection of registered devices.
     *
     * @param count The number of devices to list.
     * @return a list of Google Cloud Messaging registration Ids.
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<RegistrationRecord> listDevices(@Named("count") int count) {
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(count).list();
        return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
    }

    private RegistrationRecord findRecord(RegistrationRecord record) {
        return ofy().load().type(RegistrationRecord.class).filter("token", record.getToken()).first().now();
    }

}
