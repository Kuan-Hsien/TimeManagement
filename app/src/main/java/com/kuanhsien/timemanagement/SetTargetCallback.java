package com.kuanhsien.timemanagement;

import com.kuanhsien.timemanagement.object.TimePlanningTable;


/**
 * Created by Ken on 2018/9/25
 */
public interface SetTargetCallback {

    public void onCompleted(TimePlanningTable bean);

    public void onError(String errorMessage);
}
