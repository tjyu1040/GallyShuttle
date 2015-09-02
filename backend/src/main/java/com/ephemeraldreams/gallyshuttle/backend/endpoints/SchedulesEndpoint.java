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

package com.ephemeraldreams.gallyshuttle.backend.endpoints;

import com.ephemeraldreams.gallyshuttle.backend.models.Schedule;
import com.ephemeraldreams.gallyshuttle.backend.models.Station;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.NotFoundException;

import java.util.List;
import java.util.logging.Logger;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Endpoint for retrieving schedule data.
 */
@Api(
        name = "gallyshuttle",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.gallyshuttle.ephemeraldreams.com",
                ownerName = "backend.gallyshuttle.ephemeraldreams.com",
                packagePath = ""
        ),
        description = "API for retrieving Gallaudet University shuttle schedules."
)
public class SchedulesEndpoint {

    private static final Logger LOG = Logger.getLogger(SchedulesEndpoint.class.getName());

    /**
     * URL: /stations
     * Method: GET
     *
     * @return List of stations.
     */
    @ApiMethod(name = "stations", httpMethod = GET)
    public List<Station> stations() {
        return ofy().load().type(Station.class).list();
    }

    /**
     * URL: /schedules
     * Method: GET
     *
     * @return List of schedules.
     */
    @ApiMethod(name = "schedules", httpMethod = GET)
    public List<Schedule> schedules() throws NotFoundException {
        return ofy().load().type(Schedule.class).list();
    }

    /**
     * URL: /schedule/{name}
     * Method: GET
     *
     * @param name Path name of schedule to retrieve.
     * @return Specific named schedule.
     */
    @ApiMethod(name = "schedule", path = "schedule/{name}", httpMethod = GET)
    public Schedule getSchedule(@Named("name") String name) {
        return ofy().load().type(Schedule.class).filter("path", name).first().now();
    }
}
