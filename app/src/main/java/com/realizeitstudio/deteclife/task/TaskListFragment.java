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

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.task.TaskListAdapter;
import com.realizeitstudio.deteclife.task.TaskListContract;
import com.realizeitstudio.deteclife.task.TaskListFragment;
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
//public class TaskListFragment extends Fragment implements TaskListContract.View, CategoryTaskListContract.View {
public class TaskListFragment extends Fragment implements TaskListContract.View {

    private static final String MSG = "TaskListFragment: ";

    private CategoryTaskListContract.Presenter mCategroyTaskListContractPresenter;
    private CategoryTaskListAdapter mCategoryTaskListAdapter;
    private AlertDialog mDialog;

    private TaskListContract.Presenter mPresenter;
    private TaskListAdapter mTaskListAdapter;
    private int mIntPlanMode;
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
        setIntPlanMode(mode);
        mTaskListAdapter.refreshUiMode(mode);
    }

    @Override
    public void showTaskList(List<GetCategoryTaskList> bean) {
        mTaskListAdapter.updateData(bean);
    }

    @Override
    public void showTaskSelected(GetCategoryTaskList bean) {

//        Logger.d(Constants.TAG, MSG + "Select Task: ");
//        bean.logD();
//
//        // (1) 把選到的 category 傳回 (e.g. Plan page)
//        // (2) 把自己這頁關掉
//        mPresenter.show
//
//        Bundle bundle = new Bundle();
//        bundle.pu
//        bundle.putString("article_id", mArticlesList.get(mPosition).getId());
//        bundle.putString("article_author_id", mArticlesList.get(mPosition).getAuthor().getId());
//        bundle.putString("article_author_name", mArticlesList.get(mPosition).getAuthor().getName());
//        bundle.putString("article_author_image", mArticlesList.get(mPosition).getAuthor().getImage());
//        bundle.putString("article_title", mArticlesList.get(mPosition).getTitle());
//        bundle.putString("article_content", mArticlesList.get(mPosition).getContent());
//        bundle.putString("article_createdtime", mArticlesList.get(mPosition).getCreatedTime());
//        bundle.putString("article_place", mArticlesList.get(mPosition).getPlace());
//        // [TODO] picture need to send all the list item
//        bundle.putString("article_picture", mArticlesList.get(mPosition).getPictures().toString());
//        bundle.putInt("article_interests", mArticlesList.get(mPosition).getInterests());
//        bundle.putInt("article_interested_in", mArticlesList.get(mPosition).isInterestedIn() == true ? 1 : 0);
//
//        mMainActivity.getFragmentDetail().setArguments(bundle);
//        mMainActivity.displayArticleDetail();
//
//
//
//        mTaskListAdapter.showTaskSelected(bean);



    }


    // [TODO] 可刪
    public int getIntPlanMode() {
        return mIntPlanMode;
    }

    public void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }










//    @Override
//    public void setCategoryTaskListPresenter(CategoryTaskListContract.Presenter presenter) {
//        mCategroyTaskListContractPresenter = checkNotNull(presenter);
//    }

//    @Override
//    public void showTaskListDialog() {
//
//        // ****** 用預設的 mDialog 介面 ******
//        final String[] list_String = {"1", "2", "3", "4", "5"};
//
////        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////        builder.setTitle("標題");
////        builder.setIcon(R.mipmap.ic_launcher);
////        builder.setItems(list_String, new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface mDialog, int which) {    // 傳回的 which 表示點擊列表的第幾項
////                Toast.makeText(getActivity(), "點擊: " + list_String[which], Toast.LENGTH_SHORT).show();
////            }
////        });
////
////        AlertDialog mDialog = builder.create();
////        mDialog.show();
//
//        if (mCategroyTaskListContractPresenter == null) {
//            mCategroyTaskListContractPresenter = new CategoryTaskListPresenter(this);
//        }
//
//
//        // ****** 用自定義的 mDialog 介面 ******
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        View view = View.inflate(getActivity(), R.layout.dialog_categorytask_list, null);
//
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category_task_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
//
//        mCategoryTaskListAdapter = new CategoryTaskListAdapter(new ArrayList<GetCategoryTaskList>(), mCategroyTaskListContractPresenter);
//        recyclerView.setAdapter(mCategoryTaskListAdapter);
////        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));
//
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                mCategroyTaskListContractPresenter.onScrollStateChanged(
//                        recyclerView.getLayoutManager().getChildCount(),
//                        recyclerView.getLayoutManager().getItemCount(),
//                        newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                mCategroyTaskListContractPresenter.onScrolled(recyclerView.getLayoutManager());
//            }
//        });
//
//        mCategroyTaskListContractPresenter.start();
//
//        builder.setView(view);
////        builder.setCancelable(true);
////        TextView title= (TextView) view
////                .findViewById(R.id.title);        // 設置標題
////        EditText input_edt= (EditText) view
////                .findViewById(R.id.dialog_edit);  // 輸入内容
////        Button btn_cancel=(Button)view
////                .findViewById(R.id.btn_cancel);   // 取消按鈕
////        Button btn_comfirm=(Button)view
////                .findViewById(R.id.btn_comfirm);  // 確定按鈕
//
//        // 取消或確定按鈕監聽事件處理
//        mDialog = builder.create();
//        mDialog.show();
//        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
//
//    }

//    @Override
//    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
//        mCategoryTaskListAdapter.updateData(bean);
//    }

//    @Override
//    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
//        mDialog.dismiss();
//
//        Logger.d(Constants.TAG, MSG + "Category: " + bean.getCategoryName() + " Task: " + bean.getTaskName());
//        //$
//        //$
//        //$
//        //$
////        mTaskListAdapter.showCategoryTaskSelected(bean);    // 這是為了要把選到的 category 送回到 Plan page
//    }

//    @Override
//    public void refreshCategoryTaskUi(int mode) {
//        setIntTaskMode(mode);
//        mCategoryTaskListAdapter.refreshUiMode(mode);
//    }


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
}
