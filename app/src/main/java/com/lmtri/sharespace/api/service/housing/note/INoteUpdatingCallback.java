package com.lmtri.sharespace.api.service.housing.note;

import com.lmtri.sharespace.api.model.Note;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface INoteUpdatingCallback {
    void onUpdateComplete(Note note);
    void onUpdateFailure(Throwable t);
}
