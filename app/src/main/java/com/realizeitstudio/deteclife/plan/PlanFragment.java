package com.realizeitstudio.deteclife.plan;

import android.os.Bundle;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/9/29.
 *
 * A simple {@link android.app.Fragment} subclass.
 */
public class PlanFragment extends Fragment {
    private static final String MSG = "PlanFragment: ";

    private PlanChildFragment mPlanDailyFragment;
    private PlanChildPresenter mPlanDailyPresenter;

    private PlanChildFragment mPlanWeeklyFragment;
    private PlanChildPresenter mPlanWeeklyPresenter;

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;


    // [TODO] 可考慮是否要把 add plan 獨立放到另外一頁
    // selected task to add new plan
    private boolean isRefresh = true;


    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mPlanDailyFragment == null) {
            mPlanDailyFragment = PlanChildFragment.newInstance();
//            mPlanDailyFragment = new PlanChildFragment(); //PlanChildFragment.newInstance();
        }
        if (mPlanDailyPresenter == null) {
            mPlanDailyPresenter = new PlanChildPresenter(mPlanDailyFragment, Constants.TAB_DAILY_MODE);
        }

        if (mPlanWeeklyFragment == null) {
            mPlanWeeklyFragment = PlanChildFragment.newInstance();
//            mPlanWeeklyFragment = new PlanChildFragment(); //PlanChildFragment.newInstance();
        }
        if (mPlanWeeklyPresenter == null) {
            mPlanWeeklyPresenter = new PlanChildPresenter(mPlanWeeklyFragment, Constants.TAB_WEEKLY_MODE);
        }

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mPlanDailyFragment);
        mFragmentList.add(mPlanWeeklyFragment);

        View root = inflater.inflate(R.layout.fragment_plan, container, false);
        mTablayout = (TabLayout) root.findViewById(R.id.tab_plan_period);
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_DAILY));
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_WEEKLY));


        mViewPager = (ViewPager) root.findViewById(R.id.viewpager_plan_period);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mFragmentList.get(position);
            }
        });
        initTabListener();

        return root;
    }

    // listener for tab-bar and view-pager
    private void initTabListener() {
//        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTablayout));
        mTablayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = " + hidden);

        if (hidden) {  // 不在最前端介面顯示 (被 hide())
            ;
        } else {  //重新顯示到最前端 (被 show())
            Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = false => SHOW");

            if (isRefresh()) {

                mPlanDailyPresenter.start();
                mPlanDailyPresenter.refreshUi(Constants.MODE_PLAN_VIEW);
                mPlanWeeklyPresenter.start();
                mPlanWeeklyPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else {    // false 表示正在新增 task，有收到回傳的 task
                setRefresh(true);   // 事件已在 selectTaskToPlan 中完成，下次再進 onHiddenChanged 就要更新畫面
            }

        }
    }


    // 由 main presenter 傳入在 tasklistFragment 中選擇的 task
    public void selectTaskToPlan(GetCategoryTaskList bean) {

        setRefresh(false);

       if (mFragmentList.get(mTablayout.getSelectedTabPosition()).equals(mPlanDailyFragment)) {

            Logger.d(Constants.TAG, MSG + "selectTaskToPlan: mPlanDailyPresenter");
            mPlanDailyPresenter.selectTaskToPlan(bean);

        } else {

            Logger.d(Constants.TAG, MSG + "selectTaskToPlan: mPlanWeeklyPresenter");
            mPlanWeeklyPresenter.selectTaskToPlan(bean);
        }

        // 針對現在被選擇的 page 更新
    }


    // press backkey on category page (without choose any category)
    // task page shouldn't refresh
    public void backFromTask() {

        setRefresh(false);
        Logger.d(Constants.TAG, MSG + "backFromTask");
    }


    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
