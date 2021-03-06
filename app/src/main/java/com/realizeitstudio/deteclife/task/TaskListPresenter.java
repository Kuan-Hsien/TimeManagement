package com.realizeitstudio.deteclife.task;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.MainContract;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListCallback;
import com.realizeitstudio.deteclife.dml.SetTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTaskCallback;
import com.realizeitstudio.deteclife.iconpicker.IconPickerDialog;
import com.realizeitstudio.deteclife.iconpicker.IconPickerPresenter;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/14
 */
public class TaskListPresenter implements TaskListContract.Presenter {
    private static final String MSG = "TaskListPresenter: ";

    private final TaskListContract.View mTaskView;
    private MainContract.Presenter mMainPresenter;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    // Dialog: Icon Picker
    private IconPickerDialog mIconPickerDialog;
    private IconPickerPresenter mIconPickerPresenter;

    private MainActivity mMainActivity;



    public TaskListPresenter(TaskListContract.View view,  MainContract.Presenter mainPresenter, MainActivity mainActivity) {

        mTaskView = checkNotNull(view, "taskView cannot be null!");
        mTaskView.setPresenter(this);

        mMainPresenter = mainPresenter;
        mMainActivity = mainActivity;
    }

    @Override
    public void start() {
        getTaskList();
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

    // 0-2. [Send-to-View] request fragment to refresh adapter (base on mode (view or edit))

    @Override
    public void refreshUi(int mode) {
        mTaskView.refreshUi(mode);
    }

    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getTaskList() {
        if (!isLoading()) {
            setLoading(true);

            new GetCategoryTaskListAsyncTask(new GetCategoryTaskListCallback() {

                @Override
                public void onCompleted(List<GetCategoryTaskList> bean) {

                    // create a new task list (without category items, only show tasks to choose on record page)
                    List<GetCategoryTaskList> categoryLists = new ArrayList<>();
                    List<GetCategoryTaskList> taskLists = new ArrayList<>();

                    for (int i = 0; i < bean.size(); ++i) {
                        if (Constants.ITEM_CATEGORY.equals(bean.get(i).getItemCatg())) {
                            categoryLists.add(bean.get(i));
                        }
                        if (Constants.ITEM_TASK.equals(bean.get(i).getItemCatg())) {
                            taskLists.add(bean.get(i));
                        }
                    }

                    setLoading(false);
                    showTaskList(taskLists);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetCategoryTaskList.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }

    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showTaskList(List<GetCategoryTaskList> bean) {
        mTaskView.showTaskList(bean);
    }

    @Override
    public void showTaskSelected(GetCategoryTaskList bean) {

//        mTaskView.showTaskSelected(bean);
        Logger.d(Constants.TAG, MSG + "Select Task: ");
        bean.logD();

        mMainPresenter.selectTaskToPlan(bean);
    }



    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
    @Override
    public void saveTaskResults(List<TaskDefineTable> targetList, List<TaskDefineTable> deleteTargetList) {
//    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

        // insert time_planning_table
        new SetTaskAsyncTask(targetList, deleteTargetList,  new SetTaskCallback() {

            @Override
            public void onCompleted(List<TaskDefineTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetTask onCompleted");
                for (int i = 0; i < bean.size(); ++i) {
                    bean.get(i).logD();
                }

                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                // (1) 方法 1: 用 LiveData 更新
                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
                // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetCategoryTaskList 物件
//                getCategoryTaskList();
                getTaskList();
            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "SetTask onError, errorMessage: " + errorMessage);

                refreshUi(Constants.MODE_PLAN_VIEW);
            }
        }).execute();
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }


    // ****** Category Picker Dialog ****** //
    @Override
    public void showCategoryListDialog() {
        Logger.d(Constants.TAG, MSG + "showCategoryListDialog");
        mTaskView.showCategoryListDialog();
    }


    // ****** Icon Picker Dialog ****** //
    // show Icon Picker Dialog (call by adapter)
    @Override
    public void showIconPickerDialog(String strColor) {

        if (mIconPickerDialog == null) {
            mIconPickerDialog = new IconPickerDialog(mMainActivity, strColor);
        }

        if (mIconPickerPresenter == null) {
            mIconPickerPresenter = new IconPickerPresenter(mIconPickerDialog, this);
        }

        mIconPickerDialog.showDialog(strColor);

    }

    @Override
    public void showIconSelected(IconDefineTable bean) {

        mTaskView.showIconSelected(bean);
    }

    @Override
    public void showToast(String message) {

        mTaskView.showToast(message);
        Toast.makeText(mMainActivity, message, Toast.LENGTH_SHORT).show();
    }
}
