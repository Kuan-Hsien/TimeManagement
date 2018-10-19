package com.realizeitstudio.deteclife.dml;

import com.realizeitstudio.deteclife.object.IconDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/18.
 */
public interface GetIconListCallback {

    public void onCompleted(List<IconDefineTable> bean);

    public void onError(String errorMessage);
}
