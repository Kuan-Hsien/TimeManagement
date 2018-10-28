package com.realizeitstudio.deteclife.dml;

import com.realizeitstudio.deteclife.object.ColorDefineTable;
import java.util.List;

/**
 * Created by Ken on 2018/10/27.
 */
public interface GetColorListCallback {

    public void onCompleted(List<ColorDefineTable> bean);

    public void onError(String errorMessage);
}
