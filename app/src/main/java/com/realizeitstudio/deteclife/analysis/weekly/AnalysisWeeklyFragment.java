package com.realizeitstudio.deteclife.analysis.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.object.TimeTracingTable;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/15
 *
 * A simple {@link Fragment} subclass.
 */
public class AnalysisWeeklyFragment extends Fragment implements AnalysisWeeklyContract.View {

    private static final String MSG = "AnalysisWeeklyFragment: ";

    private LinearLayout mLinearLayoutPeriod;

    private AnalysisWeeklyContract.Presenter mPresenter;
    private AnalysisWeeklyAdapter mAnalysisWeeklyAdapter;
    private int mIntAnalysisMode;

    public AnalysisWeeklyFragment() {
        // Required empty public constructor
    }

    public static AnalysisWeeklyFragment newInstance() {
        return new AnalysisWeeklyFragment();
    }

    @Override
    public void setPresenter(AnalysisWeeklyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] AnalysisWeeklyFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mAnalysisWeeklyAdapter = new AnalysisWeeklyAdapter(new ArrayList<GetResultDailySummary>(), mPresenter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_analysis_daily, container, false);

        TextView textview = root.findViewById(R.id.textview_analysis_period_daily);
        textview.setText("This Week");


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_analysis_daily);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mAnalysisWeeklyAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });


        // [TODO] delete this part
//        mLinearLayoutPeriod = root.findViewById(R.id.linearlayout_analysis_period_daily);
//        mLinearLayoutPeriod.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Logger.d(Constants.TAG, MSG + "Create a notification");
//
//            }   // end of onClick
//        });


        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showResultDailySummary(List<GetResultDailySummary> bean) {
        mAnalysisWeeklyAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntAnalysisMode(mode);
        mAnalysisWeeklyAdapter.refreshUiMode(mode);
    }

    public int getIntAnalysisMode() {
        return mIntAnalysisMode;
    }

    public void setIntAnalysisMode(int intAnalysisMode) {
        mIntAnalysisMode = intAnalysisMode;
    }

    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {

        mAnalysisWeeklyAdapter.updateCurrentTraceItem(bean);
    }


}
