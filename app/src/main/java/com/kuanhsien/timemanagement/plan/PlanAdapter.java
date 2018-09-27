package com.kuanhsien.timemanagement.plan;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/9/24
 */
public class PlanAdapter extends RecyclerView.Adapter {
    private static final String MSG = "PlanAdapter: ";

    private PlanContract.Presenter mPresenter;
    private List<GetTaskWithPlanTime> mPlanningList;
    private boolean[] isDeleteArray;
    private int mIntPlanMode;


    public PlanAdapter(List<GetTaskWithPlanTime> bean, PlanContract.Presenter presenter) {

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
            // detail ar
            ((PlanTopItemViewHolder) holder).bindView();
        } else {
            // comments
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
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "VIEW_MODE" : "EDIT_MODE"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            isDeleteArray = null;
            isDeleteArray = new boolean[mPlanningList.size()];
            Arrays.fill(isDeleteArray, false);  // in Java, boolean array default set false to all items. this setting is only for reading
        }

        setIntPlanMode(mode);
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanMainItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        //** View mode
        private ImageView mImageviewPlanTaskIcon;
        private TextView mTextviewPlanTaskName;
        private TextView mTextviewPlanTaskCostTime;
        private ConstraintLayout mConstraintLayoutPlanMainItem;

        //** Edit mode
        private ImageView mImageviewPlanTaskDeleteHint;
        private TextView mTextviewPlanTaskAdjustTime;


        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
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

        public TextView getTextviewPlanTaskAdjustTime() {
            return mTextviewPlanTaskAdjustTime;
        }

