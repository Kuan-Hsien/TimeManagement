package com.realizeitstudio.deteclife;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.StringDef;

import com.realizeitstudio.deteclife.addtask.AddTaskFragment;
import com.realizeitstudio.deteclife.addtask.AddTaskPresenter;
import com.realizeitstudio.deteclife.category.CategoryListFragment;
import com.realizeitstudio.deteclife.category.CategoryListPresenter;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.plan.PlanFragment;
import com.realizeitstudio.deteclife.record.RecordFragment;
import com.realizeitstudio.deteclife.record.RecordPresenter;
import com.realizeitstudio.deteclife.settarget.SetTargetFragment;
import com.realizeitstudio.deteclife.settarget.SetTargetPresenter;
import com.realizeitstudio.deteclife.analysis.AnalysisFragment;
import com.realizeitstudio.deteclife.task.TaskListFragment;
import com.realizeitstudio.deteclife.task.TaskListPresenter;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by Ken on 2018/9/23.
 */
public class MainPresenter implements MainContract.Presenter {

    private static final String MSG = "MainPresenter:";

    private final MainContract.View mMainView;
    private FragmentManager mFragmentManager;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FRAGMENT_TAG_RECORD,
            FRAGMENT_TAG_PLAN,
            FRAGMENT_TAG_TRACE,
            FRAGMENT_TAG_ANALYSIS,
            FRAGMENT_TAG_TASK_LIST,
            FRAGMENT_TAG_CATEGORY_LIST,
            FRAGMENT_TAG_ADD_TASK,
            FRAGMENT_TAG_SET_TARGET,
            FRAGMENT_TAG_PROFILE,
            FRAGMENT_TAG_DETAIL
    })

    public @interface FragmentType {}
    public static final String FRAGMENT_TAG_RECORD          = "FRAGMENT_TAG_RECORD";
    public static final String FRAGMENT_TAG_PLAN            = "FRAGMENT_TAG_PLAN";
    public static final String FRAGMENT_TAG_TRACE           = "FRAGMENT_TAG_TRACE";
    public static final String FRAGMENT_TAG_ANALYSIS        = "FRAGMENT_TAG_ANALYSIS";
    public static final String FRAGMENT_TAG_TASK_LIST       = "FRAGMENT_TAG_TASK_LIST";
    public static final String FRAGMENT_TAG_CATEGORY_LIST   = "FRAGMENT_TAG_CATEGORY_LIST";
    public static final String FRAGMENT_TAG_ADD_TASK        = "FRAGMENT_TAG_ADD_TASK";
    public static final String FRAGMENT_TAG_SET_TARGET      = "FRAGMENT_TAG_SET_TARGET";
    public static final String FRAGMENT_TAG_PROFILE         = "FRAGMENT_TAG_PROFILE";
    public static final String FRAGMENT_TAG_DETAIL          = "FRAGMENT_TAG_DETAIL";

    //Fragment and related presenter
    private RecordFragment mRecordFragment;
    private PlanFragment mPlanFragment;
    private AnalysisFragment mAnalysisFragment;
    private TaskListFragment mTaskListFragment;
    private CategoryListFragment mCategoryListFragment;
    private AddTaskFragment mAddTaskFragment;


//    private AnalysisPresenter mAnalysisPresenter;
//    private PlanPresenter mPlanPresenter;
    private RecordPresenter mRecordPresenter;
    private TaskListPresenter mTaskListPresenter;
    private CategoryListPresenter mCategoryListPresenter;
    private AddTaskPresenter mAddTaskPresenter;
    private SetTargetPresenter mSetTargetPresenter;


    private String mStrCurTaskPage = "";




    public MainPresenter(MainContract.View mainView, FragmentManager fragmentManager) {
        mMainView = checkNotNull(mainView, "Main-View(MainActivity) cannot be null!");
        mMainView.setPresenter(this);

        mFragmentManager = fragmentManager;
    }

    @Override
    public void transToRecord() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mRecordFragment == null) mRecordFragment = RecordFragment.newInstance();
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mRecordFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mRecordFragment, FRAGMENT_TAG_RECORD);
        } else {
            transaction.show(mRecordFragment);
        }
        transaction.commit();

        if (mRecordPresenter == null) {
            mRecordPresenter = new RecordPresenter(mRecordFragment);
        }

        mMainView.showRecordUi();
    }

    @Override
    public void transToPlan() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mPlanFragment == null) mPlanFragment = PlanFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mPlanFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mPlanFragment, FRAGMENT_TAG_PLAN);
        } else {
            transaction.show(mPlanFragment);
        }
        transaction.commit();

