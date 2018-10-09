package com.kuanhsien.timemanagement.dml;

import com.kuanhsien.timemanagement.object.TimeTracingTable;

import java.util.List;


/**
 * Created by Ken on 2018/10/07
 */
public interface SetRecordCallback {

    public void onCompleted(List<GetTraceSummary> bean);

    public void onError(String errorMessage);
}