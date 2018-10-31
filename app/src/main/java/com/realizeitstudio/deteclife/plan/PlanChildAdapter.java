package com.realizeitstudio.deteclife.plan;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/9/24
 */
public class PlanChildAdapter extends RecyclerView.Adapter {
    private static final String MSG = "PlanChildAdapter: ";

    private PlanChildContract.Presenter mPresenter;
    private List<GetTaskWithPlanTime> mPlanningList;
    private boolean[] isDeleteArray;
    private int[] mIntAdjustCostTime;  // 總調整時間 (min)
    private int mIntMaxCostTime;        // 該 plan 週期的上限 (min)
    private int mIntMaxCostHr;           // 該 plan 週期的上限 (hr)
    private int mIntTotalCostTime;      // 目前該 plan 週期中所有 target 總使用時間 (min)
    private int mIntNewItemCostTime;    // Top Item 新增時間 (min)
    private int mIntPlanMode;
    private int mIntChildMode;



    private PlanTopItemViewHolder mPlanTopItemViewHolder;
    private MainActivity mMainActivity;


    public PlanChildAdapter(List<GetTaskWithPlanTime> bean, PlanChildContract.Presenter presenter, MainActivity activity) {

        mPresenter = presenter;
        setIntChildMode(mPresenter.getIntChildMode());

        setIntPlanMode(Constants.MODE_PLAN_VIEW);
        mPlanningList = new ArrayList<>();

        for (int i = 0; i < bean.size(); ++i) {
            this.mPlanningList.add(bean.get(i));
        }

        mMainActivity = activity;
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

        for (int i = 0; i < bean.size(); ++i) {
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
            if (getIntChildMode() == Constants.TAB_DAILY_MODE) {
                mIntMaxCostTime = 24 * 60;
            } else if (getIntChildMode() == Constants.TAB_WEEKLY_MODE) {
                mIntMaxCostTime = 24 * 60 * 7;
            }

            mIntMaxCostHr = mIntMaxCostTime / 60;
            mIntNewItemCostTime = 0;
            mIntAdjustCostTime = null;

            mIntAdjustCostTime = new int[intArraySize];
            for (int i = 0; i < intArraySize; ++i) {
                mIntAdjustCostTime[i] = (int) (mPlanningList.get(i).getCostTime() / (60 * 1000));
            }

            Logger.d(Constants.TAG, MSG + "max-costTime: " + mIntMaxCostTime);
        }

        mIntTotalCostTime = 0;
        for (int i = 0; i < mPlanningList.size(); ++i) {
            mIntTotalCostTime += (int) (mPlanningList.get(i).getCostTime() / (60 * 1000));
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
        private ConstraintLayout mConstraintLayoutAdjustCostTime;
        private ConstraintLayout mConstraintLayoutPlanTaskCostTime;
        private ImageView mImageviewPlanTaskCostTime;

        private NumberPicker mNumberPickerHr;
        private NumberPicker mNumberPickerMin;
        private AlertDialog mDialog;
        private int intNumberPickerHr;
        private int intNumberPickerMin;

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

        public FrameLayout getFrameLayoutPlanTaskDelete() {
            return mFrameLayoutPlanTaskDelete;
        }

        public TextView getTextviewPlanCategoryName() {
            return mTextviewPlanCategoryName;
        }

        public ImageView getImageviewPlanTaskIcon() {
            return mImageviewPlanTaskIcon;
        }

        public TextView getTextviewPlanTaskName() {
            return mTextviewPlanTaskName;
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

        public ConstraintLayout getConstraintLayoutPlanTaskCostTime() {
            return mConstraintLayoutPlanTaskCostTime;
        }

        public ConstraintLayout getConstraintLayoutAdjustCostTime() {
            return mConstraintLayoutAdjustCostTime;
        }

        public ImageView getImageviewPlanTaskCostTime() {
            return mImageviewPlanTaskCostTime;
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

            //** Edit mode
            mFrameLayoutPlanTaskDelete = v.findViewById(R.id.framelayout_plan_task_delete_hint);
            mImageviewPlanTaskDeleteHint = (ImageView) v.findViewById(R.id.imageview_plan_task_delete_hint);
            mImageviewPlanTaskDeleteHint.setOnClickListener(this);
            mConstraintLayoutAdjustCostTime = v.findViewById(R.id.constraintlayout_plantask_adjust_costtime);
            if (Constants.TAB_DAILY_MODE == getIntChildMode()) {
                mSeekBarPlanTaskAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_task_adjust_cost_time_daily);
            } else {
                mSeekBarPlanTaskAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_task_adjust_cost_time_weekly);
            }
            mSeekBarPlanTaskAdjustTime.setOnSeekBarChangeListener(mSeekBarChangeListener);

            mImageviewPlanTaskCostTime = v.findViewById(R.id.imageview_plan_task_cost_time);
            mConstraintLayoutPlanTaskCostTime = v.findViewById(R.id.constraintlayout_plan_task_cost_time);
            mConstraintLayoutPlanTaskCostTime.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // [TODO] change R.id.imageview_plan_task_delete_hint to Delete button
            // 目前是把 delete 當作一個 checkbox 來用，先勾選並顯示在畫面上。並在按下 save 的時候一起刪掉
            if (v.getId() == R.id.imageview_plan_task_delete_hint) {

                // if original delete flag is on, than cancel. (change background color to white)
                if (isDeleteArray[getCurrentPosition()] == true) {

                    isDeleteArray[getCurrentPosition()] = false;

                    GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                    gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_light_grey));

                } else {
                    // if original delete flag is off, than delete. (change background color with drawable)

                    isDeleteArray[getCurrentPosition()] = true;

                    GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                    gradientDrawable.setColor(Color.parseColor("#f44336"));
                }

                Logger.d(Constants.TAG, MSG + "delete " + mPlanningList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);

            } else if (v.getId() == R.id.constraintlayout_plan_task_cost_time) {

                showTimePickerDialog();

            } else if (v.getId() == R.id.imageview_timepicker_check) {    // check the time selected result

                Logger.d(Constants.TAG, MSG + "onClick-timepicker_check: item id = " + getCurrentPosition());

                intNumberPickerHr = mNumberPickerHr.getValue();
                intNumberPickerMin = mNumberPickerMin.getValue();
                Logger.d(Constants.TAG, MSG + "PickTime: Hr = " + intNumberPickerHr + " Min = " + intNumberPickerMin);

                int progress = intNumberPickerHr * 60 + intNumberPickerMin;
                Logger.d(Constants.TAG, MSG + "PickTime: progress = " + progress);

                mDialog.dismiss();


//                mIntAdjustCostTime[getCurrentPosition()] = mPlanningList.get(mPosition).getCostTime();

                // progress 是時間選擇器選擇出來的時間，加上 totalCostTime 後和 maxTotalTime 比較
                // mIntAdjustCostTime[getCurrentPosition()] 是現在這個項目已經計畫的時間

                // if (mIntTotalCostTime + progress > maxTotalTime) // 表示現在選擇時間太長了 (只會發生在分針超過的時候)，要跳出 Toast
                // else if (mIntTotalCostTime + progress <= maxTotalTime) // 表示 progress 拉得剛剛好

                if ((mIntTotalCostTime - mIntAdjustCostTime[getCurrentPosition()] + progress) > mIntMaxCostTime) {

                    mPresenter.showToast(Constants.TOAST_COST_TIME_EXCEEDED + ": " + ParseTime.intToHrM(mIntMaxCostTime));   // 提示時間超過上限了

                    // [Seekbar] 1. 如果已經封頂了，user 還是把 progress 往後拉，則強制停在原地並 return。其餘數字均不改動
                    if (mIntMaxCostTime == mIntTotalCostTime) {
                        mSeekBarPlanTaskAdjustTime.setProgress(mIntAdjustCostTime[getCurrentPosition()]);
                        Logger.d(Constants.TAG, MSG + "Already meet the max-time, progress won't change");
                        Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                        return;
                    }

                    // [Seekbar] 2. 如果第一次被拉到封頂
                    progress = mIntAdjustCostTime[getCurrentPosition()] + mIntMaxCostTime - mIntTotalCostTime;         // 直接把 progress 設到滿
                    mIntTotalCostTime = mIntMaxCostTime;                    // 並把 totalCostTime 加到滿

                    mIntAdjustCostTime[getCurrentPosition()] = progress;    // progress means minutes
                    mSeekBarPlanTaskAdjustTime.setProgress(progress);

                    String strCostTime = ParseTime.intToHourMin(progress);
                    getTextviewPlanTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間

                    Logger.d(Constants.TAG, MSG + "Meet the max-time, reset progress to current maximum");
                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);

                    return;

                } else if ((mIntTotalCostTime - mIntAdjustCostTime[getCurrentPosition()] + progress) <= mIntMaxCostTime) { // 表示 progress 拉得剛剛好

                    // [Seekbar] 3. 沒封頂的情況

                    String strCostTime = ParseTime.intToHourMin(progress);

                    Logger.d(Constants.TAG, MSG + "Current CostTime: " + mIntAdjustCostTime[getCurrentPosition()] + " Progress: " + progress + " CostTime: " + strCostTime);

                    getTextviewPlanTaskCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間
                    mIntTotalCostTime = mIntTotalCostTime - mIntAdjustCostTime[getCurrentPosition()] + progress; // 總時數 - 這個 item 原本的 costTime ＋ 這個 item 新調的時間
                    mIntAdjustCostTime[getCurrentPosition()] = progress;    // progress means minutes

                    mSeekBarPlanTaskAdjustTime.setProgress(progress);

                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress + " Current CostTime: " + mIntAdjustCostTime[getCurrentPosition()]);
                }

            } else if (v.getId() == R.id.imageview_timepicker_cancel) { // cancel: just close the time selected dialog
                mDialog.dismiss();
            }
        }


        public void showTimePickerDialog() {

            // 彈出對話框選擇時間
            // ****** 用自定義的 mDialog 介面 ******
            View view = View.inflate(mMainActivity, R.layout.dialog_time_picker, null);

            mNumberPickerHr = (NumberPicker) view.findViewById(R.id.numberpicker_hr);
            mNumberPickerHr.setMinValue(0);
            mNumberPickerMin = (NumberPicker) view.findViewById(R.id.numberpicker_min);
            mNumberPickerMin.setMaxValue(59);
            mNumberPickerMin.setMinValue(0);

            // ** 可調整時間要設定成自己當前的使用時間 + 現在還可以調整的範圍
            // 基本上不會超過 mIntMaxCostHr

            // Max - curTotal + (costTime of current task)
            int intMaxValue = ((mIntMaxCostTime - mIntTotalCostTime + mSeekBarPlanTaskAdjustTime.getProgress()) / 60);
            mNumberPickerHr.setMaxValue(intMaxValue);


            // 先把 numberPicker 的數字設定成現在佔用的 cost-time
            mNumberPickerHr.setValue(mSeekBarPlanTaskAdjustTime.getProgress() / 60);
            mNumberPickerMin.setValue(mSeekBarPlanTaskAdjustTime.getProgress() % 60);


            ((ImageView) view.findViewById(R.id.imageview_timepicker_check)).setOnClickListener(this);
            ((ImageView) view.findViewById(R.id.imageview_timepicker_cancel)).setOnClickListener(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
            builder.setView(view);

            // 取消或確定按鈕監聽事件處理
            Logger.d(Constants.TAG, MSG + "before dialog build:");

            mDialog = builder.create();
            mDialog.show();
            mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);

            // 換 dialog 顏色
            LinearLayout linearLayoutIconDialog = view.findViewById(R.id.linearlayout_timepicker_dialog);
            GradientDrawable gradientDrawable = (GradientDrawable) linearLayoutIconDialog.getBackground();
            gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white));

        }

        //** Seekbar
        private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            //onProgressChanged  進度條只要拖動就觸發此事件（持續觸發）

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                Logger.d(Constants.TAG, MSG + "onProgressChanged: item id = " + getCurrentPosition());

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
        public void bindView(GetTaskWithPlanTime item, int pos) {

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());

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
                getConstraintLayoutAdjustCostTime().setVisibility(View.GONE);
                getSeekBarPlanTaskAdjustTime().setVisibility(View.GONE);
                getConstraintLayoutPlanTaskCostTime().setClickable(false);
                getImageviewPlanTaskCostTime().setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                getFrameLayoutPlanTaskDelete().setVisibility(View.VISIBLE);
                getImageviewPlanTaskDeleteHint().setVisibility(View.VISIBLE);
                getConstraintLayoutAdjustCostTime().setVisibility(View.VISIBLE);
                getSeekBarPlanTaskAdjustTime().setVisibility(View.VISIBLE);
                getConstraintLayoutPlanTaskCostTime().setClickable(true);
                getImageviewPlanTaskCostTime().setVisibility(View.VISIBLE);

                Animation seekbarAnimation = new TranslateAnimation(0,0,100,0);
                //動畫開始到結束的時間，1秒
                seekbarAnimation.setDuration(1000);
                // 動畫重覆次數 (-1表示一直重覆，0表示不重覆執行，所以只會執行一次)
                seekbarAnimation.setRepeatCount(0);
                //將動畫寫入ImageView
                getSeekBarPlanTaskAdjustTime().setAnimation(seekbarAnimation);
                //開始動畫
                seekbarAnimation.startNow();


                gradientDrawable = (GradientDrawable) getFrameLayoutPlanTaskDelete().getBackground();
                gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_light_grey));

                getSeekBarPlanTaskAdjustTime().setProgress((int)(item.getCostTime() / (60 * 1000)));
                Logger.d(Constants.TAG, MSG + "bindview: seekbar-progress = " + (int)(item.getCostTime() / (60 * 1000)));

                getSeekBarPlanTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_IN);
