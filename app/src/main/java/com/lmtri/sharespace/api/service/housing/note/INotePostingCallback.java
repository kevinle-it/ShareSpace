package com.lmtri.sharespace.api.service.housing.note;

import com.lmtri.sharespace.api.model.Note;

/**
 * Created by lmtri on 8/18/2017.
 */

public interface INotePostingCallback {
    void onPostComplete(Note note);
    void onPostFailure(Throwable t);
}
