package com.kuanhsien.timemanagement.dml;

import java.util.List;

/**
 * Created by Ken on 2018/10/09.
 */
public interface GetTraceDailySummaryCallback {

    public void onCompleted(List<GetTraceDetail> bean);

    public void onError(String errorMessage);
}

