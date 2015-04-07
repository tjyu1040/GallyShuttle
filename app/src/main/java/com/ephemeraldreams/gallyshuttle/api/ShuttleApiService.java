/*
 *  Copyright (C) 2014 Timothy Yu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ephemeraldreams.gallyshuttle.api;

import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;

import retrofit.http.GET;
import rx.Observable;

/**
 * REST API service to get schedules in JSON format.
 */
public interface ShuttleApiService {

    @GET("/1/public/values?alt=json")
    Observable<ApiResponse> getContinuousSchedule();

    @GET("/2/public/values?alt=json")
    Observable<ApiResponse> getAlternativeContinuousSchedule();

    @GET("/3/public/values?alt=json")
    Observable<ApiResponse> getLateNightSchedule();

    @GET("/4/public/values?alt=json")
    Observable<ApiResponse> getModifiedSchedule();

    @GET("/5/public/values?alt=json")
    Observable<ApiResponse> getWeekendSchedule();
}
