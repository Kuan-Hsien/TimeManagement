package com.kuanhsien.timemanagement;

import java.util.List;

/**
 * Created by Ken on 2018/9/30.
 */
public interface GetCategoryTaskListCallback {

    public void onCompleted(List<GetCategoryTaskList> bean);

    public void onError(String errorMessage);
}
