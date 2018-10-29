package com.realizeitstudio.deteclife.task;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/14.
 *
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment implements TaskListContract.View {

    private static final String MSG = "TaskListFragment: ";

    private TaskListContract.Presenter mPresenter;
    private TaskListAdapter mTaskListAdapter;
    private int mIntTaskMode;

    private boolean isRefresh = true;


    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    @Override
    public void setPresenter(TaskListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] TaskListFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mTaskListAdapter = new TaskListAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_tasklist);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mTaskListAdapter);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = " + hidden);

        if (hidden) {  // 不在最前端介面顯示 (被 hide())
            ;
        } else {  //重新顯示到最前端 (被 show())
            Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = false => SHOW");

            if (isRefresh()) {
                mPresenter.start();
                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else {    // false 表示正在新增 category，有收到回傳的 category
                setRefresh(true);   // 事件完成，下次再進 onHiddenChanged 就要更新畫面
            }
        }
    }

    @Override
    public void refreshUi(int mode) {
        mTaskListAdapter.refreshUiMode(mode);
    }

    @Override
    public void showTaskList(List<GetCategoryTaskList> bean) {
        mTaskListAdapter.updateData(bean);
    }


    // ****** Category Picker Dialog ****** //

    @Override
    public void showCategoryListDialog() {

        Logger.d(Constants.TAG, MSG + "showCategoryListDialog: ");
        ((MainActivity) getActivity()).transToCategoryList();

    }


    // 由 main presenter 傳入在 tasklistFragment 中選擇的 task
    public void completeSelectCategory(GetCategoryTaskList bean) {

        setRefresh(false);

        Logger.d(Constants.TAG, MSG + "completeSelectCategory => select category: ");
        bean.logD();

        mTaskListAdapter.completeSelectCategory(bean);
    }

    // press backkey on category page (without choose any category)
    // task page shouldn't refresh
    public void backFromCategory() {

        setRefresh(false);
        Logger.d(Constants.TAG, MSG + "backFromCategory");
    }

    // ****** Icon Picker Dialog ****** //
    //
    // 從 Fragment 叫起 Dialog 不需要經過 presenter
    // show Icon Picker Dialog
    public void showIconSelected(IconDefineTable bean) {

        mTaskListAdapter.showIconSelected(bean);
    }

    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }


    @Override
    public void showToast(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
