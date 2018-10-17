package com.realizeitstudio.deteclife.dml;

import com.realizeitstudio.deteclife.object.TimeTracingTable;


/**
 * Created by Ken on 2018/10/07.
 */
public interface GetCurrentTraceTaskCallback {

    public void onCompleted(TimeTracingTable bean);

    public void onError(String errorMessage);
}
