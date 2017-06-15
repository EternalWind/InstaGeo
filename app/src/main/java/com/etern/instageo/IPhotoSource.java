package com.etern.instageo;

import java.util.List;

/**
 * Created by etern on 6/12/2017.
 */

public interface IPhotoSource {
    public interface IPhotoSearchListener {
        void onSucceed(List<Photo> photos);
    }

    void init();
    void searchByLocation(Geolocation location, final IPhotoSearchListener callback);
}
