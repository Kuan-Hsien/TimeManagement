package com.realizeitstudio.deteclife.addtask;

import com.realizeitstudio.deteclife.MainContract;
import com.realizeitstudio.deteclife.dml.SetTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTaskCallback;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/14
 */
public class AddTaskPresenter implements AddTaskContract.Presenter {
    private static final String MSG = "AddTaskPresenter: ";

    private final AddTaskContract.View mTaskView;
    private MainContract.Presenter mMainPresenter;

    private boolean mLoading = false;


    public AddTaskPresenter(AddTaskContract.View mainView, MainContract.Presenter mainPresenter)  {
        mTaskView = checkNotNull(mainView, "taskView cannot be null!");
        mTaskView.setPresenter(this);
        mMainPresenter = mainPresenter;
    }


    @Override
    public void start() {
//        getCategoryTaskList();
    }

    // 2-1. [Send-to-Model] database insert to update data (insert new task or edit existed tasks)
    // 2-2. [Send-to-Model] database delete to delete data (delete existed tasks)
    @Override
    public void saveTaskResults(List<TaskDefineTable> targetList, List<TaskDefineTable> deleteTargetList) {
        if (!isLoading()) {
            setLoading(true);

            // insert time_planning_table
            new SetTaskAsyncTask(targetList, deleteTargetList, new SetTaskCallback() {

                @Override
                public void onCompleted(List<TaskDefineTable> bean) {

                    Logger.d(Constants.TAG, MSG + "AddTask onCompleted");
                    for (int i = 0; i < bean.size(); ++i) {
                        bean.get(i).LogD();
                    }

                    // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                    // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                    // (1) 方法 1: 用 LiveData 更新
                    // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
                    // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetCategoryTaskList 物件
//                    getCategoryTaskList();

                    setLoading(false);

                    mMainPresenter.addTaskComplete();
                }

                @Override
                public void onError(String errorMessage) {

                    setLoading(false);
                    Logger.d(Constants.TAG, MSG + "SetTask onError, errorMessage: " + errorMessage);

//                refreshCategoryTaskUi(Constants.MODE_PLAN_VIEW);
                }
            }).execute();
        }
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public void addTaskComplete() {
        mMainPresenter.addTaskComplete();
    }

//    @Override
//    public void showCategoryListDialog() {
//        mTaskView.showCategoryListDialog();
//    }

}
