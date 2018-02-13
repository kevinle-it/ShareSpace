package com.lmtri.sharespace.api.service.user.note.housing;

import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.model.Housing;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderHousingsWithNotesCallback {
    void onGetComplete(List<HistoryHousingNote> olderHousingsWithNotes);
    void onGetFailure(Throwable t);
}
