package com.lmtri.sharespace.api.service.user.note.sharehousing;

import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderShareHousingsWithNotesCallback {
    void onGetComplete(List<HistoryShareHousingNote> olderShareHousingsWithNotes);
    void onGetFailure(Throwable t);
}