//                getSeekBarPlanTaskAdjustTime().getProgressDrawable().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.SRC_ATOP); // 疑似也是改 thumb
                getSeekBarPlanTaskAdjustTime().getThumb().setColorFilter(Color.parseColor(item.getTaskColor()), PorterDuff.Mode.MULTIPLY);

                // 修改 cost_time 點選提示 (底線) 的顏色
                mImageviewPlanTaskCostTime.setBackgroundColor(Color.parseColor(item.getTaskColor()));
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
        private FrameLayout mFrameLayoutAddItemIcon;
        private ConstraintLayout mConstraintLayoutSetTargetCostTime;
        private ImageView mImageviewAddItemIcon;
        private ImageView mImageviewSetTargetCostTime;
        private TextView mTextviewSetTargetCategory;
        private TextView mTextviewSetTargetTask;
        private TextView mTextviewSetTargetCostTime;
        private SeekBar mSeekBarSetTargetAdjustTime;
        private ConstraintLayout mConstraintLayoutAdjustCostTime;

        private NumberPicker mNumberPickerHr;
        private NumberPicker mNumberPickerMin;
        private AlertDialog mDialog;
        private int intNumberPickerHr;
        private int intNumberPickerMin;

        public TextView getTextviewPlanTopRemainingTime() {
            return mTextviewPlanTopRemainingTime;
        }

        public ConstraintLayout getConstraintLayoutPlanTopItem() {
            return mConstraintLayoutPlanTopItem;
        }

        public ConstraintLayout getConstraintLayoutPlanSetTarget() {
            return mConstraintLayoutPlanSetTarget;
        }

        public FrameLayout getFrameLayoutAddItemIcon() {
            return mFrameLayoutAddItemIcon;
        }

        public ImageView getImageviewAddItemIcon() {
            return mImageviewAddItemIcon;
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

        public ConstraintLayout getConstraintLayoutAdjustCostTime() {
            return mConstraintLayoutAdjustCostTime;
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
            mTextviewSetTargetCategory.setOnClickListener(this);

            // Set Task
            mTextviewSetTargetTask = (TextView) v.findViewById(R.id.edittext_plan_top_editmode_task);
            mTextviewSetTargetTask.setOnClickListener(this);

            // Set Icon
            mImageviewAddItemIcon = v.findViewById(R.id.imageview_plan_top_editmode_icon);
            getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(Constants.DEFAULT_TASK_ICON));
            getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

            mFrameLayoutAddItemIcon = v.findViewById(R.id.framelayout_plan_top_editmode_icon);
            mFrameLayoutAddItemIcon.setOnClickListener(this);

            GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutAddItemIcon().getBackground();
            gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_light_grey));

            // Set Cost Time
            mConstraintLayoutSetTargetCostTime = v.findViewById(R.id.constraintlayout_plan_set_target_cost_time);
            mTextviewSetTargetCostTime = v.findViewById(R.id.textview_plan_set_target_cost_time);
            mImageviewSetTargetCostTime = v.findViewById(R.id.imageview_plan_set_target_cost_time);
            mConstraintLayoutSetTargetCostTime.setOnClickListener(this);

            mConstraintLayoutPlanSetTarget = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_top_editmode);
            mConstraintLayoutAdjustCostTime = v.findViewById(R.id.constraintlayout_settarget_adjust_costtime);
            if (Constants.TAB_DAILY_MODE == getIntChildMode()) {
                mSeekBarSetTargetAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_set_target_cost_time_daily);
            } else {
                mSeekBarSetTargetAdjustTime = (SeekBar) v.findViewById(R.id.seekbar_plan_set_target_cost_time_weekly);
            }

            ((ImageView) v.findViewById(R.id.imageview_plan_top_editmode_save)).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.imageview_plan_top_editmode_cancel)).setOnClickListener(this);

            mSeekBarSetTargetAdjustTime.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_plan_top_viewmode) {    // View mode

                mPresenter.refreshUi(Constants.MODE_PLAN_EDIT);

            } else if (v.getId() == R.id.imageview_plan_top_editmode_save) {  // Edit mode - complete

                // Delete 的目標，表示要把 endTime 押在這個 plan 週期之前，如果是 Daily，endTime 就是昨天，如果是 Weekly，endTime 就是上週日
                // [TODO] 可檢查是否本來 startTime 在這個 plan 週期，如果是的話就真的刪掉，如果不是的話就照上面處理
                // [TODO] plan 週期需要能讀畫面上元件內容
                // 或是用 job 在 DB 裡面刪掉
                // Edit 表示要更新，把 StartTime 在這個 plan 週期之前的全部押上 endTime，並開一個新的目標
                //   StartTime 設定為這個週期的第一天
                //   EndTime 設定為 4000/01/01
                // [TODO] 未來可以一次新增多個 target (多加一個小打勾，像 trello 新增卡片)
                // [TODO] 換成真正的 startTime, endTime

                // 1. 取得時間 // [TODO] 未來要改取畫面上時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);

                // 1.1 做出新增 Task 的 startTime, endTime
                String curEndVerNo = Constants.DB_ENDLESS_DATE;

                // Daily 的 startTime 是當天
                String curStartVerNoDaily = simpleDateFormat.format(curDate);

                // Weekly 的 startTime 是當週的週一
                int intWeekDay = ParseTime.date2Day(curDate);    // 把今天傳入，回傳今天是星期幾 (1 = 星期一，2 = 星期二)
                // 如果今天是星期一，則需從今天往回減 0 天。
                // 如果今天是星期二，則需從今天往回減 1 天。
                Date thisMonday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (intWeekDay - 1)); // 找出本週一
                String curStartVerNoWeekly = simpleDateFormat.format(thisMonday);

                // 1.2 update_date
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);

                // 1.3 取得上一個 planning 週期的 endTime (daily 為昨天，weekly 為上週日)
                Date yesterday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                String lastEndVerNoDaily = simpleDateFormat.format(yesterday);

                // 計算 Weekly 的上一個週期 endTime
                // 如果今天是星期一，則需從今天往回減 1 天。
                // 如果今天是星期二，則需從今天往回減 2 天。
                Date lastSunday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * intWeekDay); // 找出上週日
                String lastEndVerNoWeekly = simpleDateFormat.format(lastSunday);


                // 2. 新增兩個 List 以 (1) 存放要存回 database 的資料 (2) 要從 database 刪除的資料
                List<TimePlanningTable> targetList = new ArrayList<>();
                List<TimePlanningTable> deleteTargetList = new ArrayList<>();

                // 2.1 先針對現有所有目標清單做出 TimePlanning Table 物件
                // [TODO] Weekly 要另外判斷
                for (int i = 0; i < mPlanningList.size(); ++i) {

                    // [Delete] if user decides to delete this item, then delete from database
                    if (isDeleteArray[i] == true) {

                        if (Constants.MODE_DAILY.equals(mPlanningList.get(i).getMode())) {

                            // Daily
                            // 是否該 item 的 startTime 在這個 plan 週期 (startTime >= curStartVerNoDaily)，是的話就真的刪掉
                            if (mPlanningList.get(i).getStartTime().compareTo(curStartVerNoDaily) >= 0) {

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        mPlanningList.get(i).getEndTime(),
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                deleteTargetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Delete item: ");
                                item.logD();

                            } else {    // startTime 在之前的 plan 週期 (startTime < curStartVerNoDaily)，如果是的話把原本的 target 押上 lastEndVerNoDaily

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        lastEndVerNoDaily,
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
                                item.logD();
                            }


                        } else {    // if (Constants.MODE_WEEKLY.equals(mPlanningList.get(i).getMode()))

                            // Weekly
                            // 是否該 item 的 startTime 在這個 plan 週期 (startTime >= curStartVerNoWeekly)，是的話就真的刪掉
                            if (mPlanningList.get(i).getStartTime().compareTo(curStartVerNoWeekly) >= 0) {

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        mPlanningList.get(i).getEndTime(),
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                deleteTargetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Delete item: ");
                                item.logD();

                            } else {    // startTime 在之前的 plan 週期 (startTime < curStartVerNoWeekly)，如果是的話把原本的 target 押上 lastEndVerNoWeekly

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        lastEndVerNoWeekly,
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
                                item.logD();
                            }
                        }

                        // end of [Delete]
                    } else { // [Edit]

                        if (Constants.MODE_DAILY.equals(mPlanningList.get(i).getMode())) {
                            // Daily

                            // 是否該 item 的 startTime 在這個 plan 週期 (startTime >= curStartVerNoDaily)，是的話就直接 update (add 相同 key 的 item)
                            if (mPlanningList.get(i).getStartTime().compareTo(curStartVerNoDaily) >= 0) {

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        mPlanningList.get(i).getEndTime(),
                                        ((long) mIntAdjustCostTime[i]) * 60000, // 存調整後的 costTime
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (1. adjust current target of this plan-period)");
                                item.logD();

                            } else {    // startTime 在之前的 plan 週期 (startTime < curStartVerNoDaily)，如果是的話把原本的 target 押上 lastEndVerNoDaily，並加一筆新的

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        lastEndVerNoDaily,
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (2.1. save current target to last plan-period)");
                                item.logD();


                                item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        curStartVerNoDaily,
                                        curEndVerNo,
                                        ((long) mIntAdjustCostTime[i]) * 60000, // 存調整後的 costTime
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (2.2 add a new target to this plan-period)");
                                item.logD();
                            }


                        } else {    // if (Constants.MODE_WEEKLY.equals(mPlanningList.get(i).getMode()))

                            // Weekly
                            // 是否該 item 的 startTime 在這個 plan 週期 (startTime >= curStartVerNoWeekly)，是的話就直接 update (add 相同 key 的 item)
                            if (mPlanningList.get(i).getStartTime().compareTo(curStartVerNoWeekly) >= 0) {

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        mPlanningList.get(i).getEndTime(),
                                        ((long) mIntAdjustCostTime[i]) * 60000, // 存調整後的 costTime
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (1. adjust current target of this plan-period)");
                                item.logD();

                            } else {    // startTime 在之前的 plan 週期 (startTime < curStartVerNoWeekly)，如果是的話把原本的 target 押上 lastEndVerNoWeekly，並加一筆新的

                                TimePlanningTable item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        mPlanningList.get(i).getStartTime(),
                                        lastEndVerNoWeekly,
                                        mPlanningList.get(i).getCostTime(),
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (2.1. save current target to last plan-period)");
                                item.logD();


                                item = new TimePlanningTable(mPlanningList.get(i).getMode(),
                                        mPlanningList.get(i).getCategoryName(),
                                        mPlanningList.get(i).getTaskName(),
                                        curStartVerNoWeekly,
                                        curEndVerNo,
                                        ((long) mIntAdjustCostTime[i]) * 60000, // 存調整後的 costTime
                                        strCurrentTime);

                                targetList.add(item);

                                Logger.d(Constants.TAG, MSG + "Add/Edit item: (2.2 add a new target to this plan-period)");
                                item.logD();
                            }
                        }
                    }   // end of [EDIT]

                }   // end of for each item

                // 2.2 再把最新 add 的目標加在最後
                if (TimeManagementApplication.getAppContext().getResources().getString(R.string.default_task_hint)
                        .equals(getTextviewSetTargetTask().getText().toString().trim())) {

                    mPresenter.showToast(Constants.TOAST_ADD_TASK_FAIL);

                } else {

                    if (Constants.TAB_DAILY_MODE == getIntChildMode()) {
                        targetList.add(new TimePlanningTable(
                                Constants.MODE_DAILY,
                                getTextviewSetTargetCategory().getText().toString().trim(),
                                getTextviewSetTargetTask().getText().toString().trim(),
                                curStartVerNoDaily,
                                curEndVerNo,
                                (long)(mIntNewItemCostTime * 60000),
                                strCurrentTime
                        ));
                    } else {
                        targetList.add(new TimePlanningTable(
                                Constants.MODE_WEEKLY,
                                getTextviewSetTargetCategory().getText().toString().trim(),
                                getTextviewSetTargetTask().getText().toString().trim(),
                                curStartVerNoWeekly,
                                curEndVerNo,
                                (long)(mIntNewItemCostTime * 60000),
                                strCurrentTime
                        ));
                    }
                }

                // 3. send asyncTask to update data
                mPresenter.saveTargetResults(targetList, deleteTargetList);

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.imageview_plan_top_editmode_cancel) { // Edit mode - cancel

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.edittext_plan_top_editmode_task
                    || v.getId() == R.id.textview_plan_top_editmode_category
                    || v.getId() == R.id.framelayout_plan_top_editmode_icon) {

                mPresenter.showTaskListUi();

            } else if (v.getId() == R.id.constraintlayout_plan_set_target_cost_time) {
                Logger.d(Constants.TAG, MSG + "click cost-time");

                showTimePickerDialog();

            } else if (v.getId() == R.id.imageview_timepicker_check) {    // check the time selected result

                Logger.d(Constants.TAG, MSG + "onClick-timePickerCheck: top item ");

                intNumberPickerHr = mNumberPickerHr.getValue();
                intNumberPickerMin = mNumberPickerMin.getValue();
                Logger.d(Constants.TAG, MSG + "Hr = " + intNumberPickerHr + " Min = " + intNumberPickerMin);

                int progress = intNumberPickerHr * 60 + intNumberPickerMin;

                mDialog.dismiss();



                if ((mIntTotalCostTime - mIntNewItemCostTime + progress) > mIntMaxCostTime) { // 目前設定總時數 - 這個 item 原本的設定的 costTime ＋ 這個 item 新調的時間 > 上限)

                    mPresenter.showToast(Constants.TOAST_COST_TIME_EXCEEDED + ": " + ParseTime.intToHrM(mIntMaxCostTime));   // 提示時間超過上限了

                    // [Seekbar] 1. 如果已經封頂了，user 還是把 progress 往後拉，則強制停在原地並 return。其餘數字均不改動
                    if (mIntMaxCostTime == mIntTotalCostTime) {
                        mSeekBarSetTargetAdjustTime.setProgress(mIntNewItemCostTime);
                        Logger.d(Constants.TAG, MSG + "Already meet the max-time, progress won't change");
                        Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                        return;
                    }

                    // [Seekbar] 2. 如果第一次被拉到封頂
                    progress = mIntNewItemCostTime + mIntMaxCostTime - mIntTotalCostTime;         // 直接把 progress 設到滿
                    mIntTotalCostTime = mIntMaxCostTime;                    // 並把 totalCostTime 加到滿

                    mIntNewItemCostTime = progress;    // progress means minutes
                    mSeekBarSetTargetAdjustTime.setProgress(progress);

                    String strCostTime = ParseTime.intToHourMin(progress);
                    getTextviewSetTargetCostTime().setText(strCostTime);  // 設定 UI 顯示現在 progress 進度時間

                    Logger.d(Constants.TAG, MSG + "Meet the max-time, reset progress to current maximum");
                    Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);
                    return;
                }

                // [Seekbar] 3. 沒封頂的情況
                String strCostTime = ParseTime.intToHourMin(progress);

                Logger.d(Constants.TAG, MSG + "Progress: " + progress + " CostTime: " + strCostTime);

                getTextviewSetTargetCostTime().setText(strCostTime);
                mIntTotalCostTime = mIntTotalCostTime - mIntNewItemCostTime + progress; // 總時數 - 這個 item 原本的 costTime ＋ 這個 item 新調的時間
                mIntNewItemCostTime = progress; // progress means minutes

                Logger.d(Constants.TAG, MSG + "MaxTime: " + mIntMaxCostTime + " Total costTime: " + mIntTotalCostTime + " Progress: " + progress);

                mSeekBarSetTargetAdjustTime.setProgress(progress);









            } else if (v.getId() == R.id.imageview_timepicker_cancel) { // cancel: just close the time selected dialog
                mDialog.dismiss();
            }
        }

        public void showTimePickerDialog() {

            // 彈出對話框選擇時間
            // ****** 用自定義的 mDialog 介面 ******
            View view = View.inflate(mMainActivity, R.layout.dialog_time_picker, null);

            mNumberPickerHr = (NumberPicker) view.findViewById(R.id.numberpicker_hr);
            mNumberPickerHr.setMinValue(0);
            mNumberPickerMin = (NumberPicker) view.findViewById(R.id.numberpicker_min);
            mNumberPickerMin.setMaxValue(59);
            mNumberPickerMin.setMinValue(0);

            // ** 可調整時間要設定成自己當前的使用時間 + 現在還可以調整的範圍 (取大值)
            // 這個時間應該不會超過 mIntMaxCostHr
            int intMaxValue = ((mIntMaxCostTime - mIntTotalCostTime + mSeekBarSetTargetAdjustTime.getProgress()) / 60);
            mNumberPickerHr.setMaxValue(intMaxValue);

            mNumberPickerHr.setValue(mSeekBarSetTargetAdjustTime.getProgress() / 60);
            mNumberPickerMin.setValue(mSeekBarSetTargetAdjustTime.getProgress() % 60);

            ((ImageView) view.findViewById(R.id.imageview_timepicker_check)).setOnClickListener(this);
            ((ImageView) view.findViewById(R.id.imageview_timepicker_cancel)).setOnClickListener(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
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
            Logger.d(Constants.TAG, MSG + "before dialog build:");

            mDialog = builder.create();
            mDialog.show();
            mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);

            // 換 dialog 顏色
            LinearLayout linearLayoutIconDialog = view.findViewById(R.id.linearlayout_timepicker_dialog);
            GradientDrawable gradientDrawable = (GradientDrawable) linearLayoutIconDialog.getBackground();
            gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white));
        }

        //** Seekbar
        private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            //onProgressChanged  進度條只要拖動就觸發此事件（持續觸發）

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                Logger.d(Constants.TAG, MSG + "onProgressChanged: top item ");

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

                // [Seekbar] 3. 沒封頂的情況
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

            Logger.d(Constants.TAG, MSG + "bindView: top item ");

            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {

                mConstraintLayoutPlanTopItem.setVisibility(View.VISIBLE);
                mConstraintLayoutPlanSetTarget.setVisibility(View.GONE);
                mConstraintLayoutAdjustCostTime.setVisibility(View.GONE);
                mSeekBarSetTargetAdjustTime.setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                mConstraintLayoutPlanTopItem.setVisibility(View.GONE);
                mConstraintLayoutPlanSetTarget.setVisibility(View.VISIBLE);
                mConstraintLayoutAdjustCostTime.setVisibility(View.VISIBLE);
                mSeekBarSetTargetAdjustTime.setVisibility(View.VISIBLE);
                resetEditField();
            }
        }


        public void resetEditField() {

            // Plan page 整頁切換為編輯模式預設內容
            // Set task/category
            getTextviewSetTargetCategory().setText(TimeManagementApplication.getAppContext().getResources().getString(R.string.default_task_hint_categroy));
            getTextviewSetTargetTask().setText(TimeManagementApplication.getAppContext().getResources().getString(R.string.default_task_hint));

            // Set category label
            GradientDrawable gradientDrawable = (GradientDrawable) getTextviewSetTargetCategory().getBackground();
            gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_light_grey));

            // Set task icon
            gradientDrawable = (GradientDrawable) getFrameLayoutAddItemIcon().getBackground();
            gradientDrawable.setColor(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_light_grey));

            getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(Constants.DEFAULT_TASK_ICON));
            getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

            // Set cost time
            mIntNewItemCostTime = 0;
            getTextviewSetTargetCostTime().setText("0 min");
            getSeekBarSetTargetAdjustTime().setProgress(0);
            mSeekBarSetTargetAdjustTime.getProgressDrawable().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_default_blue), PorterDuff.Mode.SRC_IN);
        }
    }


    private int getIntPlanMode() {
        return mIntPlanMode;
    }

    private void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }

    public int getIntChildMode() {
        return mIntChildMode;
    }

    public void setIntChildMode(int intChildMode) {
        mIntChildMode = intChildMode;
    }




    public void showCategoryTaskSelected(GetCategoryTaskList bean) {

        // set category/task name
        mPlanTopItemViewHolder.getTextviewSetTargetCategory().setText(bean.getCategoryName());
        mPlanTopItemViewHolder.getTextviewSetTargetTask().setText(bean.getTaskName());

        // set category label
        GradientDrawable gradientDrawable = (GradientDrawable) mPlanTopItemViewHolder.getTextviewSetTargetCategory().getBackground();
        gradientDrawable.setColor(Color.parseColor(bean.getCategoryColor()));

        // set task icon
        mPlanTopItemViewHolder.getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(bean.getTaskIcon()));
        mPlanTopItemViewHolder.getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

        gradientDrawable = (GradientDrawable) mPlanTopItemViewHolder.getFrameLayoutAddItemIcon().getBackground();
        gradientDrawable.setColor(Color.parseColor(bean.getTaskColor()));
    }

}
