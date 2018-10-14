package com.kuanhsien.timemanagement.plan;


import android.os.Bundle;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.plan.daily.PlanDailyFragment;
import com.kuanhsien.timemanagement.plan.daily.PlanDailyPresenter;
import com.kuanhsien.timemanagement.plan.weekly.PlanWeeklyFragment;
import com.kuanhsien.timemanagement.plan.weekly.PlanWeeklyPresenter;
import com.kuanhsien.timemanagement.record.RecordFragment;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/9/29.
 *
 * A simple {@link android.app.Fragment} subclass.
 */
public class PlanFragment extends Fragment {
    private static final String MSG = "PlanFragment: ";


    private PlanDailyFragment mPlanDailyFragment;
    private PlanDailyPresenter mPlanDailyPresenter;

    private PlanWeeklyFragment mPlanWeeklyFragment;
    private PlanWeeklyPresenter mPlanWeeklyPresenter;

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

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
        View root = inflater.inflate(R.layout.fragment_plan, container, false);

        if (mPlanDailyFragment == null) {
            mPlanDailyFragment = PlanDailyFragment.newInstance();
        }
        if (mPlanDailyPresenter == null) {
            mPlanDailyPresenter = new PlanDailyPresenter(mPlanDailyFragment);
        }

        if (mPlanWeeklyFragment == null) {
            mPlanWeeklyFragment = mPlanWeeklyFragment.newInstance();
        }
        if (mPlanWeeklyPresenter == null) {
            mPlanWeeklyPresenter = new PlanWeeklyPresenter(mPlanWeeklyFragment);
        }


        mFragmentList = new ArrayList<>();
        mFragmentList.add(mPlanDailyFragment);
        mFragmentList.add(mPlanWeeklyFragment);
//        mFragmentList.add(mPlanDailyFragment);

        mTablayout = (TabLayout) root.findViewById(R.id.tab_plan_period);
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_DAILY));
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_WEEKLY));
//        mTablayout.addTab(mTablayout.newTab().setText("Page three"));

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

            mPlanDailyPresenter.start();
            mPlanWeeklyPresenter.start();
        }
    }

}
