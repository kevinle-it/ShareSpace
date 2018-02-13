package com.lmtri.sharespace.api.service.housing.photo;

import java.util.ArrayList;

/**
 * Created by lmtri on 8/19/2017.
 */

public interface IPhotoGettingCallback {
    void onGetComplete(ArrayList<String> photoURLs);
    void onGetFailure(Throwable t);
}
