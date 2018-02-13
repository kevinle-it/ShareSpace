package com.lmtri.sharespace.api.service.user.note.sharehousing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfShareHousingsWithNotesCallback {
    void onGetComplete(Integer numOfShareHousingsWithNotes);
    void onGetFailure(Throwable t);
}
