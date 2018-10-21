package com.realizeitstudio.deteclife.plan.weekly;

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

import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.ParseTime;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/9/24
 */
public class PlanWeeklyAdapter extends RecyclerView.Adapter {
    private static final String MSG = "PlanWeeklyAdapter: ";

    private PlanWeeklyContract.Presenter mPresenter;
    private List<GetTaskWithPlanTime> mPlanningList;
    private boolean[] isDeleteArray;
    private int[] mIntAdjustCostTime;
    private int mIntMaxCostTime;
    private int mIntTotalCostTime;
    private int mIntPlanMode;
    private int mIntNewItemCostTime;

    private PlanTopItemViewHolder mPlanTopItemViewHolder;


    public PlanWeeklyAdapter(List<GetTaskWithPlanTime> bean, PlanWeeklyContract.Presenter presenter) {

        mPresenter = presenter;
        setIntPlanMode(Constants.MODE_PLAN_VIEW);
        mPlanningList = new ArrayList<>();

        for( int i = 0 ; i < bean.size() ; ++i ) {
            this.mPlanningList.add(bean.get(i));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_top, parent, false);
            return new PlanTopItemViewHolder(view);

        } else {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_main, parent, false);
            return new PlanMainItemViewHolder(view);

        }
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mPlanningList.get(position));

        if (holder instanceof PlanTopItemViewHolder) {
            // create a new target

            mPlanTopItemViewHolder = (PlanTopItemViewHolder) holder;
            ((PlanTopItemViewHolder) holder).bindView();


        } else {
            // current target list
            ((PlanMainItemViewHolder) holder).bindView(mPlanningList.get(position - 1), position - 1);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        if (mPlanningList.isEmpty()) {

            Logger.d(Constants.TAG, MSG + "getItemCount: " + "size is " + 1);
            return 1; // size of mPlanningList (add top item - "add a new plan")
        } else {

            Logger.d(Constants.TAG, MSG + "getItemCount: " + "size is " + (mPlanningList.size() + 1));
            return mPlanningList.size() + 1; // size of mPlanningList (add top item - "add a new plan")
        }
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_TOP : Constants.VIEWTYPE_NORMAL));
        return (position == 0) ? Constants.VIEWTYPE_TOP : Constants.VIEWTYPE_NORMAL;
    }

    public void updateData(List<GetTaskWithPlanTime> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mPlanningList.clear();

        for (int i = 0 ; i < bean.size() ; ++i) {
            mPlanningList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }

    public void refreshUiMode(int mode) {
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "viewmode" : "editmode"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            int intArraySize = mPlanningList.size();

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
                mIntAdjustCostTime[i] = mPlanningList.get(i).getCostTime() / (60*1000);;
            }

            Logger.d(Constants.TAG, MSG + "max-costTime: " + mIntMaxCostTime);
        }

        mIntTotalCostTime = 0;
        for (int i = 0 ; i < mPlanningList.size() ; ++i) {
            mIntTotalCostTime += mPlanningList.get(i).getCostTime() / (60*1000);;
        }

        Logger.d(Constants.TAG, MSG + "total-costTime: " + mIntTotalCostTime);

        setIntPlanMode(mode);
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanMainItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //** View mode
        private FrameLayout mFrameLayoutPlanTaskIcon;
        private ImageView mImageviewPlanTaskIcon;
        private TextView mTextviewPlanTaskName;
        private TextView mTextviewPlanCategoryName;
        private TextView mTextviewPlanTaskCostTime;
        private ConstraintLayout mConstraintLayoutPlanMainItem;

        //** Edit mode
        private FrameLayout mFrameLayoutPlanTaskDelete;
        private ImageView mImageviewPlanTaskDeleteHint;
        private SeekBar mSeekBarPlanTaskAdjustTime;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public FrameLayout getFrameLayoutPlanTaskIcon() {
            return mFrameLayoutPlanTaskIcon;
        }

        public ImageView getImageviewPlanTaskIcon() {
            return mImageviewPlanTaskIcon;
        }

        public FrameLayout getFrameLayoutPlanTaskDelete() {
            return mFrameLayoutPlanTaskDelete;
        }

        public TextView getTextviewPlanTaskName() {
            return mTextviewPlanTaskName;
        }

        public TextView getTextviewPlanCategoryName() {
            return mTextviewPlanCategoryName;
        }

        public TextView getTextviewPlanTaskCostTime() {
            return mTextviewPlanTaskCostTime;
        }

        public ImageView getImageviewPlanTaskDeleteHint() {
            return mImageviewPlanTaskDeleteHint;
        }

        public SeekBar getSeekBarPlanTaskAdjustTime() {
            return mSeekBarPlanTaskAdjustTime;
        }

        public PlanMainItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mFrameLayoutPlanTaskIcon = (FrameLayout) v.findViewById(R.id.framelayout_plan_task_icon);
            mImageviewPlanTaskIcon = (ImageView) v.findViewById(R.id.imageview_plan_task_icon);
            mConstraintLayoutPlanMainItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_main_item);
            mTextviewPlanCategoryName = v.findViewById(R.id.textview_plan_category_name);
            mTextviewPlanTaskName = (TextView) v.findViewById(R.id.textview_plan_task_name);
            mTextviewPlanTaskCostTime = (TextView) v.findViewById(R.id.textview_plan_task_cost_time);

