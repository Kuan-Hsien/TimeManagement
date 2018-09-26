package com.kuanhsien.timemanagement.plan;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.TimeManagementApplication;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/9/24.
 *
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment implements PlanContract.View {

    private PlanContract.Presenter mPresenter;
    private PlanAdapter mPlanAdapter;
    private int mIntPlanMode;

    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }

    @Override
    public void setPresenter(PlanContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] PlanFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mPlanAdapter = new PlanAdapter(new ArrayList<GetTaskWithPlanTime>(), mPresenter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_plan, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_plan);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mPlanAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


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
    public void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean) {
        mPlanAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntPlanMode(mode);
        mPlanAdapter.refreshUiMode(mode);
    }


    @Override
    public void showSetTargetUi() {
        ((MainActivity) getActivity()).transToSetTarget();
    }


    public int getIntPlanMode() {
        return mIntPlanMode;
    }

    public void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }
}
