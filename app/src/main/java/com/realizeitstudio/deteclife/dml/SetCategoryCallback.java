package com.realizeitstudio.deteclife.dml;

import com.realizeitstudio.deteclife.object.CategoryDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/01
 */
public interface SetCategoryCallback {

    public void onCompleted(List<CategoryDefineTable> bean);

    public void onError(String errorMessage);
}
