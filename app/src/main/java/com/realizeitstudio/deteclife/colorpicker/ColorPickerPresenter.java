package com.realizeitstudio.deteclife.colorpicker;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.dml.GetColorListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetColorListCallback;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/27
 */
public class ColorPickerPresenter implements ColorPickerContract.Presenter {
    private static final String MSG = "ColorPickerPresenter: ";

    private final ColorPickerContract.View mTaskView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;

    public ColorPickerPresenter(ColorPickerContract.View mainView) {
        mTaskView = checkNotNull(mainView, "ColorPickerView cannot be null!");
    }

    @Override
    public void start() {
        getColorList();
    }


    // 0-1. recyclerView Scroll event
    @Override
    public void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState) {

        if (newState == RecyclerView.SCROLL_STATE_IDLE && visibleItemCount > 0) {

            if (mlastVisibleItemPosition == totalItemCount - 1) {
                Logger.d(Constants.TAG, MSG + "Scroll to bottom");

            } else if (mfirstVisibleItemPosition == 0) {

                // Scroll to top
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof LinearLayoutManager) {

            mlastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            mfirstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();

        } else if (layoutManager instanceof GridLayoutManager) {

            mlastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            mfirstVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        }
    }

    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getColorList() {

        if (!isLoading()) {
            setLoading(true);

            new GetColorListAsyncTask(new GetColorListCallback() {

                @Override
                public void onCompleted(List<ColorDefineTable> bean) {
                    setLoading(false);
                    showColorList(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetColorListAsyncTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showColorList(List<ColorDefineTable> bean) {
        mTaskView.showColorList(bean);
    }

    @Override
    public void showColorSelected(ColorDefineTable bean) {

        Logger.d(Constants.TAG, MSG + "choose color: ");
        bean.logD();

        // call current task page's view
        mTaskView.showColorSelected(bean);
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }
}
