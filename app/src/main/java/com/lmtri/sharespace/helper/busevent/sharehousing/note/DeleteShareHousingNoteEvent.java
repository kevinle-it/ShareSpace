package com.lmtri.sharespace.helper.busevent.sharehousing.note;

import com.lmtri.sharespace.api.model.HistoryShareHousingNote;

/**
 * Created by lmtri on 12/11/2017.
 */

public class DeleteShareHousingNoteEvent {
    private HistoryShareHousingNote HistoryShareHousingNote;

    public DeleteShareHousingNoteEvent(HistoryShareHousingNote historyShareHousingNote) {
        this.HistoryShareHousingNote = historyShareHousingNote;
    }

    public HistoryShareHousingNote getHistoryShareHousingNote() {
        return HistoryShareHousingNote;
    }
}