//        if (mPlanPresenter == null) {
//            mPlanPresenter = new PlanDailyPresenter(mPlanFragment);
//        }

        mMainView.showPlanUi();
    }

    @Override
    public void transToAnalysis() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mAnalysisFragment == null) mAnalysisFragment = AnalysisFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mAnalysisFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mAnalysisFragment, FRAGMENT_TAG_ANALYSIS);
        } else {
            transaction.show(mAnalysisFragment);
        }
        transaction.commit();

//        if (mAnalysisPresenter == null) {
//            mAnalysisPresenter = new AnalysisDailyPresenter(mAnalysisFragment);
//        }

        mMainView.showAnalysisUi();
    }

    @Override
    public void transToTaskList() {

        mStrCurTaskPage = Constants.PAGE_TASK_LIST;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mTaskListFragment == null) mTaskListFragment = TaskListFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mTaskListFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mTaskListFragment, FRAGMENT_TAG_TASK_LIST);
        } else {
            transaction.show(mTaskListFragment);
        }
        transaction.commit();

        if (mTaskListPresenter == null) {
            mTaskListPresenter = new TaskListPresenter(mTaskListFragment, this, ((MainActivity) mMainView));
        }

        mMainView.showTaskListUi();
    }

    @Override
    public void transToCategoryList() {
        Logger.d(Constants.TAG, MSG + "transToCategoryList");

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mCategoryListFragment == null) mCategoryListFragment = CategoryListFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mCategoryListFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mCategoryListFragment, FRAGMENT_TAG_CATEGORY_LIST);
        } else {
            transaction.show(mCategoryListFragment);
        }
        transaction.commit();

        if (mCategoryListPresenter == null) {
            mCategoryListPresenter = new CategoryListPresenter(mCategoryListFragment, this);
        }

        mMainView.showCategoryListUi();
    }

    @Override
    public void transToAddTask() {

        setStrCurTaskPage(Constants.PAGE_ADD_TASK);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mAddTaskFragment == null) mAddTaskFragment = AddTaskFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        if (!mAddTaskFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mAddTaskFragment, FRAGMENT_TAG_ADD_TASK);
        } else {
            transaction.show(mAddTaskFragment);
        }
        transaction.commit();

        if (mAddTaskPresenter == null) {
            mAddTaskPresenter = new AddTaskPresenter(mAddTaskFragment, this, ((MainActivity) mMainView));
        }

        mMainView.showAddTaskUi();
    }

//    @Override
//    public void transToTaskList() {
//
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//
//        if (mRecordFragment != null && !mRecordFragment.isHidden()) {
//            transaction.hide(mRecordFragment);
//            transaction.addToBackStack(FRAGMENT_TAG_RECORD);
//        }
//
//        if (mPlanFragment != null && !mPlanFragment.isHidden()) {
//            transaction.hide(mPlanFragment);
//            transaction.addToBackStack(FRAGMENT_TAG_PLAN);
//        }
//
//        if (mAnalysisFragment != null && !mAnalysisFragment.isHidden()) {
//            transaction.hide(mAnalysisFragment);
//            transaction.addToBackStack(FRAGMENT_TAG_ANALYSIS);
//        }
//
//        TaskListFragment taskListFragment = TaskListFragment.newInstance();
//        transaction.add(R.id.linearlayout_main_container, taskListFragment, FRAGMENT_TAG_TASK_LIST);
//        transaction.commit();
//
//        mTaskListPresenter = new TaskListPresenter(taskListFragment);
//
//        mMainView.showTaskListUi();
//    }


    @Override
    public void transToSetTarget() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mPlanFragment != null && !mPlanFragment.isHidden()) {
            transaction.hide(mPlanFragment);
            transaction.addToBackStack(FRAGMENT_TAG_PLAN);
        }
