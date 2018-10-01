package com.kuanhsien.timemanagement.task;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/9/30
 */
public class CategoryTaskListAdapter extends RecyclerView.Adapter {
    private static final String MSG = "CategoryTaskListAdapter: ";

    private CategoryTaskListContract.Presenter mPresenter;
    private List<GetCategoryTaskList> mCategoryTaskList;
    private boolean[] isDeleteArray;
    private int mIntPlanMode;

    public CategoryTaskListAdapter(List<GetCategoryTaskList> bean, CategoryTaskListContract.Presenter presenter) {

        mPresenter = presenter;
        setIntPlanMode(Constants.MODE_PLAN_VIEW);
        mCategoryTaskList = new ArrayList<>();

        for( int i = 0 ; i < bean.size() ; ++i ) {
            this.mCategoryTaskList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);

        if (viewType == Constants.VIEWTYPE_CATEGORY) {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_item_category, parent, false);
            return new CategoryItemViewHolder(view);
        } else {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_item_task, parent, false);
            return new TaskItemViewHolder(view);

        }
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mCategoryTaskList.get(position));

        if (holder instanceof CategoryItemViewHolder) {
            // detail ar
            ((CategoryItemViewHolder) holder).bindView(mCategoryTaskList.get(position), position);
        } else {
            // comments
            ((TaskItemViewHolder) holder).bindView(mCategoryTaskList.get(position), position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mCategoryTaskList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_CATEGORY : Constants.VIEWTYPE_TASK));

        if (Constants.ITEM_CATEGORY.equals(mCategoryTaskList.get(position).getItemCatg())) {

            return Constants.VIEWTYPE_CATEGORY;

        } else { // Constants.ITEM_TASK

            return Constants.VIEWTYPE_TASK;
        }

    }


    public void updateData(List<GetCategoryTaskList> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mCategoryTaskList.clear();

        for (int i = 0 ; i < bean.size() ; ++i) {
            mCategoryTaskList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }

//    public void refreshUiMode(int mode) {
//        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "VIEW_MODE" : "EDIT_MODE"));
//
//        // if user request to change to MODE_PLAN_EDIT
//        if (mode == Constants.MODE_PLAN_EDIT) {
//
//            int intArraySize = mCategoryTaskList.size();
//
//            // 1. [Delete] initialization
//            isDeleteArray = null;
//            isDeleteArray = new boolean[intArraySize];
//            Arrays.fill(isDeleteArray, false);  // in Java, boolean array default set false to all items. this setting is only for reading
//        }
//
//        setIntPlanMode(mode);
//        notifyDataSetChanged();
//    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class TaskItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        private ImageView mImageviewCategeoryColorLabel;
        private ImageView mImageviewTaskIcon;
        private TextView mTextviewTaskName;
        private FrameLayout mFrameLayoutTaskColor;
        private ConstraintLayout mConstraintLayoutTaskItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ImageView getImageviewCategeoryColorLabel() {
            return mImageviewCategeoryColorLabel;
        }

        public ImageView getImageviewTaskIcon() {
            return mImageviewTaskIcon;
        }

        public TextView getTextviewTaskName() {
            return mTextviewTaskName;
        }

        public FrameLayout getFrameLayoutTaskColor() {
            return mFrameLayoutTaskColor;
        }

        public ConstraintLayout getConstraintLayoutTaskItem() {
            return mConstraintLayoutTaskItem;
        }

        public TaskItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mImageviewCategeoryColorLabel = (ImageView) v.findViewById(R.id.imageview_categorytask_categorycolor);
            mImageviewTaskIcon = (ImageView) v.findViewById(R.id.imageview_categorytask_task_icon);
            mTextviewTaskName = (TextView) v.findViewById(R.id.textview_categorytask_task_name);
            mFrameLayoutTaskColor = (FrameLayout) v.findViewById(R.id.framelayout_categorytask_task_icon);
            mConstraintLayoutTaskItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_categorytask_task_item);
            mConstraintLayoutTaskItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_categorytask_task_item) {

                mPresenter.showCategoryTaskSelected(mCategoryTaskList.get(getCurrentPosition()));
//                // if original delete flag is on, than cancel. (change background color to white)
//                if (isDeleteArray[getCurrentPosition()] == true) {
//
//                    isDeleteArray[getCurrentPosition()] = false;
//                    mFrameLayoutTaskColor.setBackground(TimeManagementApplication.getAppContext().getDrawable(android.R.color.white));
//
//                } else {
//                    // if original delete flag is off, than delete. (change background color with drawable)
//
//                    isDeleteArray[getCurrentPosition()] = true;
//                    mFrameLayoutTaskColor.setBackground(TimeManagementApplication.getAppContext().getDrawable(R.drawable.toolbar_background));
//
//                }
//
//                Logger.d(Constants.TAG, MSG + "delete " + mCategoryTaskList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);
            }
        }


        /**
         * call by onBindViewHolder
         */
        public void bindView(GetCategoryTaskList item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());
            getImageviewCategeoryColorLabel().setBackgroundColor(Color.parseColor(item.getCategoryColor()));

            getFrameLayoutTaskColor().setBackgroundColor(Color.parseColor(item.getTaskColor()));
            getImageviewTaskIcon().setImageDrawable(TimeManagementApplication.getIconResource(item.getTaskIcon()));
            getTextviewTaskName().setText(item.getTaskName());

            setPosition(pos);

