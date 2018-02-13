package com.lmtri.sharespace.api.service.housing.note;

import com.lmtri.sharespace.api.model.Note;

/**
 * Created by lmtri on 8/19/2017.
 */

public interface INoteGettingCallback {
    void onGetComplete(Note note);
    void onGetFailure(Throwable t);
}
