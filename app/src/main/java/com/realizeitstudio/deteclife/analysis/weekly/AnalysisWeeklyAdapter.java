/*
 * Copyright 2018 Philipp Jahoda
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * */

package com.realizeitstudio.deteclife.analysis.weekly;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by Ken on 2018/10/15
 */
public class AnalysisWeeklyAdapter extends RecyclerView.Adapter {
    private static final String MSG = "AnalysisWeeklyAdapter: ";

    private AnalysisWeeklyContract.Presenter mPresenter;
    private List<GetResultDailySummary> mAnalysisningList;
    private boolean[] isDeleteArray;
    private int[] mIntAdjustCostTime;
    private int mIntMaxCostTime;
    private int mIntTotalCostTime;
    private int mIntAnalysisMode;
    private int mIntNewItemCostTime;

    private boolean isShowLegend;
    private long mLongTotalCostTime;


    private AnalysisTopItemViewHolder mAnalysisTopItemViewHolder;


    public AnalysisWeeklyAdapter(List<GetResultDailySummary> bean, AnalysisWeeklyContract.Presenter presenter) {

        mPresenter = presenter;
        setIntAnalysisMode(Constants.MODE_PLAN_VIEW);
        setShowLegend(false);

        mAnalysisningList = new ArrayList<>();



        for( int i = 0 ; i < bean.size() ; ++i ) {
            this.mAnalysisningList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);

        if (viewType == Constants.VIEWTYPE_TOP) {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis_top, parent, false);
            return new AnalysisTopItemViewHolder(view);

        } else {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis_main, parent, false);
            return new AnalysisMainItemViewHolder(view);

        }
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mAnalysisningList.get(position));

        if (holder instanceof AnalysisTopItemViewHolder) {
            // create a new target

            mAnalysisTopItemViewHolder = (AnalysisTopItemViewHolder) holder;
            ((AnalysisTopItemViewHolder) holder).bindView();


        } else {
            // current target list
            ((AnalysisMainItemViewHolder) holder).bindView(mAnalysisningList.get(position - 1), position - 1);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        if (mAnalysisningList.isEmpty()) {

            Logger.d(Constants.TAG, MSG + "getItemCount: " + "size is " + 1);
            return 1; // size of mAnalysisningList (add top item - "add a new analysis")
        } else {

            Logger.d(Constants.TAG, MSG + "getItemCount: " + "size is " + (mAnalysisningList.size() + 1));
            return mAnalysisningList.size() + 1; // size of mAnalysisningList (add top item - "add a new analysis")
        }
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_TOP : Constants.VIEWTYPE_NORMAL));
        return (position == 0) ? Constants.VIEWTYPE_TOP : Constants.VIEWTYPE_NORMAL;
    }

    public void updateData(List<GetResultDailySummary> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mAnalysisningList.clear();

        for (int i = 0 ; i < bean.size() ; ++i) {
            mAnalysisningList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }

    public void refreshUiMode(int mode) {
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "viewmode" : "editmode"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            int intArraySize = mAnalysisningList.size();

            // 1. [Delete] initialization
            isDeleteArray = null;
            isDeleteArray = new boolean[intArraySize];
            Arrays.fill(isDeleteArray, false);  // in Java, boolean array default set false to all items. this setting is only for reading

            // 2. [Edit] initialization
            // [TODO] add calendar mode Constants.MODE_CALENDAR
            // [TODO] add weekly mode
            mIntMaxCostTime = 24 * 60;
            mIntNewItemCostTime = 0;
            mIntAdjustCostTime = null;

            mIntAdjustCostTime = new int[intArraySize];
            for (int i = 0 ; i < intArraySize ; ++i) {
                mIntAdjustCostTime[i] = mAnalysisningList.get(i).getCostTime() / (60 * 1000);
            }

            Logger.d(Constants.TAG, MSG + "max-costTime: " + mIntMaxCostTime);
        }

        mIntTotalCostTime = 0;
        for (int i = 0 ; i < mAnalysisningList.size() ; ++i) {
            mIntTotalCostTime += mAnalysisningList.get(i).getCostTime() / (60 * 1000);
        }

        Logger.d(Constants.TAG, MSG + "total-costTime: " + mIntTotalCostTime);

        setIntAnalysisMode(mode);
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class AnalysisMainItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //** View mode
        private FrameLayout mFrameLayoutAnalysisTaskIcon;
        private FrameLayout mFrameLayoutAnalysisTaskDeleteHint;
        private ImageView mImageviewAnalysisTaskIcon;
        private TextView mTextviewAnalysisTaskName;
        private TextView mTextviewAnalysisCategoryName;
        private TextView mTextviewAnalysisTaskCostTime;
        private TextView mTextviewAnalysisTaskPlanTime;
        private ConstraintLayout mConstraintLayoutAnalysisMainItem;

        //** Edit mode
        private ImageView mImageviewAnalysisTaskDeleteHint;
        private SeekBar mSeekBarAnalysisTaskAdjustTime;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public FrameLayout getFrameLayoutAnalysisTaskIcon() {
            return mFrameLayoutAnalysisTaskIcon;
        }

        public FrameLayout getFrameLayoutAnalysisTaskDeleteHint() {
            return mFrameLayoutAnalysisTaskDeleteHint;
        }

        public ImageView getImageviewAnalysisTaskIcon() {
            return mImageviewAnalysisTaskIcon;
        }

        public TextView getTextviewAnalysisTaskName() {
            return mTextviewAnalysisTaskName;
        }

        public TextView getTextviewAnalysisTaskCostTime() {
            return mTextviewAnalysisTaskCostTime;
        }

        public TextView getTextviewAnalysisCategoryName() {
            return mTextviewAnalysisCategoryName;
        }

        public TextView getTextviewAnalysisTaskPlanTime() {
            return mTextviewAnalysisTaskPlanTime;
        }

        public ImageView getImageviewAnalysisTaskDeleteHint() {
            return mImageviewAnalysisTaskDeleteHint;
        }

        public SeekBar getSeekBarAnalysisTaskAdjustTime() {
            return mSeekBarAnalysisTaskAdjustTime;
        }

        public AnalysisMainItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mFrameLayoutAnalysisTaskIcon = v.findViewById(R.id.framelayout_analysis_task_icon);
            mImageviewAnalysisTaskIcon = v.findViewById(R.id.imageview_analysis_task_icon);
            mConstraintLayoutAnalysisMainItem = v.findViewById(R.id.constraintlayout_analysis_main_item);
            mTextviewAnalysisTaskName = v.findViewById(R.id.textview_analysis_task_name);
            mTextviewAnalysisCategoryName = v.findViewById(R.id.textview_analysis_category_name);
            mTextviewAnalysisTaskCostTime = v.findViewById(R.id.textview_analysis_task_cost_time);
            mTextviewAnalysisTaskPlanTime = v.findViewById(R.id.textview_analysis_task_plan_time);

//            mConstraintLayoutAnalysisMainItem.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    // 點擊一個已經設定好的目標，也許未來要可以編輯或是刪除等等
//                    // add a new target
//                }
//            });

            //** Edit mode
            mFrameLayoutAnalysisTaskDeleteHint = v.findViewById(R.id.framelayout_analysis_task_delete_hint);
            mImageviewAnalysisTaskDeleteHint = (ImageView) v.findViewById(R.id.imageview_analysis_task_delete_hint);
            mImageviewAnalysisTaskDeleteHint.setOnClickListener(this);
            // [TODO] 有可能需要改用 weekly 的 seekbar, 或是可以透過程式根據 daily-view 或 weekly-view 設定 max 大小
            mSeekBarAnalysisTaskAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_analysis_task_adjust_cost_time_daily);
            mSeekBarAnalysisTaskAdjustTime.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

        @Override
        public void onClick(View v) {

            // [TODO] change R.id.imageview_analysis_task_delete_hint to Delete button
            // 目前是把 delete 當作一個 checkbox 來用，先勾選並顯示在畫面上。並在按下 save 的時候一起刪掉
            if (v.getId() == R.id.imageview_analysis_task_delete_hint) {

                // if original delete flag is on, than cancel. (change background color to white)
                if (isDeleteArray[getCurrentPosition()] == true) {

                    isDeleteArray[getCurrentPosition()] = false;
                    mConstraintLayoutAnalysisMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(android.R.color.white));

                } else {
                    // if original delete flag is off, than delete. (change background color with drawable)

                    isDeleteArray[getCurrentPosition()] = true;
                    mConstraintLayoutAnalysisMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(R.drawable.toolbar_background));

                }

                Logger.d(Constants.TAG, MSG + "delete " + mAnalysisningList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);
            }
        }

        //** Seekbar
        private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
            //onProgressChanged  進度條只要拖動就觸發此事件（持續觸發）

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if ((mIntTotalCostTime - mIntAdjustCostTime[getCurrentPosition()] + progress) >= mIntMaxCostTime) { // 目前設定總時數 - 這個 item 原本的 costTime ＋ 這個 item 新調的時間 > 上限)

                    // [Seekbar] 1. 如果已經封頂了，user 還是把 progress 往後拉，則強制停在原地並 return。其餘數字均不改動
                    if (mIntMaxCostTime == mIntTotalCostTime) {
                        seekBar.setProgress(mIntAdjustCostTime[getCurrentPosition()]);
                        Logger.d(Constants.TAG, MSG + "Already meet the max-time, progress won't change");
                        Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                        return;
                    }

                    // [Seekbar] 2. 如果第一次被拉到封頂
                    progress = mIntAdjustCostTime[getCurrentPosition()] + mIntMaxCostTime - mIntTotalCostTime;         // 直接把 progress 設到滿
                    mIntTotalCostTime = mIntMaxCostTime;                    // 並把 totalCostTime 加到滿

                    mIntAdjustCostTime[getCurrentPosition()] = progress;    // progress means minutes
                    seekBar.setProgress(progress);

                    String strCostTime = ParseTime.intToHourMin(progress);
                    getTextviewAnalysisTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間

                    Logger.d(Constants.TAG, MSG + "Meet the max-time, reset progress to current maximum");
                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                    return;
                }


                // [Seekbar] 3. 沒封頂的情況

                String strCostTime = ParseTime.intToHourMin(progress);

                Logger.d(Constants.TAG, MSG + "Progress: " + progress + " CostTime: " + strCostTime);

                getTextviewAnalysisTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間
                mIntTotalCostTime = mIntTotalCostTime - mIntAdjustCostTime[getCurrentPosition()] + progress; // 總時數 - 這個 item 原本的 costTime ＋ 這個 item 新調的時間
                mIntAdjustCostTime[getCurrentPosition()] = progress;    // progress means minutes

                Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);

