package com.kuanhsien.timemanagement.dml;

import com.kuanhsien.timemanagement.object.CategoryDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/01
 */
public interface SetCategoryCallback {

    public void onCompleted(List<CategoryDefineTable> bean);

    public void onError(String errorMessage);
}