//            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {
//
//                getImageviewPlanTaskDeleteHint().setVisibility(View.GONE);
//
//            } else { // getIntPlanMode() == Constants.MODE_PLAN_EDIT
//
//                getImageviewPlanTaskDeleteHint().setVisibility(View.VISIBLE);
//            }
        }

    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class CategoryItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        //** View Mode
        private ConstraintLayout mConstraintLayoutCategoryItem;
        private ImageView mImageviewCategorySeperation;
        private ImageView mImageviewCategeoryColor;
        private TextView mTextviewCategoryName;
        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ConstraintLayout getConstraintLayoutCategoryItem() {
            return mConstraintLayoutCategoryItem;
        }

        public ImageView getImageviewCategorySeperation() {
            return mImageviewCategorySeperation;
        }

        public ImageView getImageviewCategeoryColor() {
            return mImageviewCategeoryColor;
        }

        public TextView getTextviewCategoryName() {
            return mTextviewCategoryName;
        }

        public CategoryItemViewHolder(View v) {
            super(v);

            //** View Mode
            mConstraintLayoutCategoryItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_categorytask_category_item);
            mImageviewCategorySeperation = (ImageView) v.findViewById(R.id.imageview_categorytask_separationline);
            mImageviewCategeoryColor = (ImageView) v.findViewById(R.id.imageview_categorytask_category_color);
            mTextviewCategoryName = (TextView) v.findViewById(R.id.textview_categorytask_category_name);

        }

        /**
         * call by onBindViewHolder
         */
        public void bindView(GetCategoryTaskList item , int pos) {

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getCategoryColor() + " CategoryName: " + item.getCategoryName());

            getConstraintLayoutCategoryItem().setBackgroundColor(Color.parseColor(item.getCategoryColor()));
            getImageviewCategeoryColor().setBackgroundColor(Color.parseColor(item.getCategoryColor()));
            getTextviewCategoryName().setText(item.getCategoryName());

//            getImageviewCategorySeperation().setBackgroundColor(Color.parseColor(item.getCategoryColor()));

            setPosition(pos);

//            if (getIntPlanMode() == Constants.MODE_PLAN_VIEW) {
//
//                mConstraintLayoutPlanTopItem.setVisibility(View.VISIBLE);
//                mConstraintLayoutPlanSetTarget.setVisibility(View.GONE);
//
//            } else { // getIntPlanMode() == Constants.MODE_PLAN_EDIT
//
//                mConstraintLayoutPlanTopItem.setVisibility(View.GONE);
//                mConstraintLayoutPlanSetTarget.setVisibility(View.VISIBLE);
//            }
        }
    }


    public int getIntPlanMode() {
        return mIntPlanMode;
    }

    public void setIntPlanMode(int intPlanMode) {
        mIntPlanMode = intPlanMode;
    }
}
