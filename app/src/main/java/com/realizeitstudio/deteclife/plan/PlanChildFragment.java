package com.realizeitstudio.deteclife.plan;

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
public class PlanChildFragment extends Fragment implements PlanChildContract.View {

    private static final String MSG = "PlanChildFragment: ";

    private PlanChildContract.Presenter mPresenter;
    private PlanChildAdapter mPlanChildAdapter;
    private int mIntPlanMode;
    private int mIntChildMode;

    public PlanChildFragment() {
        // Required empty public constructor
    }

    public static PlanChildFragment newInstance() {
        return new PlanChildFragment();
    }

    @Override
    public void setPresenter(PlanChildContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] PlanChildFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mPlanChildAdapter = new PlanChildAdapter(new ArrayList<GetTaskWithPlanTime>(), mPresenter, (MainActivity)getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_plan_daily, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_plan_daily);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mPlanChildAdapter);
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
        mPlanChildAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntPlanMode(mode);
        mPlanChildAdapter.refreshUiMode(mode);
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

        mPlanChildAdapter.showCategoryTaskSelected(bean);
    }

    @Override
    public void showToast(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
