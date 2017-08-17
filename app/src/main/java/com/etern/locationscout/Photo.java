package com.etern.locationscout;

import android.os.Parcel;
import android.os.Parcelable;

import org.jdeferred.DoneCallback;
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

public class Photo implements Parcelable {
    private String url;
    private OkHttpClient httpClient = null;
    private DeferredObject dataHandle = null;

    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            String url = source.readString();
            Photo photo = new Photo(url, App.Network().HttpClient());

            int byteCount = source.readInt();
            if (byteCount > 0) {
                // If there is data already, resolve the data handle immediately.
                byte[] bytes = new byte[byteCount];
                source.readByteArray(bytes);

                photo.dataHandle = new DeferredObject();
                photo.dataHandle.resolve(bytes);
            }

            return photo;
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final Parcel temp = dest;

        temp.writeString(url);

        if (dataHandle != null && dataHandle.isResolved()) {
            dataHandle.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    byte[] bytes = (byte[])result;

                    temp.writeInt(bytes.length);
                    temp.writeByteArray(bytes);
                }
            });
        } else {
            temp.writeInt(0);
        }
    }
}