//                //取得當前SeekBar的值
//                seekR = background_r.getProgress();
//                seekG = background_g.getProgress();
//                seekB = background_b.getProgress();
//
//                //動態顯示目前所設定的顏色
//                changeBackground.setBackgroundColor(0xff000000 + seekR * 0x10000+ seekG * 0x100 + seekB);
//
//                //取得各RGB之值，為多少
//                background_rgb.setText("R:"+seekR+" G:"+seekG+" B:"+seekB);
            }

            //onStartTrackingTouch  進度條開始拖動就觸發此事件（僅一次觸發事件）
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //onStopTrackingTouch  拖動結束時則觸發此事件
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

        };

        /**
         * call by onBindViewHolder
         */
        public void bindView(GetResultDailySummary item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());

//            getFrameLayoutAnalysisTaskIcon().setBackgroundColor(Color.parseColor(item.getTaskColor()));
            GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutAnalysisTaskIcon().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getTaskColor()));

            getImageviewAnalysisTaskIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getTaskIcon()));
            getTextviewAnalysisCategoryName().setText(item.getCategoryName());
            getTextviewAnalysisTaskName().setText(item.getTaskName());
            getTextviewAnalysisTaskCostTime().setText(ParseTime.msToHourMin(item.getCostTime()));
            getTextviewAnalysisTaskPlanTime().setText(ParseTime.msToHourMin(item.getPlanTime()));

            setPosition(pos);

            if (getIntAnalysisMode() == Constants.MODE_PLAN_VIEW) {

                getFrameLayoutAnalysisTaskDeleteHint().setVisibility(View.GONE);
                getImageviewAnalysisTaskDeleteHint().setVisibility(View.GONE);
                getSeekBarAnalysisTaskAdjustTime().setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                getFrameLayoutAnalysisTaskDeleteHint().setVisibility(View.VISIBLE);
                getImageviewAnalysisTaskDeleteHint().setVisibility(View.VISIBLE);
                gradientDrawable = (GradientDrawable) getFrameLayoutAnalysisTaskDeleteHint().getBackground();
                gradientDrawable.setColor(Color.parseColor("#d9d9d9"));

                getSeekBarAnalysisTaskAdjustTime().setProgress(item.getCostTime() / (60 * 1000));
                getSeekBarAnalysisTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_IN);
