package com.lmtri.sharespace.helper.busevent.housing.note;

import com.lmtri.sharespace.api.model.HistoryHousingNote;

/**
 * Created by lmtri on 12/11/2017.
 */

public class DeleteHousingNoteEvent {
    private HistoryHousingNote HistoryHousingNote;

    public DeleteHousingNoteEvent(HistoryHousingNote historyHousingNote) {
        this.HistoryHousingNote = historyHousingNote;
    }

    public HistoryHousingNote getHistoryHousingNote() {
        return HistoryHousingNote;
    }
}
