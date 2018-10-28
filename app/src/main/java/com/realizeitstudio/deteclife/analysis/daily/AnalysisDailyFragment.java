package com.realizeitstudio.deteclife.analysis.daily;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/12
 *
 * A simple {@link Fragment} subclass.
 */
public class AnalysisDailyFragment extends Fragment implements AnalysisDailyContract.View {

    private static final String MSG = "AnalysisDailyFragment: ";

    private AnalysisDailyContract.Presenter mPresenter;
    private AnalysisDailyAdapter mAnalysisDailyAdapter;
    private int mIntAnalysisMode;
    private int mIntTaskMode;

    public AnalysisDailyFragment() {
        // Required empty public constructor
    }

    public static AnalysisDailyFragment newInstance() {
        return new AnalysisDailyFragment();
    }

    @Override
    public void setPresenter(AnalysisDailyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] AnalysisDailyFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mAnalysisDailyAdapter = new AnalysisDailyAdapter(new ArrayList<GetResultDailySummary>(), mPresenter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_analysis_daily, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_analysis_daily);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mAnalysisDailyAdapter);
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
        mAnalysisDailyAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntAnalysisMode(mode);
        mAnalysisDailyAdapter.refreshUiMode(mode);
    }

    public int getIntAnalysisMode() {
        return mIntAnalysisMode;
    }

    public void setIntAnalysisMode(int intAnalysisMode) {
        mIntAnalysisMode = intAnalysisMode;
    }

    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {

        mAnalysisDailyAdapter.updateCurrentTraceItem(bean);
    }

}

