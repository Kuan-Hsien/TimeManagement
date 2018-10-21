package com.realizeitstudio.deteclife.record;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by Ken on 2018/10/07
 */
public class RecordAdapter extends RecyclerView.Adapter {
    private static final String MSG = "RecordAdapter: ";

    private RecordContract.Presenter mPresenter;
    private List<GetCategoryTaskList> mCategoryTaskList;
    private boolean[] isDeleteArray;
    private int mIntTaskMode;

    private TimeTracingTable mCurrentItem;



    public RecordAdapter(List<GetCategoryTaskList> bean, RecordContract.Presenter presenter) {

        mPresenter = presenter;
        setIntTaskMode(Constants.MODE_PLAN_VIEW);
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

        if (viewType == Constants.VIEWTYPE_ADD_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_add_task, parent, false);
            return new AddItemViewHolder(view);

        } else if (viewType == Constants.VIEWTYPE_CATEGORY) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_item_category, parent, false);
            return new CategoryItemViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_choose_task, parent, false);
            return new TaskItemViewHolder(view);

        }
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mCategoryTaskList.get(position));

        if (holder instanceof AddItemViewHolder) {

            ((AddItemViewHolder) holder).bindView();

        } else if (holder instanceof CategoryItemViewHolder) {

            ((CategoryItemViewHolder) holder).bindView(mCategoryTaskList.get(position), position);

        } else {

            ((TaskItemViewHolder) holder).bindView(mCategoryTaskList.get(position), position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mCategoryTaskList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_CATEGORY : Constants.VIEWTYPE_TASK));

        if (position == mCategoryTaskList.size()) { // last item would be add-item-layout

            return Constants.VIEWTYPE_ADD_ITEM;

        } else if (Constants.ITEM_CATEGORY.equals(mCategoryTaskList.get(position).getItemCatg())) { // Category item

            return Constants.VIEWTYPE_CATEGORY;

        } else { // Constants.ITEM_TASK.equals(mCategoryTaskList.get(position).getItemCatg())   // Task item

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

    public void updateCurrentTraceItem(TimeTracingTable bean) {
        Logger.d(Constants.TAG, MSG + "updateCurrentTraceItem");

         mCurrentItem = new TimeTracingTable(bean);
    }


    public void refreshUiMode(int mode) {
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "VIEW_MODE" : "EDIT_MODE"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            int intArraySize = mCategoryTaskList.size();

            // 1. [Delete] initialization
            isDeleteArray = null;
            isDeleteArray = new boolean[intArraySize];
            Arrays.fill(isDeleteArray, false);  // in Java, boolean array default set false to all items. this setting is only for reading
        }

        setIntTaskMode(mode);
        notifyDataSetChanged();
    }

    // [TODO] ViewHolder of Category
    public class CategoryItemViewHolder extends RecyclerView.ViewHolder {

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
        }
    }

    // ViewHolder of Task
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class TaskItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private ImageView mImageviewCategeoryColorLabel;
        private ImageView mImageviewTaskIcon;
        private TextView mTextviewTaskName;
//        private FrameLayout mFrameLayoutTaskColor;
        private ConstraintLayout mConstraintLayoutTaskItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ImageView getImageviewTaskIcon() {
            return mImageviewTaskIcon;
        }

        public TextView getTextviewTaskName() {
            return mTextviewTaskName;
        }

        public ConstraintLayout getConstraintLayoutTaskItem() {
            return mConstraintLayoutTaskItem;
        }

        public TaskItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
//            mImageviewCategeoryColorLabel = (ImageView) v.findViewById(R.id.imageview_categorytask_categorycolor);
            mImageviewTaskIcon = (ImageView) v.findViewById(R.id.imageview_record_choose_task_icon);
            mTextviewTaskName = (TextView) v.findViewById(R.id.textview_record_choose_task_name);
