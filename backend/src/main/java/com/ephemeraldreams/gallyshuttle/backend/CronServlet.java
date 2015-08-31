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

package com.ephemeraldreams.gallyshuttle.backend;

import com.ephemeraldreams.gallyshuttle.backend.models.Schedule;
import com.ephemeraldreams.gallyshuttle.backend.scraper.ScheduleScraper;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Servlet responsible for cron jobs.
 */
public class CronServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CronServlet.class.getName());

    private static final Gson GSON = new Gson();
    private boolean isAllSchedulesSaved = true;
    private List<Exception> exceptions = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ResponseBodyStatus responseBodyStatus = new ResponseBodyStatus();
        exceptions.clear();

        String path = req.getPathInfo().substring(1);
        switch (path) {
            case "schedules":
                try {
                    updateSchedules();
                    if (isAllSchedulesSaved) {
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                        responseBodyStatus.setStatus(ResponseBodyStatus.SUCCESS);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                        responseBodyStatus.setStatus(ResponseBodyStatus.PARTIAL_SUCCESS);
                        responseBodyStatus.setExceptions(exceptions);
                    }
                } catch (IOException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    responseBodyStatus.setStatus(ResponseBodyStatus.FAILURE);
                    exceptions.add(e);
                    responseBodyStatus.setExceptions(exceptions);
                } finally {
                    resp.getWriter().println(GSON.toJson(responseBodyStatus));
                }
                break;
            case "stations":
                updateStations();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                responseBodyStatus.setStatus(ResponseBodyStatus.SUCCESS);
                resp.getWriter().println(GSON.toJson(responseBodyStatus));
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
        }
    }

    /**
     * Download and save schedules to datastore.
     *
     * @throws IOException
     */
    private void updateSchedules() throws IOException {
        LOG.info("Downloading schedules...");
        Schedule schedule;
        for (int id = 0; id < 5; id++) {
            schedule = ScheduleScraper.scrape(id);
            if (schedule != null) {
                ofy().save().entity(schedule).now();
                LOG.info("Succeeded in downloading schedule " + Constants.SCHEDULES_NAMES[id] + ".");
            } else {
                isAllSchedulesSaved = false;
                String exceptionMessage = "Failed to download schedule " + Constants.SCHEDULES_NAMES[id] + ".";
                exceptions.add(new IOException(exceptionMessage));
                LOG.severe(exceptionMessage);
            }
        }
    }

    /**
     * Save stations to datastore.
     */
    private void updateStations() {
        LOG.info("Saving stations...");
        ofy().save().entities(Constants.ALL_STATIONS).now();
        LOG.info("Saved stations to datastore.");
    }

    /**
     * Class to represent HTTP response body.
     */
    private class ResponseBodyStatus {

        private static final String SUCCESS = "success";
        private static final String FAILURE = "failure";
        private static final String PARTIAL_SUCCESS = "partial success";

        @Expose @SerializedName("status")
        private String status;

        @Expose @SerializedName("exceptions")
        private List<Exception> exceptions;

        public ResponseBodyStatus() {

        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setExceptions(List<Exception> exceptions) {
            this.exceptions = exceptions;
        }

        public String getStatus() {
            return status;
        }

        public List<Exception> getExceptions() {
            return exceptions;
        }
    }
}
