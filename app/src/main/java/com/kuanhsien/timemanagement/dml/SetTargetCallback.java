package com.kuanhsien.timemanagement.dml;

import com.kuanhsien.timemanagement.object.TimePlanningTable;

import java.util.List;


/**
 * Created by Ken on 2018/9/25
 */
public interface SetTargetCallback {

    public void onCompleted(List<TimePlanningTable> bean);

    public void onError(String errorMessage);
}
