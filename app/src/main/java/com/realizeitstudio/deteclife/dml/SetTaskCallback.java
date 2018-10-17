package com.realizeitstudio.deteclife.dml;

import com.realizeitstudio.deteclife.object.TaskDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/01
 */
public interface SetTaskCallback {

    public void onCompleted(List<TaskDefineTable> bean);

    public void onError(String errorMessage);
}

