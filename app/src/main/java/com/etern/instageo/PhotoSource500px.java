package com.etern.instageo;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by etern on 6/12/2017.
 */

public class PhotoSource500px implements IPhotoSource {
    private String consumerKey;
    private I500pxApi api;
    private Activity context;

    public PhotoSource500px(String consumer_key, Activity context) {
        consumerKey = consumer_key;
        this.context = context;
    }

    @Override
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.500px.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        api = retrofit.create(I500pxApi.class);
    }

    @Override
    public void searchByLocation(Geolocation location, final IPhotoSearchListener callback) {
        String geoString = location.toString();
        Call<PhotosSearchResponse500px> request =
                api.searchPhotosByLocation(consumerKey, geoString);

        request.enqueue(new Callback<PhotosSearchResponse500px>() {
            @Override
            public void onResponse(Call<PhotosSearchResponse500px> call, Response<PhotosSearchResponse500px> response) {
                PhotosSearchResponse500px data = response.body();
                callback.onSucceed(Helpers.<PhotoObject500px, Photo>map(data.photos, new Helpers.Transformer<PhotoObject500px, Photo>() {
                    @Override
                    public Photo transform(PhotoObject500px orig) {
                        Photo photo = new Photo();
                        photo.url = orig.image_url;

                        return photo;
                    }
                }));
            }

            @Override
            public void onFailure(Call<PhotosSearchResponse500px> call, Throwable throwable) {
                Log.d(context.getResources().getString(R.string.app_name), throwable.getMessage());
            }
        });
    }
}
