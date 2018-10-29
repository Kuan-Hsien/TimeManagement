package com.realizeitstudio.deteclife.colorpicker;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.dml.GetColorListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetColorListCallback;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;

public class ColorPickerPresenterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void getColorList() {

//        ColorPickerContract.View mView = mock(ColorPickerContract.View.class);
//        ColorPickerPresenter mPresenter = new ColorPickerPresenter(mView);
        ColorPickerPresenter mPresenter = mock(ColorPickerPresenter.class);


//        List<ColorDefineTable> bean = new ArrayList<>();
//        bean.add(new ColorDefineTable("#f44336", false, null));
//
//        DatabaseDao dao = Mockito.mock(DatabaseDao.class);
//        Mockito.when(dao.getAllColorList()).thenReturn(bean);




//        List<ColorDefineTable> bean = Mockito.mock(ArrayList.class);

//        GetColorListAsyncTask asyncTask = Mockito.mock(GetColorListAsyncTask.class);

//        Mockito.when(asyncTask.execute()).getMock();

        GetColorListCallback callback = Mockito.mock(GetColorListCallback.class);
//        callback.onCompleted(bean);
//        Mockito.when(callback).getMock();

//

        GetColorListAsyncTask asyncTask = new GetColorListAsyncTask(callback);
//        GetColorListAsyncTask asyncTask = new GetColorListAsyncTask(new GetColorListCallback() {
//
//            @Override
//            public void onCompleted(List<ColorDefineTable> bean) {
//                mPresenter.setLoading(false);
//                mPresenter.showColorList(bean);
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                mPresenter.setLoading(false);
//                Logger.e(Constants.TAG, "GetColorListAsyncTask.onError, errorMessage: " + errorMessage);
//            }
//        });



//        Mockito.when(mPresenter.isLoading()).thenReturn(false);

//        mPresenter.setLoading(false);
        mPresenter.getColorList();

        Mockito.verify(mPresenter, times(0)).isLoading();
        Mockito.verify(mPresenter, times(1)).getColorList();
        Mockito.verify(mPresenter, times(1)).setLoading(false);
//        Mockito.verify(mPresenter, times(1)).setLoading(false);
//        Mockito.verify(mPresenter, times(1)).showColorList(bean);

//        Assert.
//        Assert.assertEquals(password, null);
    }

    @Test
    public void showColorList() {

        ColorPickerContract.View mView = mock(ColorPickerContract.View.class);
        ColorPickerPresenter mPresenter = new ColorPickerPresenter(mView);

        List<ColorDefineTable> bean = new ArrayList<>();
        bean.add(new ColorDefineTable("#f44336", false, null));


        mPresenter.showColorList(bean);
        Mockito.verify(mView, times(1)).showColorList(bean);

        mPresenter.showColorList(null);
        Mockito.verify(mView, times(1)).showColorList(bean);
    }


//    @Test
//    public void showColorSelected() {
//    }
}