package com.etern.locationscout;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by etern on 6/8/2017.
 */

public interface I500pxApi {
    @GET("photos/search")
    Call<PhotosSearchResponse500px> searchPhotosByLocation(@Query("consumer_key") String consumerKey,
                                                           @Query("geo") String geoString);
}
