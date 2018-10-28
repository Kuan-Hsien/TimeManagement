package com.realizeitstudio.deteclife.iconpicker;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.addtask.AddTaskPresenter;
import com.realizeitstudio.deteclife.dml.GetIconListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetIconListCallback;
import com.realizeitstudio.deteclife.dml.SetTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTaskCallback;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.task.TaskListPresenter;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/18
 */
public class IconPickerPresenter implements IconPickerContract.Presenter {
    private static final String MSG = "IconPickerPresenter: ";

    private final IconPickerContract.View mTaskView;

    private AddTaskPresenter mAddTaskPresenter;
    private TaskListPresenter mTaskListPresenter;
    private String mStrCurTaskPage = "";

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public IconPickerPresenter(IconPickerContract.View mainView, AddTaskPresenter addTaskPresenter) {
        mTaskView = checkNotNull(mainView, "IconPickerView cannot be null!");
        mTaskView.setPresenter(this);

        mAddTaskPresenter = addTaskPresenter;
        setStrCurTaskPage(Constants.PAGE_ADD_TASK);

    }

    public IconPickerPresenter(IconPickerContract.View mainView, TaskListPresenter taskListPresenter) {
        mTaskView = checkNotNull(mainView, "IconPickerView cannot be null!");
        mTaskView.setPresenter(this);

        mTaskListPresenter = taskListPresenter;
        setStrCurTaskPage(Constants.PAGE_TASK_LIST);

    }

    @Override
    public void start() {
        getIconList();
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
    public void getIconList() {
        if (!isLoading()) {
            setLoading(true);

            new GetIconListAsyncTask(new GetIconListCallback() {

                @Override
                public void onCompleted(List<IconDefineTable> bean) {
                    setLoading(false);
                    showIconList(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetIconListAsyncTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showIconList(List<IconDefineTable> bean) {
        mTaskView.showIconList(bean);
    }

    @Override
    public void showIconSelected(IconDefineTable bean) {

        mTaskView.closeDialog();   // call view to close dialog

        Logger.d(Constants.TAG, MSG + "choose icon: ");
        bean.logD();

        // call current task page's presenter
        if (Constants.PAGE_ADD_TASK.equals(getStrCurTaskPage())) {

            mAddTaskPresenter.showIconSelected(bean);

        } else {

            mTaskListPresenter.showIconSelected(bean);
        }
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public String getStrCurTaskPage() {
        return mStrCurTaskPage;
    }

    public void setStrCurTaskPage(String strCurTaskPage) {
        mStrCurTaskPage = strCurTaskPage;
    }
}
