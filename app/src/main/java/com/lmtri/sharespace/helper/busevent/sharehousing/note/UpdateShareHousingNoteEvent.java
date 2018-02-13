package com.lmtri.sharespace.helper.busevent.sharehousing.note;

import com.lmtri.sharespace.api.model.HistoryShareHousingNote;

/**
 * Created by lmtri on 12/11/2017.
 */

public class UpdateShareHousingNoteEvent {
    private HistoryShareHousingNote HistoryShareHousingNote;

    public UpdateShareHousingNoteEvent(HistoryShareHousingNote historyShareHousingNote) {
        this.HistoryShareHousingNote = historyShareHousingNote;
    }

    public HistoryShareHousingNote getHistoryShareHousingNote() {
        return HistoryShareHousingNote;
    }
}
