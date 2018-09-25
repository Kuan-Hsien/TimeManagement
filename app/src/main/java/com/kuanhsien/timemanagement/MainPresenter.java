package com.kuanhsien.timemanagement;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.StringDef;

import com.kuanhsien.timemanagement.plan.PlanFragment;
import com.kuanhsien.timemanagement.plan.PlanPresenter;
import com.kuanhsien.timemanagement.settarget.SetTargetFragment;
import com.kuanhsien.timemanagement.settarget.SetTargetPresenter;
import com.kuanhsien.timemanagement.trace.TraceFragment;

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
            FRAGMENT_TAG_TRACE,
            FRAGMENT_TAG_PLAN,
            FRAGMENT_TAG_SET_TARGET,
            FRAGMENT_TAG_PROFILE,
            FRAGMENT_TAG_DETAIL
    })

    public @interface FragmentType {}
    public static final String FRAGMENT_TAG_TRACE       = "FRAGMENT_TAG_TRACE";
    public static final String FRAGMENT_TAG_PLAN        = "FRAGMENT_TAG_PLAN";
    public static final String FRAGMENT_TAG_SET_TARGET  = "FRAGMENT_TAG_SET_TARGET";
    public static final String FRAGMENT_TAG_PROFILE     = "FRAGMENT_TAG_PROFILE";
    public static final String FRAGMENT_TAG_DETAIL      = "FRAGMENT_TAG_DETAIL";

    //Fragment and related presenter
    private TraceFragment mTraceFragment;
    private PlanFragment mPlanFragment;
//    private ProfileFragment mProfileFragment;

//    private TracePresenter mTracePresenter;
    private PlanPresenter mPlanPresenter;
//    private ProfilePresenter mProfilePresenter;
    private SetTargetPresenter mSetTargetPresenter;


    public MainPresenter(MainContract.View mainView, FragmentManager fragmentManager) {
        mMainView = checkNotNull(mainView, "Main-View(MainActivity) cannot be null!");
        mMainView.setPresenter(this);

        mFragmentManager = fragmentManager;
    }

    @Override
    public void transToPlan() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG_SET_TARGET) != null) mFragmentManager.popBackStack();
        if (mPlanFragment == null) mPlanFragment = PlanFragment.newInstance();
//        if (mTraceFragment != null) transaction.hide(mTraceFragment);
//        if (mProfileFragment != null) transaction.hide(mProfileFragment);
        if (!mPlanFragment.isAdded()) {
            transaction.add(R.id.linearlayout_main_container, mPlanFragment, "FRAGMENT_TAG_PLAN");
        } else {
            transaction.show(mPlanFragment);
        }
        transaction.commit();

        if (mPlanPresenter == null) {
            mPlanPresenter = new PlanPresenter(mPlanFragment);
        }

        mMainView.showPlanUi();
    }

    @Override
    public void transToSetTarget() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mPlanFragment != null && !mPlanFragment.isHidden()) {
            transaction.hide(mPlanFragment);
            transaction.addToBackStack(FRAGMENT_TAG_PLAN);
        }
//        if (mTraceFragment != null && !mTraceFragment.isHidden()) {
//            transaction.hide(mTraceFragment);
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
    public void start() {
        transToPlan();
    }
}
