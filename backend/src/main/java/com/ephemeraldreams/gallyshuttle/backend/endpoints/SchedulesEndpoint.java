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
import com.ephemeraldreams.gallyshuttle.backend.models.collections.ScheduleCollectionResponse;
import com.ephemeraldreams.gallyshuttle.backend.models.collections.StationCollectionResponse;
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
     * @return Collection of stations.
     * @throws NotFoundException
     */
    @ApiMethod(name = "stations", httpMethod = GET)
    public StationCollectionResponse stations() throws NotFoundException {
        List<Station> stations = ofy().load().type(Station.class).list();
        if (stations.isEmpty()) {
            throw new NotFoundException("Stations not found in datastore. Please contact admin.");
        } else {
            return new StationCollectionResponse(stations);
        }
    }

    /**
     * URL: /schedules
     * Method: GET
     *
     * @return Collection of schedules.
     * @throws NotFoundException
     */
    @ApiMethod(name = "schedules", httpMethod = GET)
    public ScheduleCollectionResponse schedules() throws NotFoundException {
        List<Schedule> schedules = ofy().load().type(Schedule.class).list();
        if (schedules.isEmpty()) {
            throw new NotFoundException("Schedules not found in datastore. Please contact admin.");
        } else {
            return new ScheduleCollectionResponse(schedules);
        }
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
        return ofy().load().type(Schedule.class).filter("pathName", name).first().now();
    }
}
