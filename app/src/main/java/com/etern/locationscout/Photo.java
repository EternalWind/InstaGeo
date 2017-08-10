package com.etern.locationscout;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by etern on 6/12/2017.
 */

public class Photo {
    private String url;
    private OkHttpClient httpClient = null;
    private DeferredObject dataHandle = null;

    public Photo(String url, OkHttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    private DeferredObject beginLoadingData(OkHttpClient httpClient, String url) {
        final DeferredObject handle = new DeferredObject();

        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handle.reject(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handle.resolve(response.body().bytes());
            }
        });

        return handle;
    }

    public Promise getData() {
        if (dataHandle == null) {
            dataHandle = beginLoadingData(httpClient, url);
        }

        return dataHandle.promise();
    }
}
