package com.kuanhsien.timemanagement.dml;

import com.kuanhsien.timemanagement.object.TaskDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/01
 */
public interface SetTaskCallback {

    public void onCompleted(List<TaskDefineTable> bean);

    public void onError(String errorMessage);
}