//        if (mAnalysisFragment != null && !mAnalysisFragment.isHidden()) {
//            transaction.hide(mAnalysisFragment);
//            transaction.addToBackStack(FRAGMENT_TAG_TRACE);
//        }
//        if (mProfileFragment != null && !mProfileFragment.isHidden()) {
//            transaction.hide(mProfileFragment);
//            transaction.addToBackStack(FRAGMENT_TAG_PROFILE);
//        }
        SetTargetFragment setTargetFragment = SetTargetFragment.newInstance();
        transaction.add(R.id.linearlayout_main_container, setTargetFragment, FRAGMENT_TAG_SET_TARGET);
        transaction.commit();

        mSetTargetPresenter = new SetTargetPresenter(setTargetFragment);
    }


    @Override
    public boolean isFragmentRecordVisible() {

        if (mRecordFragment != null && mRecordFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFragmentPlanVisible() {

        if (mPlanFragment != null && mPlanFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFragmentAnalysisVisible() {

        if (mAnalysisFragment != null && mAnalysisFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFragmentTaskListVisible() {

        if (mTaskListFragment != null && mTaskListFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFragmentCategoryListVisible() {

        if (mCategoryListFragment != null && mCategoryListFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFragmentAddTaskVisible() {

        if (mAddTaskFragment != null && mAddTaskFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void selectTaskToPlan(GetCategoryTaskList bean) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mPlanFragment == null) mPlanFragment = PlanFragment.newInstance();

        // 先傳入剛剛選擇的 task
        mPlanFragment.selectTaskToPlan(bean);

        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (!mPlanFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mPlanFragment, FRAGMENT_TAG_PLAN);
        } else {
            transaction.show(mPlanFragment);
        }
        transaction.commit();

        mMainView.showPlanUi();
    }

    public void backTaskToPlan() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mPlanFragment == null) mPlanFragment = PlanFragment.newInstance();

        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (!mPlanFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mPlanFragment, FRAGMENT_TAG_PLAN);
        } else {
            transaction.show(mPlanFragment);
        }

        mPlanFragment.backFromTask();
        mMainView.showPlanUi();


        transaction.commit();
    }


    @Override
    public void selectCategoryToTask(GetCategoryTaskList bean) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);


        Logger.d(Constants.TAG, MSG + "selectCategoryToTask => current task: " + getStrCurTaskPage());

        // Back to task list page
        if (Constants.PAGE_TASK_LIST.equals(getStrCurTaskPage())) {

            if (!mTaskListFragment.isAdded()) {
                transaction.add(R.id.linearlayout_main_container, mTaskListFragment, FRAGMENT_TAG_TASK_LIST);
            } else {
                transaction.show(mTaskListFragment);
            }
            mTaskListFragment.completeSelectCategory(bean);

            mMainView.showTaskListUi();

        } else {    // Constants.PAGE_ADD_TASK.equals(getStrCurTaskPage())  // Back to add-task page

            if (!mAddTaskFragment.isAdded()) {
                transaction.add(R.id.linearlayout_main_container, mAddTaskFragment, FRAGMENT_TAG_ADD_TASK);
            } else {
                transaction.show(mAddTaskFragment);
            }
            mAddTaskFragment.completeSelectCategory(bean);


            mMainView.showAddTaskUi();
        }

        transaction.commit();
    }

    // press backkey on category page (without choose any category)
    public void backCategoryToTask() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mCategoryListFragment != null) transaction.hide(mCategoryListFragment);
        Logger.d(Constants.TAG, MSG + "backCategoryToTask => current task: " + getStrCurTaskPage());

        // Back to task list page
        if (Constants.PAGE_TASK_LIST.equals(getStrCurTaskPage())) {

            if (!mTaskListFragment.isAdded()) {
                transaction.add(R.id.linearlayout_main_container, mTaskListFragment, FRAGMENT_TAG_TASK_LIST);
            } else {
                transaction.show(mTaskListFragment);
            }

            // task page shouldn't refresh
            mTaskListFragment.backFromCategory();

            mMainView.showTaskListUi();

        } else {    // Constants.PAGE_ADD_TASK.equals(getStrCurTaskPage())  // Back to add-task page

            if (!mAddTaskFragment.isAdded()) {
                transaction.add(R.id.linearlayout_main_container, mAddTaskFragment, FRAGMENT_TAG_ADD_TASK);
            } else {
                transaction.show(mAddTaskFragment);
            }
            mAddTaskFragment.backFromCategory();


            mMainView.showAddTaskUi();
        }

        transaction.commit();
    }

    @Override
    public void addTaskComplete() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mRecordFragment == null) mRecordFragment = RecordFragment.newInstance();

//        mRecordPresenter.start();

        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mRecordFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mRecordFragment, FRAGMENT_TAG_RECORD);
        } else {
            transaction.show(mRecordFragment);  // 這時候會呼叫 RecordFragment 的 onHiddenChanged
        }
        transaction.commit();

        mMainView.showRecordUi();
    }

    @Override
    public void start() {

        transToRecord();
    }


    public String getStrCurTaskPage() {
        return mStrCurTaskPage;
    }

    public void setStrCurTaskPage(String strCurTaskPage) {
        mStrCurTaskPage = strCurTaskPage;
    }
}
