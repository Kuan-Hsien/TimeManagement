package com.realizeitstudio.deteclife.dml;

import java.util.List;


/**
 * Created by Ken on 2018/10/07
 */
public interface SetRecordCallback {

    public void onCompleted(List<GetTraceSummary> bean);

    public void onError(String errorMessage);
}