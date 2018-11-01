package com.realizeitstudio.deteclife.record;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.util.ArrayList;
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

    private RecordContract.Presenter mPresenter;
    private RecordAdapter mRecordAdapter;
    private GridLayoutManager mGridLayoutManager;
    private int mIntPlanMode;
    private int mIntTaskMode;

    private TextView mTextviewRecordCurrentTask;
    private TextView mTextviewRecordCurrentTime;
    private TextView mTextviewRecordTimer;
    private Button mButtonRecordLater;
    private Button mButtonRecordSummit;


    private Handler mHandler = new Handler();

    private long mLongTimer;
    private String mStrTimer;


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
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Logger.d(Constants.TAG, MSG + "onCreateView");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_record, container, false);

        mTextviewRecordCurrentTask = root.findViewById(R.id.textview_record_current_task);
        mTextviewRecordCurrentTime = root.findViewById(R.id.textview_record_current_time);
        mTextviewRecordTimer = root.findViewById(R.id.textview_record_timer);

        mButtonRecordLater = root.findViewById(R.id.button_record_later);
        mButtonRecordLater.setOnClickListener(this);

        mButtonRecordSummit = root.findViewById(R.id.button_record_view_statistics);
        mButtonRecordSummit.setOnClickListener(this);


        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_record_button);

        mGridLayoutManager = new GridLayoutManager(getActivity(), Constants.RECORD_TASK_SPAN_COUNT);
        recyclerView.setLayoutManager(mGridLayoutManager);

//        mRecordAdapter = new RecordAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter, recyclerView.getWidth());
//        mRecordAdapter = new RecordAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter, ((MainActivity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics().widthPixels));

        Logger.d(Constants.TAG, MSG + "onCreateView recyclerView.getPaddingStart() = " + recyclerView.getPaddingStart());
        Logger.d(Constants.TAG, MSG + "onCreateView getScreenWidth() = " + getScreenWidth());

        mRecordAdapter = new RecordAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter, getScreenWidth() - 2 * recyclerView.getPaddingStart());
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
            showStatisticUi();

        } else if (v.getId() == R.id.button_record_view_statistics) {

            // user click summit
            showStatisticUi();

        } else {
            Logger.d(Constants.TAG, MSG + "Exception: enter else of onClick, view.getId is " + v.getId());
        }
    }


    @Override
    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
        mRecordAdapter.updateData(bean);
    }

    // get current tracing item
    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {

//        mTextviewRecordCurrentTask.setText("Current Task: " + bean.getTaskName());
        mTextviewRecordCurrentTask.setText(bean.getTaskName());
        mTextviewRecordCurrentTime.setText(ParseTime.msToHhmm(bean.getStartTime()));  // HH:mm

        mRecordAdapter.updateCurrentTraceItem(bean);


        // update timer on this page
        mLongTimer = bean.getStartTime();
        mHandler.postDelayed(runnable, 1000); // 開始 Timer
    }


    // Every 1 sec call updateTimer()
    private Runnable runnable = new Runnable() {
        public void run() {

            updateTimer();

            mHandler.postDelayed(this, 1000);
            // postDelayed(this,1000) 方法安排一個 Runnable 對象到主線程隊列中
        }
    };

    // update timer on record page
    private void updateTimer() {

        mStrTimer = ParseTime.msToHrMinSecDiff(mLongTimer, new Date().getTime());
        mTextviewRecordTimer.setText(mStrTimer);

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

    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }

    @Override
    public void showStatisticUi() {

        mHandler.removeCallbacks(runnable); // 停止 Timer

        ((MainActivity) getActivity()).transToAnalysis();
    }

    @Override
    public void showAddTaskUi() {
        ((MainActivity) getActivity()).transToAddTask();
    }


    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((MainActivity)getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 寬度 dm.widthPixels
        // 高度 dm.heightPixels
        return dm.widthPixels;
    }

}