        public PlanMainItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mConstraintLayoutPlanMainItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_main_item);
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
            mTextviewPlanTaskAdjustTime = (TextView) v.findViewById(R.id.textview_plan_task_adjust_cost_time);
            mImageviewPlanTaskDeleteHint = (ImageView) v.findViewById(R.id.imageview_plan_task_delete_hint);
            mImageviewPlanTaskDeleteHint.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // [TODO] change R.id.imageview_plan_task_delete_hint to Delete button
            // 目前是把 delete 當作一個 checkbox 來用，先勾選並顯示在畫面上。並在按下 save 的時候一起刪掉
            if (v.getId() == R.id.imageview_plan_task_delete_hint) {

                // if original delete flag is on, than cancel. (change background color to white)
                if (isDeleteArray[getCurrentPosition()] == true) {

                    isDeleteArray[getCurrentPosition()] = false;
                    mConstraintLayoutPlanMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(android.R.color.white));

                } else {
                    // if original delete flag is off, than delete. (change background color with drawable)

                    isDeleteArray[getCurrentPosition()] = true;
                    mConstraintLayoutPlanMainItem.setBackground(TimeManagementApplication.getAppContext().getDrawable(R.drawable.toolbar_background));

                }

                Logger.d(Constants.TAG, MSG + "delete " + mPlanningList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);
            }
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView(GetTaskWithPlanTime item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder
            getTextviewPlanTaskName().setText(item.getTaskName());
            getTextviewPlanTaskCostTime().setText(item.getCostTime());
            setPosition(pos);

            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {

                getImageviewPlanTaskDeleteHint().setVisibility(View.GONE);
                getTextviewPlanTaskAdjustTime().setVisibility(View.GONE);

            } else { // getIntPlanMode() == Constants.MODE_PLAN_EDIT

                getImageviewPlanTaskDeleteHint().setVisibility(View.VISIBLE);
                getTextviewPlanTaskAdjustTime().setVisibility(View.VISIBLE);
            }
        }

    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanTopItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        //** View Mode
        private ConstraintLayout mConstraintLayoutPlanTopItem;
        private TextView mTextviewPlanTopRemainingTime;

        //** Edit Mode
        private ConstraintLayout mConstraintLayoutPlanSetTarget;
        private EditText mEditTextSetTargetCategory;
        private EditText mEditTextSetTargetTask;
        private TextView mTextViewSetTargetCostTime;


        public TextView getTextviewPlanTopRemainingTime() {
            return mTextviewPlanTopRemainingTime;
        }

        public ConstraintLayout getConstraintLayoutPlanTopItem() {
            return mConstraintLayoutPlanTopItem;
        }

        public ConstraintLayout getConstraintLayoutPlanSetTarget() {
            return mConstraintLayoutPlanSetTarget;
        }

        public EditText getEditTextSetTargetCategory() {
            return mEditTextSetTargetCategory;
        }

        public EditText getEditTextSetTargetTask() {
            return mEditTextSetTargetTask;
        }

        public TextView getTextViewSetTargetCostTime() {
            return mTextViewSetTargetCostTime;
        }

        public PlanTopItemViewHolder(View v) {
            super(v);

            //** View Mode
            mTextviewPlanTopRemainingTime = (TextView) v.findViewById(R.id.textview_plan_top_remaining_time);
            mConstraintLayoutPlanTopItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_top_view_mode);
            mConstraintLayoutPlanTopItem.setOnClickListener(this);

            //** Edit Mode
            mEditTextSetTargetCategory = (EditText) v.findViewById(R.id.edittext_plan_set_target_category);
            mEditTextSetTargetTask = (EditText) v.findViewById(R.id.edittext_plan_set_target_task);
            mTextViewSetTargetCostTime = (TextView) v.findViewById(R.id.textview_plan_set_target_cost_time);
            mConstraintLayoutPlanSetTarget = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_set_target);

            ((ImageView) v.findViewById(R.id.imageview_plan_set_target_save)).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.imageview_plan_set_target_cancel)).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_plan_top_view_mode) {    // View mode

                // Plan page 整頁切換為編輯模式
                getEditTextSetTargetCategory().setText("");
                getEditTextSetTargetTask().setText("");

                mPresenter.refreshUi(Constants.MODE_PLAN_EDIT);

                // [TODO] 之後要增加一頁新的 category 可參考此處寫法
                // mPresenter.showSetTargetUi();

            } else if (v.getId() == R.id.imageview_plan_set_target_save) {  // Edit mode - complete

                // [TODO] 未來可以一次新增多個 target (多加一個小打勾，像 trello 新增卡片)
                // [TODO] 換成真正的 startTime, endTime
                // 1. 取得現在時間
                // 1.1 做成 startTime, endTime
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strStartTime = simpleDateFormat.format(curDate);

                // 1.2 update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleDateFormat.format(curDate);

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
                                mPlanningList.get(i).getCostTime(),
                                strCurrentTime));

                        Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
                    }

                    Logger.d(Constants.TAG, MSG +
                                    "Categroy: " + mPlanningList.get(i).getCategoryName() +
                                    "TaskName: " + mPlanningList.get(i).getTaskName() +
                                    "CostTime: " + mPlanningList.get(i).getCostTime());
                }

                // 2.2 再把最新 add 的目標加在最後
                // [TODO] 此處需判斷每個字串是否為空，還有對輸入的時間做檢查
                targetList.add(new TimePlanningTable(
                        Constants.MODE_PERIOD,
                        getEditTextSetTargetCategory().getText().toString().trim(),
                        getEditTextSetTargetTask().getText().toString().trim(),
                        strStartTime,
                        strStartTime,
                        "8 hr",
                        strCurrentTime
                ));

                // 3. send asyncTask to update data
                mPresenter.saveTargetResults(targetList, deleteTargetList);

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.imageview_plan_set_target_cancel) { // Edit mode - cancel

                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);
            }
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView() {
            // [TODO] 計算剩下幾個小時並顯示在畫面上
//            getTextviewPlanTopRemainingTime().setText("顯示剩多少小時");

            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {

                mConstraintLayoutPlanTopItem.setVisibility(View.VISIBLE);
                mConstraintLayoutPlanSetTarget.setVisibility(View.GONE);

            } else { // getIntPlanMode() == Constants.MODE_PLAN_EDIT

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
}
