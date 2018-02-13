package com.lmtri.sharespace.helper.busevent.housing.note;

import com.lmtri.sharespace.api.model.HistoryHousingNote;

/**
 * Created by lmtri on 12/11/2017.
 */

public class TakeHousingNoteEvent {
    private HistoryHousingNote HistoryHousingNote;

    public TakeHousingNoteEvent(HistoryHousingNote historyHousingNote) {
        this.HistoryHousingNote = historyHousingNote;
    }

    public HistoryHousingNote getHistoryHousingNote() {
        return HistoryHousingNote;
    }
}
