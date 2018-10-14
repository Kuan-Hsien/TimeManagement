package com.kuanhsien.timemanagement;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.StringDef;

import com.kuanhsien.timemanagement.addtask.AddTaskFragment;
import com.kuanhsien.timemanagement.addtask.AddTaskPresenter;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.plan.PlanFragment;
import com.kuanhsien.timemanagement.record.RecordFragment;
import com.kuanhsien.timemanagement.record.RecordPresenter;
import com.kuanhsien.timemanagement.settarget.SetTargetFragment;
import com.kuanhsien.timemanagement.settarget.SetTargetPresenter;
import com.kuanhsien.timemanagement.analysis.AnalysisFragment;
import com.kuanhsien.timemanagement.task.TaskListFragment;
import com.kuanhsien.timemanagement.task.TaskListPresenter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by Ken on 2018/9/23.
 */
public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mMainView;
    private FragmentManager mFragmentManager;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FRAGMENT_TAG_RECORD,
            FRAGMENT_TAG_PLAN,
            FRAGMENT_TAG_TRACE,
            FRAGMENT_TAG_ANALYSIS,
            FRAGMENT_TAG_TASK_LIST,
            FRAGMENT_TAG_ADD_TASK,
            FRAGMENT_TAG_SET_TARGET,
            FRAGMENT_TAG_PROFILE,
            FRAGMENT_TAG_DETAIL
    })

    public @interface FragmentType {}
    public static final String FRAGMENT_TAG_RECORD      = "FRAGMENT_TAG_RECORD";
    public static final String FRAGMENT_TAG_PLAN        = "FRAGMENT_TAG_PLAN";
    public static final String FRAGMENT_TAG_TRACE       = "FRAGMENT_TAG_TRACE";
    public static final String FRAGMENT_TAG_ANALYSIS    = "FRAGMENT_TAG_ANALYSIS";
    public static final String FRAGMENT_TAG_TASK_LIST   = "FRAGMENT_TAG_TASK_LIST";
    public static final String FRAGMENT_TAG_ADD_TASK    = "FRAGMENT_TAG_ADD_TASK";
    public static final String FRAGMENT_TAG_SET_TARGET  = "FRAGMENT_TAG_SET_TARGET";
    public static final String FRAGMENT_TAG_PROFILE     = "FRAGMENT_TAG_PROFILE";
    public static final String FRAGMENT_TAG_DETAIL      = "FRAGMENT_TAG_DETAIL";

    //Fragment and related presenter
    private RecordFragment mRecordFragment;
    private PlanFragment mPlanFragment;
    private AnalysisFragment mAnalysisFragment;
    private TaskListFragment mTaskListFragment;
    private AddTaskFragment mAddTaskFragment;

//    private AnalysisPresenter mAnalysisPresenter;
//    private PlanPresenter mPlanPresenter;
    private RecordPresenter mRecordPresenter;
    private TaskListPresenter mTaskListPresenter;
    private AddTaskPresenter mAddTaskPresenter;
    private SetTargetPresenter mSetTargetPresenter;


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

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mTaskListFragment == null) mTaskListFragment = TaskListFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mAddTaskFragment != null) transaction.hide(mAddTaskFragment);
        if (!mTaskListFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mTaskListFragment, FRAGMENT_TAG_TASK_LIST);
        } else {
            transaction.show(mTaskListFragment);
        }
        transaction.commit();

        if (mTaskListPresenter == null) {
            mTaskListPresenter = new TaskListPresenter(mTaskListFragment, this);
        }

        mMainView.showTaskListUi();
    }

    @Override
    public void transToAddTask() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mAddTaskFragment == null) mAddTaskFragment = AddTaskFragment.newInstance();
        if (mRecordFragment != null) transaction.hide(mRecordFragment);
        if (mPlanFragment != null) transaction.hide(mPlanFragment);
        if (mAnalysisFragment != null) transaction.hide(mAnalysisFragment);
        if (mTaskListFragment != null) transaction.hide(mTaskListFragment);
        if (!mAddTaskFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mAddTaskFragment, FRAGMENT_TAG_ADD_TASK);
        } else {
            transaction.show(mAddTaskFragment);
        }
        transaction.commit();

        if (mAddTaskPresenter == null) {
            mAddTaskPresenter = new AddTaskPresenter(mAddTaskFragment, this);
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
}
