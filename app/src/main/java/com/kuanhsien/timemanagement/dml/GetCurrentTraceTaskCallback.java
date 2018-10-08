package com.kuanhsien.timemanagement.dml;

import com.kuanhsien.timemanagement.object.TimeTracingTable;


/**
 * Created by Ken on 2018/10/07.
 */
public interface GetCurrentTraceTaskCallback {

    public void onCompleted(TimeTracingTable bean);

    public void onError(String errorMessage);
}
