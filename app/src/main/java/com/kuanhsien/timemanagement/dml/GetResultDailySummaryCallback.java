package com.kuanhsien.timemanagement.dml;

import java.util.List;

public interface GetResultDailySummaryCallback {

    public void onCompleted(List<GetResultDailySummary> bean);

    public void onError(String errorMessage);
}
