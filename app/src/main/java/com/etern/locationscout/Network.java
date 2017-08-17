package com.etern.locationscout;

import okhttp3.OkHttpClient;

/**
 * Created by etern on 8/13/2017.
 */

public final class Network {
    private OkHttpClient httpClient = new OkHttpClient();
    public OkHttpClient HttpClient() {
        return httpClient;
    }
}
