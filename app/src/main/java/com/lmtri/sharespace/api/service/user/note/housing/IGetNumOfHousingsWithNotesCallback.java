package com.lmtri.sharespace.api.service.user.note.housing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfHousingsWithNotesCallback {
    void onGetComplete(Integer numOfHousingsWithNotes);
    void onGetFailure(Throwable t);
}
