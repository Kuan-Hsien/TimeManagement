package com.kuanhsien.timemanagement;


import java.util.List;

/**
 * Created by Ken on 2018/9/24.
 */
public interface GetTaskWithPlanTimeCallback {

    public void onCompleted(List<GetTaskWithPlanTime> bean);

    public void onError(String errorMessage);
}
