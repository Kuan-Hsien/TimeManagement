package com.realizeitstudio.deteclife.category;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.MainContract;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListCallback;
import com.realizeitstudio.deteclife.dml.SetCategoryAsyncTask;
import com.realizeitstudio.deteclife.dml.SetCategoryCallback;
import com.realizeitstudio.deteclife.dml.SetTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTaskCallback;
import com.realizeitstudio.deteclife.object.CategoryDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.task.TaskListContract;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/19
 */
public class CategoryListPresenter implements CategoryListContract.Presenter {
    private static final String MSG = "CategoryListPresenter: ";

    private final CategoryListContract.View mCategoryView;
    private MainContract.Presenter mMainPresenter;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public CategoryListPresenter(CategoryListContract.View view,  MainContract.Presenter mainPresenter) {
        mCategoryView = checkNotNull(view, "categoryView cannot be null!");
        mCategoryView.setPresenter(this);
        mMainPresenter = mainPresenter;
    }

    @Override
    public void start() {
        getCategoryList();
    }


    // 0-1. recyclerView Scroll event
    @Override
    public void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState) {

        if (newState == RecyclerView.SCROLL_STATE_IDLE && visibleItemCount > 0) {

            if (mlastVisibleItemPosition == totalItemCount - 1) {
                Logger.d(Constants.TAG, MSG + "Scroll to bottom");

//                loadArticles();

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
        mCategoryView.refreshUi(mode);
    }

    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getCategoryList() {

        Logger.d(Constants.TAG, MSG + "getCategoryList");

        if (!isLoading()) {
            setLoading(true);

            new GetCategoryTaskListAsyncTask(new GetCategoryTaskListCallback() {

                @Override
                public void onCompleted(List<GetCategoryTaskList> bean) {

                    Logger.d(Constants.TAG, MSG + "GetCategoryTaskListAsyncTask.onCompleted");

                    // create a new task list (without category items, only show tasks to choose on record page)
                    List<GetCategoryTaskList> categoryLists = new ArrayList<>();
                    List<GetCategoryTaskList> taskLists = new ArrayList<>();

                    for (int i = 0 ; i < bean.size() ; ++i) {
                        if (Constants.ITEM_CATEGORY.equals(bean.get(i).getItemCatg())) {
                            categoryLists.add(bean.get(i));
                        }
                        if (Constants.ITEM_TASK.equals(bean.get(i).getItemCatg())) {
                            taskLists.add(bean.get(i));
                        }
                    }

                    setLoading(false);
                    showCategoryList(categoryLists);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, MSG + "GetCategoryTaskListAsyncTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }

    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showCategoryList(List<GetCategoryTaskList> bean) {
        mCategoryView.showCategoryList(bean);
    }

    @Override
    public void showCategorySelected(GetCategoryTaskList bean) {

//        mCategoryView.showCategorySelected(bean);
        Logger.d(Constants.TAG, MSG + "Select Category: ");
        bean.LogD();

        mMainPresenter.selectCategoryToTask(bean);
    }



    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
    @Override
    public void saveCategoryResults(List<CategoryDefineTable> targetList, List<CategoryDefineTable> deleteTargetList) {
//    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

        // insert time_planning_table
        new SetCategoryAsyncTask(targetList, deleteTargetList,  new SetCategoryCallback() {

            @Override
            public void onCompleted(List<CategoryDefineTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetCategory onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }

                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                // (1) 方法 1: 用 LiveData 更新
                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
                // *(3) 方法 3: [TODO] 把 CategoryDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetCategoryList 物件
//                getCategoryList();
                getCategoryList();
            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "SetCategory onError, errorMessage: " + errorMessage);

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


    @Override
    public void showCategoryListDialog() {
//        mCategoryView.showCategoryListDialog();
        Logger.d(Constants.TAG, MSG + "showCategoryListDialog: ");
    }

}
