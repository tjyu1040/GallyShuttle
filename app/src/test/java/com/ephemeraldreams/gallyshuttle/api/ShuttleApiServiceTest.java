package com.ephemeraldreams.gallyshuttle.api;

import com.ephemeraldreams.gallyshuttle.api.models.ApiResponse;
import com.ephemeraldreams.gallyshuttle.api.models.Entry;

import org.junit.Before;
import org.junit.Test;

import retrofit.client.OkClient;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ShuttleApiServiceTest {

    public boolean allowSystemPrint = false;
    public ShuttleApiService shuttleApiService;

    @Before
    public void setUp() throws Exception {
        ApiModule apiModule = new ApiModule();
        shuttleApiService = apiModule.provideShuttleApiService(apiModule.provideRestAdapter(new OkClient()));
    }

    @Test
    public void testGetContinuousSchedule() throws Exception {
        if (allowSystemPrint) {
            System.out.println("Testing getContinuousSchedule()...");
        }

        shuttleApiService.getContinuousSchedule()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .toBlocking()
                .forEach(new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse apiResponse) {
                        assertNotNull(apiResponse);
                        assertNotNull(apiResponse.feed);
                        assertNotNull(apiResponse.feed.title);
                        assertEquals("Continous", apiResponse.feed.title.toString());
                        assertNotNull(apiResponse.feed.entries);
                        for (Entry entry : apiResponse.feed.entries) {
                            assertNotNull(entry);

                            // Test that this schedule has all 6 stations:
                            // NoMa-Gallaudet, Union Station, MSSD, KDES, Benson, and Kellogg
                            assertNotNull(entry.bensonStationTime);
                            assertNotNull(entry.kdesStationTime);
                            assertNotNull(entry.kelloggStationTime);
                            assertNotNull(entry.mssdStationTime);
                            assertNotNull(entry.noMaGallaudetStationTime);
                            assertNotNull(entry.unionStationTime);
                            if (allowSystemPrint) {
                                System.out.println(entry.toString());
                            }
                        }
                    }
                });
        if (allowSystemPrint) {
            System.out.println("\n");
        }
    }

    @Test
    public void testGetAlternativeContinuousSchedule() throws Exception {
        if (allowSystemPrint) {
            System.out.println("Testing getAlternativeContinuousSchedule()...");
        }

        shuttleApiService.getAlternativeContinuousSchedule()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .toBlocking()
                .forEach(new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse apiResponse) {
                        assertNotNull(apiResponse);
                        assertNotNull(apiResponse.feed);
                        assertNotNull(apiResponse.feed.title);
                        assertEquals("Alternative Continous", apiResponse.feed.title.toString());
                        assertNotNull(apiResponse.feed.entries);
                        for (Entry entry : apiResponse.feed.entries) {
                            assertNotNull(entry);

                            // Test that this schedule has all 6 stations:
                            // NoMa-Gallaudet, Union Station, MSSD, KDES, Benson, and Kellog
                            assertNotNull(entry.bensonStationTime);
                            assertNotNull(entry.kdesStationTime);
                            assertNotNull(entry.kelloggStationTime);
                            assertNotNull(entry.mssdStationTime);
                            assertNotNull(entry.noMaGallaudetStationTime);
                            assertNotNull(entry.unionStationTime);

                            if (allowSystemPrint) {
                                System.out.println(entry.toString());
                            }
                        }
                    }
                });
        if (allowSystemPrint) {
            System.out.println("\n");
        }
    }

    @Test
    public void testGetLateNightSchedule() throws Exception {
        if (allowSystemPrint) {
            System.out.println("Testing getLateNightSchedule()...");
        }

        shuttleApiService.getLateNightSchedule()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .toBlocking()
                .forEach(new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse apiResponse) {
                        assertNotNull(apiResponse);
                        assertNotNull(apiResponse.feed);
                        assertNotNull(apiResponse.feed.title);
                        assertEquals("Late Night", apiResponse.feed.title.toString());
                        assertNotNull(apiResponse.feed.entries);
                        for (Entry entry : apiResponse.feed.entries) {
                            assertNotNull(entry);

                            // Test that this schedule has only 3 stations:
                            // Benson, Kellog, and Union Station
                            assertNotNull(entry.bensonStationTime);
                            assertNotNull(entry.kelloggStationTime);
                            assertNotNull(entry.unionStationTime);

                            assertNull(entry.noMaGallaudetStationTime);
                            assertNull(entry.mssdStationTime);
                            assertNull(entry.kdesStationTime);

                            if (allowSystemPrint) {
                                System.out.println(entry.toString());
                            }
                        }
                    }
                });
        if (allowSystemPrint) {
            System.out.println("\n");
        }
    }

    @Test
    public void testGetModifiedSchedule() throws Exception {
        if (allowSystemPrint) {
            System.out.println("Testing getModifiedContinuousSchedule()...");
        }

        shuttleApiService.getModifiedSchedule()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .toBlocking()
                .forEach(new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse apiResponse) {
                        assertNotNull(apiResponse);
                        assertNotNull(apiResponse.feed);
                        assertNotNull(apiResponse.feed.title);
                        assertEquals("Modified", apiResponse.feed.title.toString());
                        assertNotNull(apiResponse.feed.entries);
                        for (Entry entry : apiResponse.feed.entries) {
                            assertNotNull(entry);

                            // Test that this schedule has only 3 stations:
                            // Benson, Kellog, and Union Station
                            assertNotNull(entry.bensonStationTime);
                            assertNotNull(entry.kelloggStationTime);
                            assertNotNull(entry.unionStationTime);

                            assertNull(entry.noMaGallaudetStationTime);
                            assertNull(entry.mssdStationTime);
                            assertNull(entry.kdesStationTime);

                            if (allowSystemPrint) {
                                System.out.println(entry.toString());
                            }
                        }
                    }
                });
        if (allowSystemPrint) {
            System.out.println("\n");
        }
    }

    @Test
    public void testGetWeekendSchedule() throws Exception {
        if (allowSystemPrint) {
            System.out.println("Testing getWeekendContinuousSchedule()...");
        }

        shuttleApiService.getWeekendSchedule()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .toBlocking()
                .forEach(new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse apiResponse) {
                        assertNotNull(apiResponse);
                        assertNotNull(apiResponse.feed);
                        assertNotNull(apiResponse.feed.title);
                        assertEquals("Weekend", apiResponse.feed.title.toString());
                        assertNotNull(apiResponse.feed.entries);
                        for (Entry entry : apiResponse.feed.entries) {
                            assertNotNull(entry);

                            // Confirm that schedule has only 4 stations:
                            // Benson, Kellog, NoMa-Gallaudet, and Union Station
                            assertNotNull(entry.bensonStationTime);
                            assertNotNull(entry.kelloggStationTime);
                            assertNotNull(entry.noMaGallaudetStationTime);
                            assertNotNull(entry.unionStationTime);

                            assertNull(entry.kdesStationTime);
                            assertNull(entry.mssdStationTime);

                            if (allowSystemPrint) {
                                System.out.println(entry.toString());
                            }
                        }
                    }
                });
        if (allowSystemPrint) {
            System.out.println("\n");
        }
    }
}