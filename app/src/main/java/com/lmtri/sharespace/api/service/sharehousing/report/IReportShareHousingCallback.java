package com.lmtri.sharespace.api.service.sharehousing.report;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IReportShareHousingCallback {
    void onReportComplete(Boolean isReported);
    void onReportFailure(Throwable t);
}