//                getSeekBarAnalysisTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_ATOP); // 疑似也是改 thumb
                getSeekBarAnalysisTaskAdjustTime().getThumb().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.MULTIPLY);
            }
        }

    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class AnalysisTopItemViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {

        private PieChart mPieChart;
        private TextView mTextviewAnalysisTopItemTaskName;
        private TextView mTextviewAnalysisTopItemPercent;
        private TextView mTextviewAnalysisTopItemCostTime;
        private TextView mTextviewAnalysisTopItemPlaceholder;
        private ImageView mImageviewAnalysisTopItemPlaceholder;

        public TextView getTextviewAnalysisTopItemTaskName() {
            return mTextviewAnalysisTopItemTaskName;
        }

        public TextView getTextviewAnalysisTopItemPercent() {
            return mTextviewAnalysisTopItemPercent;
        }

        public TextView getTextviewAnalysisTopItemCostTime() {
            return mTextviewAnalysisTopItemCostTime;
        }

        public AnalysisTopItemViewHolder(View v) {
            super(v);

            // Get the piechart
            mPieChart = v.findViewById(R.id.piechart_analysis_topitem_trace);
            mPieChart.setTouchEnabled(true);
            mPieChart.setOnChartValueSelectedListener(this);

            // description of a slice of the piechart
            mTextviewAnalysisTopItemTaskName = v.findViewById(R.id.textview_analysis_topitem_taskname);
            mTextviewAnalysisTopItemCostTime = v.findViewById(R.id.textview_analysis_topitem_costtime);
            mTextviewAnalysisTopItemPercent = v.findViewById(R.id.textview_analysis_topitem_costtime_precent);

            // place-holder
            mTextviewAnalysisTopItemPlaceholder = v.findViewById(R.id.textview_analysis_topitem_trace_placeholder);
            mImageviewAnalysisTopItemPlaceholder = v.findViewById(R.id.imageview_analysis_topitem_trace_placeholder);
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView() {

            // 更新 pie chart 的內容
            setupPieChart();

            // 設定 pie chart 時會加總 mLongTotalCostTime，如果為 0 表示一比 trace 都沒有
            if (mLongTotalCostTime == 0) {

                mImageviewAnalysisTopItemPlaceholder.setVisibility(View.VISIBLE);
                mTextviewAnalysisTopItemPlaceholder.setVisibility(View.VISIBLE);

            } else {

                mImageviewAnalysisTopItemPlaceholder.setVisibility(View.GONE);
                mTextviewAnalysisTopItemPlaceholder.setVisibility(View.GONE);
            }

        }

        private void setupPieChart() {

            // 1. 準備資料
            // Populating a list of PieEntries
            mLongTotalCostTime = 0;
            List<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int i = 0 ; i < mAnalysisningList.size() ; ++i) {

                // pieEntries.add(new PieEntry(10f, "Green"));   // label is just a string
                pieEntries.add(new PieEntry(mAnalysisningList.get(i).getCostTime(), mAnalysisningList.get(i).getTaskName()));

                // [TODO] add plan piechart

                colors.add(Color.parseColor(mAnalysisningList.get(i).getTaskColor()));

                mLongTotalCostTime += mAnalysisningList.get(i).getCostTime();
            }


            // 2. 設定給餅狀圖所需資料相關屬性
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Statistic Results");

//            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);    // library 中預設的顏色
            pieDataSet.setColors(colors);   // 上面根據 icon 所設定好不同的顏色
            pieDataSet.setSliceSpace(2f);   // 每塊扇形之間的間距
            pieDataSet.setSelectionShift(7f);   // 每塊餅被選中時偏離中心的距離
            pieDataSet.setDrawValues(false);    // 餅上不要顯示數字比例 (不然餅塊太小的時候字會擠在一起)

            // 3. 餅圖資訊
            PieData pieData = new PieData(pieDataSet);

//            pieData.setDrawValues(false);   // 餅上不要顯示數字比例 (不然餅塊太小的時候字會擠在一起)
            pieData.setValueTextSize(0);
//            pieData.setValueFormatter(new PercentFormatter());
//            pieData.setValueTextSize(11f);
//            pieData.setValueTextColor(Color.DKGRAY);


            // 4. 繪圖
            mPieChart.setData(pieData);
            mPieChart.setTransparentCircleAlpha(24); // 設定中心透明圓形的透明度 (0-255)

            mPieChart.setDrawHoleEnabled(true);
            mPieChart.setHoleRadius(70f);
            mPieChart.setTransparentCircleRadius(100f); // 半透明圈半徑
            mPieChart.setDrawEntryLabels(false);    // 隱藏每塊餅圖上的文字 (不然餅塊太小的時候字會擠在一起)
            mPieChart.setDrawCenterText(true);  // 餅狀圖中間可加文字
//            mPieChart.setCenterText("55%");     // 設定文字內容
            mPieChart.setSelected(true);
//            mPieChart.setCenterTextColor();   // 設定文字顏色
            mPieChart.setCenterTextSize(50);    // 設定文字大小
            mPieChart.setNoDataText("請新增一筆紀錄或計畫");    // 沒有資料時顯示的文字內容
            mPieChart.setRotationEnabled(true); // 可以手動旋轉
            mPieChart.setUsePercentValues(true);    // 顯示百分比
            mPieChart.setDescription(null);     // 不要其他描述
            mPieChart.setHighlightPerTapEnabled(true);  // 選到的餅塊 highlight


            // 設定動畫
            mPieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
//            mPieChart.animateX(10000);
            mPieChart.invalidate(); // refresh draw chart


            // 設定是否要顯示餅狀圖每個顏色代表的內容，預設不要顯示，資訊乾淨一點
            Legend legend = mPieChart.getLegend();
            if (isShowLegend()) {
                legend.setEnabled(true);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);
                legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            } else {
                legend.setEnabled(false);
            }

        }   // end of setupPieChart


        // Pie chart 事件監聽
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            Logger.d(Constants.TAG, MSG + "Click Pie-chart, task (label): " + ((PieEntry) e).getLabel());   // 抓出被點擊項目的 Label (只有 PieEntry 有 Label)
            Logger.d(Constants.TAG, MSG + "Click Pie-chart, value: " + ((PieEntry) e).getValue());   // 抓出被點擊項目的 Value
            Logger.d(Constants.TAG, MSG + "Total cost time: " + mLongTotalCostTime);                // 現在所有 cost-time 總和


            // 被點擊到的扇形的 task name
            String strClickEntryLabel = ((PieEntry) e).getLabel();

            // 找出被點擊到的扇形是哪個 task
            for (int i = 0 ; i < mAnalysisningList.size() ; ++i) {

                if ( strClickEntryLabel.equals(mAnalysisningList.get(i).getTaskName()) ) {

                    Logger.d(Constants.TAG, MSG + "Click Pie-chart, costTime: " + mAnalysisningList.get(i).getCostTime());   // 抓出被點擊項目的花費時間
                    Logger.d(Constants.TAG, MSG + "Click Pie-chart, %: " + (double) mAnalysisningList.get(i).getCostTime() * 100/ (double) mLongTotalCostTime + "%");   // 抓出被點擊項目的花費時間

                    // 轉換為小數點下一位的百分比
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    String strPercent = decimalFormat.format((double) mAnalysisningList.get(i).getCostTime() * 100/ (double) mLongTotalCostTime) + "%";

//                    mPieChart.setCenterText(strPercent);
                    mTextviewAnalysisTopItemPercent.setText(strPercent);
                    mTextviewAnalysisTopItemTaskName.setText(strClickEntryLabel);
                    mTextviewAnalysisTopItemCostTime.setText(ParseTime.msToHrM(mAnalysisningList.get(i).getCostTime()));

                    break;
                }
            }
        }

        @Override
        public void onNothingSelected() {

        }
    }


    public int getIntAnalysisMode() {
        return mIntAnalysisMode;
    }

    public void setIntAnalysisMode(int intAnalysisMode) {
        mIntAnalysisMode = intAnalysisMode;
    }


    public void showCategoryTaskSelected(GetCategoryTaskList bean) {

//        mAnalysisTopItemViewHolder.getTextviewSetTargetCategory().setText(bean.getCategoryName());
//        mAnalysisTopItemViewHolder.getTextviewSetTargetTask().setText(bean.getTaskName());

    }

    public boolean isShowLegend() {
        return isShowLegend;
    }

    public void setShowLegend(boolean showLegend) {
        isShowLegend = showLegend;
    }
}