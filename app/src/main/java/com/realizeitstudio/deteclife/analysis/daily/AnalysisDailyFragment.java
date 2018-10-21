package com.realizeitstudio.deteclife.analysis.daily;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.task.CategoryTaskListAdapter;
import com.realizeitstudio.deteclife.task.CategoryTaskListContract;
import com.realizeitstudio.deteclife.task.CategoryTaskListPresenter;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/12
 *
 * A simple {@link Fragment} subclass.
 */
public class AnalysisDailyFragment extends Fragment implements AnalysisDailyContract.View, CategoryTaskListContract.View {

    private static final String MSG = "AnalysisDailyFragment: ";


    private LinearLayout mLinearLayoutPeriod;

    private CategoryTaskListContract.Presenter mCategroyTaskListContractPresenter;
    private CategoryTaskListAdapter mCategoryTaskListAdapter;
    private AlertDialog mDialog;

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
        mAnalysisDailyAdapter.updateData(bean);
    }


    @Override
    public void refreshUi(int mode) {
        setIntAnalysisMode(mode);
        mAnalysisDailyAdapter.refreshUiMode(mode);
    }


    @Override
    public void showSetTargetUi() {
        ((MainActivity) getActivity()).transToSetTarget();
    }


    public int getIntAnalysisMode() {
        return mIntAnalysisMode;
    }

    public void setIntAnalysisMode(int intAnalysisMode) {
        mIntAnalysisMode = intAnalysisMode;
    }




    @Override
    public void setCategoryTaskListPresenter(CategoryTaskListContract.Presenter presenter) {
        mCategroyTaskListContractPresenter = checkNotNull(presenter);
    }

    @Override
    public void showTaskListDialog() {

        // ****** 用預設的 mDialog 介面 ******
        final String[] list_String = {"1", "2", "3", "4", "5"};

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("標題");
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setItems(list_String, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface mDialog, int which) {    // 傳回的 which 表示點擊列表的第幾項
//                Toast.makeText(getActivity(), "點擊: " + list_String[which], Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        AlertDialog mDialog = builder.create();
//        mDialog.show();

        if (mCategroyTaskListContractPresenter == null) {
            mCategroyTaskListContractPresenter = new CategoryTaskListPresenter(this);
        }


        // ****** 用自定義的 mDialog 介面 ******
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_categorytask_list, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));

        mCategoryTaskListAdapter = new CategoryTaskListAdapter(new ArrayList<GetCategoryTaskList>(), mCategroyTaskListContractPresenter);
        recyclerView.setAdapter(mCategoryTaskListAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mCategroyTaskListContractPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mCategroyTaskListContractPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });

        mCategroyTaskListContractPresenter.start();

        builder.setView(view);
//        builder.setCancelable(true);
//        TextView title= (TextView) view
//                .findViewById(R.id.title);        // 設置標題
//        EditText input_edt= (EditText) view
//                .findViewById(R.id.dialog_edit);  // 輸入内容
//        Button btn_cancel=(Button)view
//                .findViewById(R.id.btn_cancel);   // 取消按鈕
//        Button btn_comfirm=(Button)view
//                .findViewById(R.id.btn_comfirm);  // 確定按鈕

        // 取消或確定按鈕監聽事件處理
        mDialog = builder.create();
        mDialog.show();
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);

    }

    @Override
    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
        mCategoryTaskListAdapter.updateData(bean);
    }

    @Override
    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
        mDialog.dismiss();

        Logger.d(Constants.TAG, MSG + "Category: " + bean.getCategoryName() + " Task: " + bean.getTaskName());
        mAnalysisDailyAdapter.showCategoryTaskSelected(bean);
    }

    @Override
    public void refreshCategoryTaskUi(int mode) {
        setIntTaskMode(mode);
        mCategoryTaskListAdapter.refreshUiMode(mode);
    }

    @Override
    public void showCategoryListDialog() {

    }

    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }




    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {

        mAnalysisDailyAdapter.updateCurrentTraceItem(bean);
    }



}

