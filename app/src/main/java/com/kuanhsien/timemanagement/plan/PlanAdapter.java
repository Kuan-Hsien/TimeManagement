package com.kuanhsien.timemanagement.plan;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/9/24
 */
public class PlanAdapter extends RecyclerView.Adapter {
    private static final String MSG = "PlanAdapter: ";

    private PlanContract.Presenter mPresenter;
    private List<GetTaskWithPlanTime> mPlanningList;


    public PlanAdapter(List<GetTaskWithPlanTime> bean, PlanContract.Presenter presenter) {
        mPresenter = presenter;

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

        for (int i = 0 ; i < bean.size() ; ++i) {
            mPlanningList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanMainItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private ImageView mImageviewPlanTaskIcon;
        private TextView mTextviewPlanTaskName;
        private TextView mTextviewPlanTaskCostTime;
        private ConstraintLayout mConstraintLayoutPlanMainItem;

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

        public PlanMainItemViewHolder(View v) {
            super(v);
            mConstraintLayoutPlanMainItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_main_item);
            mTextviewPlanTaskName = (TextView) v.findViewById(R.id.textview_plan_task_name);
            mTextviewPlanTaskCostTime = (TextView) v.findViewById(R.id.textview_plan_task_cost_time);

            mPosition = 0;

            mConstraintLayoutPlanMainItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // 點擊一個已經設定好的目標，也許未來要可以編輯或是刪除等等
                    // add a new target
                }
            });
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView(GetTaskWithPlanTime item , int pos) {
            // [TODO] 把相對應位置的 task 顯示在此 viewHolder
            getTextviewPlanTaskName().setText(item.getTaskName());
            getTextviewPlanTaskCostTime().setText(item.getCostTime());
            setPosition(pos);
        }
    }




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PlanTopItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

//        private ImageView mImageviewPlanTopAddItem;
        private TextView mTextviewPlanTopAddItem;
        private TextView mTextviewPlanTopRemainingTime;
        private ConstraintLayout mConstraintLayoutPlanTopItem;

        public TextView getTextviewPlanTopAddItem() {
            return mTextviewPlanTopAddItem;
        }

        public TextView getTextviewPlanTopRemainingTime() {
            return mTextviewPlanTopRemainingTime;
        }

        public ConstraintLayout getConstraintLayoutPlanTopItem() {
            return mConstraintLayoutPlanTopItem;
        }

        public PlanTopItemViewHolder(View v) {
            super(v);

//            mTextviewPlanTopAddItem = (TextView) v.findViewById(R.id.textview_plan_top_add_item);
            mTextviewPlanTopRemainingTime = (TextView) v.findViewById(R.id.textview_plan_top_remaining_time);
            mConstraintLayoutPlanTopItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_plan_top_item);

            mConstraintLayoutPlanTopItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // 新增一個目標 to Database (Room)
                    // add a new target


                }
            });
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView() {
            // [TODO] 計算剩下幾個小時並顯示在畫面上
//            getTextviewPlanTopRemainingTime().setText("顯示剩多少小時");

        }


    }
}
