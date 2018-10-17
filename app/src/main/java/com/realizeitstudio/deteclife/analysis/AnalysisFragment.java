package com.realizeitstudio.deteclife.analysis;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.analysis.daily.AnalysisDailyFragment;
import com.realizeitstudio.deteclife.analysis.daily.AnalysisDailyPresenter;
import com.realizeitstudio.deteclife.analysis.weekly.AnalysisWeeklyFragment;
import com.realizeitstudio.deteclife.analysis.weekly.AnalysisWeeklyPresenter;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ken on 2018/10/12.
 *
 * A simple {@link android.app.Fragment} subclass.
 */

public class AnalysisFragment extends Fragment {
    private static final String MSG = "AnalysisFragment: ";


    private AnalysisDailyFragment mAnalysisDailyFragment;
    private AnalysisDailyPresenter mAnalysisDailyPresenter;

    private AnalysisWeeklyFragment mAnalysisWeeklyFragment;
    private AnalysisWeeklyPresenter mAnalysisWeeklyPresenter;

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    public AnalysisFragment() {
        // Required empty public constructor
    }

    public static com.realizeitstudio.deteclife.analysis.AnalysisFragment newInstance() {
        return new com.realizeitstudio.deteclife.analysis.AnalysisFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        if (mAnalysisDailyFragment == null) {
            mAnalysisDailyFragment = AnalysisDailyFragment.newInstance();
        }
        if (mAnalysisDailyPresenter == null) {
            mAnalysisDailyPresenter = new AnalysisDailyPresenter(mAnalysisDailyFragment);
        }

        if (mAnalysisWeeklyFragment == null) {
            mAnalysisWeeklyFragment = mAnalysisWeeklyFragment.newInstance();
        }
        if (mAnalysisWeeklyPresenter == null) {
            mAnalysisWeeklyPresenter = new AnalysisWeeklyPresenter(mAnalysisWeeklyFragment);
        }


        mFragmentList = new ArrayList<>();
        mFragmentList.add(mAnalysisDailyFragment);
//        mFragmentList.add(mAnalysisDailyFragment);
//        mFragmentList.add(mAnalysisDailyFragment);
//        mFragmentList.add(mAnalysisDailyFragment);
        mFragmentList.add(mAnalysisWeeklyFragment);

        mTablayout = (TabLayout) root.findViewById(R.id.tab_analysis_period);
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_DAILY));
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_WEEKLY));
//        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_MONTHLY));
//        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_YEARLY));
//        mTablayout.addTab(mTablayout.newTab().setText("Page three"))

        mViewPager = (ViewPager) root.findViewById(R.id.viewpager_analysis_period);
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

            mAnalysisDailyPresenter.start();
            mAnalysisWeeklyPresenter.start();
        }
    }
}