//            mConstraintLayoutPlanMainItem.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    // 點擊一個已經設定好的目標，也許未來要可以編輯或是刪除等等
//                    // add a new target
//                }
//            });

            //** Edit mode
            mFrameLayoutPlanTaskDelete = v.findViewById(R.id.framelayout_plan_task_delete_hint);
            mImageviewPlanTaskDeleteHint = (ImageView) v.findViewById(R.id.imageview_plan_task_delete_hint);
            mImageviewPlanTaskDeleteHint.setOnClickListener(this);
            // [TODO] 有可能需要改用 weekly 的 seekbar, 或是可以透過程式根據 weekly-view 或 weekly-view 設定 max 大小
            mSeekBarPlanTaskAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_task_adjust_cost_time_weekly);
            mSeekBarPlanTaskAdjustTime.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

        @Override
        public void onClick(View v) {

            // [TODO] change R.id.imageview_plan_task_delete_hint to Delete button
            // 目前是把 delete 當作一個 checkbox 來用，先勾選並顯示在畫面上。並在按下 save 的時候一起刪掉
            if (v.getId() == R.id.imageview_plan_task_delete_hint) {

                // if original delete flag is on, than cancel. (change background color to white)
                if (isDeleteArray[getCurrentPosition()] == true) {

                    isDeleteArray[getCurrentPosition()] = false;
//                    mConstraintLayoutPlanMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(android.R.color.darker_gray));

                    GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                    gradientDrawable.setColor(Color.parseColor("#d9d9d9"));

                } else {
                    // if original delete flag is off, than delete. (change background color with drawable)

                    isDeleteArray[getCurrentPosition()] = true;
//                    mConstraintLayoutPlanMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(android.R.color.holo_red_light));


                    GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                    gradientDrawable.setColor(Color.parseColor("#f44336"));
                }

                Logger.d(Constants.TAG, MSG + "delete " + mPlanningList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);
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
                    getTextviewPlanTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間

                    Logger.d(Constants.TAG, MSG + "Meet the max-time, reset progress to current maximum");
                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                    return;
                }


                // [Seekbar] 3. 沒封頂的情況

                String strCostTime = ParseTime.intToHourMin(progress);

                Logger.d(Constants.TAG, MSG + "Progress: " + progress + " CostTime: " + strCostTime);

                getTextviewPlanTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間
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
        public void bindView(GetTaskWithPlanTime item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());

//            getFrameLayoutPlanTaskIcon().setBackgroundColor(Color.parseColor(item.getTaskColor()));
            GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskIcon().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getTaskColor()));

            gradientDrawable = (GradientDrawable) getTextviewPlanCategoryName().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getCategoryColor()));


            getImageviewPlanTaskIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getTaskIcon()));
            getImageviewPlanTaskIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
            getTextviewPlanTaskName().setText(item.getTaskName());
            getTextviewPlanCategoryName().setText(item.getCategoryName());
            getTextviewPlanTaskCostTime().setText(ParseTime.msToHourMin(item.getCostTime()));

            setPosition(pos);

            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {

                getFrameLayoutPlanTaskDelete().setVisibility(View.GONE);
                getImageviewPlanTaskDeleteHint().setVisibility(View.GONE);
                getSeekBarPlanTaskAdjustTime().setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                getFrameLayoutPlanTaskDelete().setVisibility(View.VISIBLE);
                getImageviewPlanTaskDeleteHint().setVisibility(View.VISIBLE);
                getSeekBarPlanTaskAdjustTime().setVisibility(View.VISIBLE);

                gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                gradientDrawable.setColor(Color.parseColor("#d9d9d9"));

                getSeekBarPlanTaskAdjustTime().setProgress(item.getCostTime() / (60 * 1000));
                getSeekBarPlanTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_IN);
