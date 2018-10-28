package com.realizeitstudio.deteclife.plan.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/9/24.
 *
 * A simple {@link Fragment} subclass.
 */
public class PlanWeeklyFragment extends Fragment implements PlanWeeklyContract.View {

    private static final String MSG = "PlanWeeklyFragment: ";

    private PlanWeeklyContract.Presenter mPresenter;
    private PlanWeeklyAdapter mPlanWeeklyAdapter;
    private int mIntPlanMode;
    private int mIntTaskMode;

    public PlanWeeklyFragment() {
        // Required empty public constructor
    }

    public static PlanWeeklyFragment newInstance() {
        return new PlanWeeklyFragment();
    }

    @Override
    public void setPresenter(PlanWeeklyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] PlanWeeklyFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mPlanWeeklyAdapter = new PlanWeeklyAdapter(new ArrayList<GetTaskWithPlanTime>(), mPresenter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_plan_weekly, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_plan_weekly);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mPlanWeeklyAdapter);
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
    public void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean) {
        mPlanWeeklyAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntPlanMode(mode);
        mPlanWeeklyAdapter.refreshUiMode(mode);
    }

    public int getIntPlanMode() {
        return mIntPlanMode;
    }

    public void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }

    @Override
    public void showTaskListUi() {
        ((MainActivity) getActivity()).transToTaskList();
    }

    @Override
    public void showTaskSelected(GetCategoryTaskList bean) {

        Logger.d(Constants.TAG, MSG + "showTaskSelected: task: ");
        bean.logD();

        mPlanWeeklyAdapter.showCategoryTaskSelected(bean);
    }

    @Override
    public void showToast(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
