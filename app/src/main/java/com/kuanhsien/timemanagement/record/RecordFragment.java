package com.kuanhsien.timemanagement.record;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;
import com.kuanhsien.timemanagement.utils.ParseTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/07.
 *
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements RecordContract.View, View.OnClickListener {

    private static final String MSG = "RecordFragment: ";

//    private CategoryTaskListContract.Presenter mCategroyTaskListContractPresenter;
//    private CategoryTaskListAdapter mCategoryTaskListAdapter;
//    private AlertDialog mDialog;

    private RecordContract.Presenter mPresenter;
    private RecordAdapter mRecordAdapter;
    private int mIntPlanMode;
    private int mIntTaskMode;

    private TextView mTextviewRecordCurrentTask;
    private TextView mTextviewRecordCurrentTime;
    private Button mButtonRecordLater;
    private Button mButtonRecordSummit;


    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    @Override
    public void setPresenter(RecordContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] RecordFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mRecordAdapter = new RecordAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_record, container, false);

        mTextviewRecordCurrentTask = root.findViewById(R.id.textview_record_current_task);
        mTextviewRecordCurrentTime = root.findViewById(R.id.textview_record_current_time);

        mButtonRecordLater = root.findViewById(R.id.button_record_later);
        mButtonRecordLater.setOnClickListener(this);

        mButtonRecordSummit = root.findViewById(R.id.button_record_view_statistics);
        mButtonRecordSummit.setOnClickListener(this);


        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_record_button);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Constants.RECORD_TASK_SPAN_COUNT));
        recyclerView.setAdapter(mRecordAdapter);
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
            mPresenter.start();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_record_later) {

            // user choose ask later
            showTraceUi();

        } else if (v.getId() == R.id.button_record_view_statistics) {

            // user click summit
            showTraceUi();

        } else {
            Logger.d(Constants.TAG, MSG + "Exception: enter else of onClick, view.getId is " + v.getId());
        }
    }


    @Override
    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
        mRecordAdapter.updateData(bean);
    }

    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {

//        mTextviewRecordCurrentTask.setText("Current Task: " + bean.getTaskName());
        mTextviewRecordCurrentTask.setText(bean.getTaskName());
        mTextviewRecordCurrentTime.setText("current task | since " + ParseTime.msToStr(bean.getStartTime())
                + " (" + ParseTime.msToHourMinDiff(bean.getStartTime(), new Date().getTime()) + ")" );  // hour + "hr " + min + "min"

        mRecordAdapter.updateCurrentTraceItem(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntPlanMode(mode);
        mRecordAdapter.refreshUiMode(mode);
    }



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
//    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
//        mDialog.dismiss();
//
//        Logger.d(Constants.TAG, MSG + "Category: " + bean.getCategoryName() + " Task: " + bean.getTaskName());
//        mRecordAdapter.showCategoryTaskSelected(bean);
//    }

//    @Override
//    public void refreshCategoryTaskUi(int mode) {
//        setIntTaskMode(mode);
//        mCategoryTaskListAdapter.refreshUiMode(mode);
//    }

//    @Override
//    public void showCategoryListDialog() {
//
//    }

    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }


    @Override
    public void showTraceUi() {
        ((MainActivity) getActivity()).transToAnalysis();
    }

}