//                getSeekBarPlanTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_ATOP); // 疑似也是改 thumb
                getSeekBarPlanTaskAdjustTime().getThumb().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.MULTIPLY);
            }
        }

    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanTopItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //** View Mode
        private ConstraintLayout mConstraintLayoutPlanTopItem;
        private TextView mTextviewPlanTopRemainingTime;

        //** Edit Mode
        private ConstraintLayout mConstraintLayoutPlanSetTarget;
        private TextView mTextviewSetTargetCategory;
        private TextView mTextviewSetTargetTask;
        private TextView mTextviewSetTargetCostTime;
        private SeekBar mSeekBarSetTargetAdjustTime;

        public TextView getTextviewPlanTopRemainingTime() {
            return mTextviewPlanTopRemainingTime;
        }

        public ConstraintLayout getConstraintLayoutPlanTopItem() {
            return mConstraintLayoutPlanTopItem;
        }

        public ConstraintLayout getConstraintLayoutPlanSetTarget() {
            return mConstraintLayoutPlanSetTarget;
        }

        public TextView getTextviewSetTargetCategory() {
            return mTextviewSetTargetCategory;
        }

        public TextView getTextviewSetTargetTask() {
            return mTextviewSetTargetTask;
        }

        public TextView getTextviewSetTargetCostTime() {
            return mTextviewSetTargetCostTime;
        }

        public SeekBar getSeekBarSetTargetAdjustTime() {
            return mSeekBarSetTargetAdjustTime;
        }

        public PlanTopItemViewHolder(View v) {
            super(v);

            //** View Mode
            mTextviewPlanTopRemainingTime = (TextView) v.findViewById(R.id.textview_plan_top_remaining_time);
            mConstraintLayoutPlanTopItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_top_viewmode);
            mConstraintLayoutPlanTopItem.setOnClickListener(this);

            //** Edit Mode
            // Set Category
            mTextviewSetTargetCategory = (TextView) v.findViewById(R.id.textview_plan_top_editmode_category);

            // Set Task
            mTextviewSetTargetTask = (TextView) v.findViewById(R.id.edittext_plan_top_editmode_task);
            mTextviewSetTargetTask.setOnClickListener(this);

            mTextviewSetTargetCostTime = (TextView) v.findViewById(R.id.textview_plan_set_target_cost_time);
            mConstraintLayoutPlanSetTarget = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_top_editmode);
            mSeekBarSetTargetAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_set_target_cost_time_weekly);

            ((ImageView) v.findViewById(R.id.imageview_plan_top_editmode_save)).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.imageview_plan_top_editmode_cancel)).setOnClickListener(this);

            mSeekBarSetTargetAdjustTime.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_plan_top_viewmode) {    // View mode

                // Plan page 整頁切換為編輯模式
                ;
//                getTextviewSetTargetTask().setText("Choose a task");
//                getTextviewSetTargetCategory().setText("--");
//                getTextviewSetTargetCostTime().setText("0 min");
//                getSeekBarSetTargetAdjustTime().setProgress(0);
//                mIntNewItemCostTime = 0;
//
//                mPresenter.refreshUi(Constants.MODE_PLAN_EDIT);

                // [TODO] 之後要增加一頁新的 category 可參考此處寫法
                // mPresenter.showSetTargetUi();

            } else if (v.getId() == R.id.imageview_plan_top_editmode_save) {  // Edit mode - complete

                // [TODO] 未來可以一次新增多個 target (多加一個小打勾，像 trello 新增卡片)
                // [TODO] 換成真正的 startTime, endTime
                // 1. 取得現在時間
                // 1.1 做成 startTime, endTime
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strStartTime = simpleDateFormat.format(curDate);
                String strEndTime = Constants.DB_ENDLESS_DATE;

                // 1.2 update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);

                // 2. 新增兩個 List 以 (1) 存放要存回 database 的資料 (2) 要從 database 刪除的資料
                List<TimePlanningTable> targetList = new ArrayList<>();
                List<TimePlanningTable> deleteTargetList = new ArrayList<>();

                // 2.1 先針對現有所有目標清單做出 TimePlanning Table 物件
                for (int i = 0 ; i < mPlanningList.size() ; ++i) {

                    // if user decides to delete this item, then delete from database
                    if (isDeleteArray[i] == true) {

                        deleteTargetList.add(new TimePlanningTable(mPlanningList.get(i).getMode(),
                                mPlanningList.get(i).getCategoryName(),
                                mPlanningList.get(i).getTaskName(),
                                mPlanningList.get(i).getStartTime(),
                                mPlanningList.get(i).getEndTime(),
                                mPlanningList.get(i).getCostTime(),
                                strCurrentTime));

                        Logger.d(Constants.TAG, MSG + "Delete item: ");

                    } else {
                        // else add in database

                        targetList.add(new TimePlanningTable(mPlanningList.get(i).getMode(),
                                mPlanningList.get(i).getCategoryName(),
                                mPlanningList.get(i).getTaskName(),
                                mPlanningList.get(i).getStartTime(),
                                mPlanningList.get(i).getEndTime(),
                                mIntAdjustCostTime[i] * 60 * 1000, // origin: mPlanningList.get(i).getCostTime(),
                                strCurrentTime));

                        Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
                    }

                    Logger.d(Constants.TAG, MSG +
                            "Categroy: " + mPlanningList.get(i).getCategoryName() +
                            " TaskName: " + mPlanningList.get(i).getTaskName() +
                            " CostTime: " + mPlanningList.get(i).getCostTime());
                }

                // 2.2 再把最新 add 的目標加在最後
                // [TODO] 此處需判斷每個字串是否為空，還有對輸入的時間做檢查
                if (getTextviewSetTargetCategory().getText().toString().trim() != null &&
                        getTextviewSetTargetTask().getText().toString().trim() != null &&
                        strStartTime != null &&
                        strStartTime != null) {

                    targetList.add(new TimePlanningTable(
                            Constants.MODE_WEEKLY,
                            getTextviewSetTargetCategory().getText().toString().trim(),
                            getTextviewSetTargetTask().getText().toString().trim(),
                            strStartTime,
                            strEndTime,
                            (long)(mIntNewItemCostTime * 60 * 1000),
                            strCurrentTime
                    ));
                }

                // 3. send asyncTask to update data
                mPresenter.saveTargetResults(targetList, deleteTargetList);

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.imageview_plan_top_editmode_cancel) { // Edit mode - cancel

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.edittext_plan_top_editmode_task) {

                mPresenter.showTaskListDialog();
            }
        }

        //** Seekbar
        private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
            //onProgressChanged  進度條只要拖動就觸發此事件（持續觸發）

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if ((mIntTotalCostTime - mIntNewItemCostTime + progress) > mIntMaxCostTime) { // 目前設定總時數 - 這個 item 原本的設定的 costTime ＋ 這個 item 新調的時間 > 上限)

                    // [Seekbar] 1. 如果已經封頂了，user 還是把 progress 往後拉，則強制停在原地並 return。其餘數字均不改動
                    if (mIntMaxCostTime == mIntTotalCostTime) {
                        seekBar.setProgress(mIntNewItemCostTime);
                        Logger.d(Constants.TAG, MSG + "Already meet the max-time, progress won't change");
                        Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                        return;
                    }

                    // [Seekbar] 2. 如果第一次被拉到封頂
                    progress = mIntNewItemCostTime + mIntMaxCostTime - mIntTotalCostTime;         // 直接把 progress 設到滿
                    mIntTotalCostTime = mIntMaxCostTime;                    // 並把 totalCostTime 加到滿

                    mIntNewItemCostTime = progress;    // progress means minutes
                    seekBar.setProgress(progress);

                    String strCostTime = ParseTime.intToHourMin(progress);
                    getTextviewSetTargetCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間

                    Logger.d(Constants.TAG, MSG + "Meet the max-time, reset progress to current maximum");
                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                    return;
                }

                // [Seekbar] 3. 沒封頂的情況 `
                String strCostTime = ParseTime.intToHourMin(progress);

                Logger.d(Constants.TAG, MSG + "Progress: " + progress + " CostTime: " + strCostTime);

                getTextviewSetTargetCostTime().setText(strCostTime);
                mIntTotalCostTime = mIntTotalCostTime - mIntNewItemCostTime + progress; // 總時數 - 這個 item 原本的 costTime ＋ 這個 item 新調的時間
                mIntNewItemCostTime = progress; // progress means minutes

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
        public void bindView() {
            // [TODO] 計算剩下幾個小時並顯示在畫面上
//            getTextviewCategoryName().setText("顯示剩多少小時");

            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {

                mConstraintLayoutPlanTopItem.setVisibility(View.VISIBLE);
                mConstraintLayoutPlanSetTarget.setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                mConstraintLayoutPlanTopItem.setVisibility(View.GONE);
                mConstraintLayoutPlanSetTarget.setVisibility(View.VISIBLE);
            }
        }
    }


    public int getIntPlanMode() {
        return mIntPlanMode;
    }

    public void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }


    public void showCategoryTaskSelected(GetCategoryTaskList bean) {

        mPlanTopItemViewHolder.getTextviewSetTargetCategory().setText(bean.getCategoryName());
        mPlanTopItemViewHolder.getTextviewSetTargetTask().setText(bean.getTaskName());

        GradientDrawable gradientDrawable = (GradientDrawable) mPlanTopItemViewHolder.getTextviewSetTargetCategory().getBackground();
        gradientDrawable.setColor(Color.parseColor(bean.getCategoryColor()));
    }

}