//            mFrameLayoutTaskColor = (FrameLayout) v.findViewById(R.id.framelayout_categorytask_task_icon);
            mConstraintLayoutTaskItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_record_choose_task_item);
            mConstraintLayoutTaskItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_record_choose_task_item) {
                // 表示選擇了此類別，此時要針對上一筆 (現在的 Task) 停止，並開始現在這筆

                // current time as startTime
                Date curDate = new Date();
                Long longCurTime = curDate.getTime(); // 把毫秒 (Long) 存起來

                // update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);


                List<TimeTracingTable> itemList = new ArrayList<>();

                // (1) insert 上一筆的 endtime + costtime
                TimeTracingTable lastItem = new TimeTracingTable(
                        mCurrentItem.getVerNo(),
                        mCurrentItem.getCategoryName(),
                        mCurrentItem.getTaskName(),
                        mCurrentItem.getStartTime(),
                        longCurTime,
                        longCurTime - mCurrentItem.getStartTime(),
                        strCurrentTime
                );
                Logger.d(Constants.TAG, MSG + "stop current task: ");
                lastItem.LogD();
                itemList.add(lastItem);

                // (2) insert 新一筆但沒有 endtime
                TimeTracingTable newItem = new TimeTracingTable(
                        mCurrentItem.getVerNo(),
                        mCategoryTaskList.get(getCurrentPosition()).getCategoryName(),
                        mCategoryTaskList.get(getCurrentPosition()).getTaskName(),
                        longCurTime,
                        null,
                        null,
                        strCurrentTime
                );
                Logger.d(Constants.TAG, MSG + "start new task: ");
                newItem.LogD();
                itemList.add(newItem);


                // (3) 交給 presenter 塞進資料庫
                mPresenter.saveTraceResults(itemList, mCurrentItem.getVerNo(), mCurrentItem.getVerNo(), mCurrentItem.getCategoryName(), mCurrentItem.getTaskName());
            }
        }


        /**
         * call by onBindViewHolder
         */
        public void bindView(GetCategoryTaskList item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());
//            getImageviewCategeoryColorLabel().setBackgroundColor(Color.parseColor(item.getCategoryColor()));

//            getFrameLayoutTaskColor().setBackgroundColor(Color.parseColor(item.getTaskColor()));


//            getConstraintLayoutTaskItem().setBackgroundColor(Color.parseColor(item.getTaskColor()));

            GradientDrawable gradientDrawable = (GradientDrawable) getConstraintLayoutTaskItem().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getTaskColor()));

            getImageviewTaskIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getTaskIcon()));
            getImageviewTaskIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
            getTextviewTaskName().setText(item.getTaskName());
            setPosition(pos);
        }
    }


    public class AddItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageviewTaskIcon;
        private TextView mTextviewTaskName;
        private ConstraintLayout mConstraintLayoutTaskItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ImageView getImageviewTaskIcon() {
            return mImageviewTaskIcon;
        }

        public TextView getTextviewTaskName() {
            return mTextviewTaskName;
        }

        public ConstraintLayout getConstraintLayoutTaskItem() {
            return mConstraintLayoutTaskItem;
        }

        public AddItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
//            mImageviewCategeoryColorLabel = (ImageView) v.findViewById(R.id.imageview_categorytask_categorycolor);
            mImageviewTaskIcon = (ImageView) v.findViewById(R.id.imageview_record_add_task_icon);
            mTextviewTaskName = (TextView) v.findViewById(R.id.textview_record_add_task_name);
//            mFrameLayoutTaskColor = (FrameLayout) v.findViewById(R.id.framelayout_categorytask_task_icon);
            mConstraintLayoutTaskItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_record_add_task_item);
            mConstraintLayoutTaskItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_record_add_task_item) {
                // [TODO] 進入新增 Task 事件

                mPresenter.showAddTaskUi();
//                mPresenter.showCategoryTaskSelected(mCategoryTaskList.get(getCurrentPosition()));



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
        public void bindView() {

//            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());
//            getImageviewCategeoryColorLabel().setBackgroundColor(Color.parseColor(item.getCategoryColor()));

//            getFrameLayoutTaskColor().setBackgroundColor(Color.parseColor(item.getTaskColor()));

            // 白底黑字 (7F 表示透明度 50%)
//            getConstraintLayoutTaskItem().setBackgroundColor(Color.parseColor("#7FFFFFFF"));
            getImageviewTaskIcon().setColorFilter(Color.parseColor("#000000")); // 設定圖案線條顏色
//            getTextviewTaskName().setTextColor(Color.parseColor("#000000"));

            // 黑底白字
//            getConstraintLayoutTaskItem().setBackgroundColor(Color.parseColor("#000000"));
//            getImageviewTaskIcon().setColorFilter(Color.parseColor("#FFFFFF"));
//            getTextviewTaskName().setTextColor(Color.parseColor("#FFFFFF"));

        }

    }


    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }
}