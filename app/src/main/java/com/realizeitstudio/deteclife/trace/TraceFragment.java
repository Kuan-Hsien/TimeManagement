package com.realizeitstudio.deteclife.trace;

import android.os.Bundle;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.trace.daily.TraceDailyFragment;
import com.realizeitstudio.deteclife.trace.daily.TraceDailyPresenter;
import com.realizeitstudio.deteclife.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/10/02.
 *
 * A simple {@link android.app.Fragment} subclass.
 */
public class TraceFragment extends Fragment {

    private TraceDailyFragment mTraceDailyFragment;
    private TraceDailyPresenter mTraceDailyPresenter;

//    private TraceWeeklyFragment mTraceWeeklyFragment;
//    private TraceWeeklyPresenter mTraceWeeklyPresenter;

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    public TraceFragment() {
        // Required empty public constructor
    }

    public static TraceFragment newInstance() {
        return new TraceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_trace, container, false);

        if (mTraceDailyFragment == null) {
            mTraceDailyFragment = TraceDailyFragment.newInstance();
        }
        if (mTraceDailyPresenter == null) {
            mTraceDailyPresenter = new TraceDailyPresenter(mTraceDailyFragment);
        }

//        if (mTraceWeeklyFragment == null) {
//            mTraceWeeklyFragment = mTraceWeeklyFragment.newInstance();
//        }
//        if (mTraceWeeklyPresenter == null) {
//            mTraceWeeklyPresenter = new TraceWeeklyPresenter(mTraceWeeklyFragment);
//        }


        mFragmentList = new ArrayList<>();
        mFragmentList.add(mTraceDailyFragment);
//        mFragmentList.add(mTraceDailyFragment);
//        mFragmentList.add(mTraceDailyFragment);
//        mFragmentList.add(mTraceDailyFragment);
//        mFragmentList.add(mTraceWeeklyFragment);

        mTablayout = (TabLayout) root.findViewById(R.id.tab_trace_period);
        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_DAILY));
//        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_WEEKLY));
//        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_MONTHLY));
//        mTablayout.addTab(mTablayout.newTab().setText(Constants.TAB_YEARLY));
//        mTablayout.addTab(mTablayout.newTab().setText("Page three"))

        mViewPager = (ViewPager) root.findViewById(R.id.viewpager_trace_period);
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
}