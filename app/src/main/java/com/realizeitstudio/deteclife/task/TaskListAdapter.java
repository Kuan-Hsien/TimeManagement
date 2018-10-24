package com.realizeitstudio.deteclife.task;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by Ken on 2018/10/14
 */
public class TaskListAdapter extends RecyclerView.Adapter {
    private static final String MSG = "TaskListAdapter: ";

    private TaskListContract.Presenter mPresenter;
    private List<GetCategoryTaskList> mTaskList;
    private boolean[] isDeleteArray;
    private int mIntTaskMode;



    AddItemViewHolder mAddItemViewHolder;



    public TaskListAdapter(List<GetCategoryTaskList> bean, TaskListContract.Presenter presenter) {

        mPresenter = presenter;
        setIntTaskMode(Constants.MODE_PLAN_VIEW);
        mTaskList = new ArrayList<>();

        for( int i = 0 ; i < bean.size() ; ++i ) {
            this.mTaskList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);

        if (viewType == Constants.VIEWTYPE_ADD_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_add_task, parent, false);

            mAddItemViewHolder = new AddItemViewHolder(view);
            return mAddItemViewHolder;

        } else if (viewType == Constants.VIEWTYPE_CATEGORY) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_item_category, parent, false);
            return new CategoryItemViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_item_task, parent, false);
            return new TaskItemViewHolder(view);

        }
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mTaskList.get(position));

        if (holder instanceof AddItemViewHolder) {

            ((AddItemViewHolder) holder).bindView();

        } else if (holder instanceof CategoryItemViewHolder) {

            ((CategoryItemViewHolder) holder).bindView(mTaskList.get(position), position);

        } else {

            ((TaskItemViewHolder) holder).bindView(mTaskList.get(position), position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mTaskList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_CATEGORY : Constants.VIEWTYPE_TASK));

        if (position == mTaskList.size()) { // last item would be add-item-layout

            return Constants.VIEWTYPE_ADD_ITEM;

        } else if (Constants.ITEM_CATEGORY.equals(mTaskList.get(position).getItemCatg())) { // Category item

            return Constants.VIEWTYPE_CATEGORY;

        } else { // Constants.ITEM_TASK.equals(mTaskList.get(position).getItemCatg())   // Task item

            return Constants.VIEWTYPE_TASK;
        }
    }


    public void updateData(List<GetCategoryTaskList> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mTaskList.clear();

        for (int i = 0 ; i < bean.size() ; ++i) {
            mTaskList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }

    
    
    
    
    
    
    
    

    public void refreshUiMode(int mode) {
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "VIEW_MODE" : "EDIT_MODE"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            int intArraySize = mTaskList.size();

            // 1. [Delete] initialization
            isDeleteArray = null;
            isDeleteArray = new boolean[intArraySize];
            Arrays.fill(isDeleteArray, false);  // in Java, boolean array default set false to all items. this setting is only for reading
        }

        setIntTaskMode(mode);
        notifyDataSetChanged();
    }


    // ViewHolder of Task
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class TaskItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private ImageView mImageviewCategeoryColorLabel;
        private ImageView mImageviewTaskIcon;
        private TextView mTextviewTaskName;
        private TextView mTextviewCategoryName;
        private FrameLayout mFrameLayoutTaskColor;
        private ConstraintLayout mConstraintLayoutTaskItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

//        public ImageView getImageviewCategeoryColorLabel() {
//            return mImageviewCategeoryColorLabel;
//        }

        public ImageView getImageviewTaskIcon() {
            return mImageviewTaskIcon;
        }

        public TextView getTextviewTaskName() {
            return mTextviewTaskName;
        }

        public TextView getTextviewCategoryName() {
            return mTextviewCategoryName;
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
//            mImageviewCategeoryColorLabel = (ImageView) v.findViewById(R.id.imageview_categorytask_categorycolor);
            mImageviewTaskIcon = (ImageView) v.findViewById(R.id.imageview_categorytask_task_icon);
            mTextviewTaskName = (TextView) v.findViewById(R.id.textview_categorytask_task_name);
            mTextviewCategoryName = (TextView) v.findViewById(R.id.textview_categorytask_category_name);
            mFrameLayoutTaskColor = (FrameLayout) v.findViewById(R.id.framelayout_categorytask_task_icon);
            mConstraintLayoutTaskItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_categorytask_task_item);
            mConstraintLayoutTaskItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_categorytask_task_item) {

                mPresenter.showTaskSelected(mTaskList.get(getCurrentPosition()));
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
//                Logger.d(Constants.TAG, MSG + "delete " + mTaskList.get(getCurrentPosition()).getTaskName() + " status: " + isDeleteArray[getCurrentPosition()]);
            }
        }


        /**
         * call by onBindViewHolder
         */
        public void bindView(GetCategoryTaskList item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

            Logger.d(Constants.TAG, MSG + "bindView setColor: " + item.getTaskColor() + " Taskname: " + item.getTaskName());

//            getImageviewCategeoryColorLabel().setBackgroundColor(Color.parseColor(item.getCategoryColor()));
//            getImageviewCategeoryColorLabel().setVisibility(View.GONE);

            // set task icon color
//            getFrameLayoutTaskColor().setBackgroundColor(Color.parseColor(item.getTaskColor()));
            GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutTaskColor().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getTaskColor()));

            // set category label color
            gradientDrawable = (GradientDrawable) getTextviewCategoryName().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getCategoryColor()));

            getImageviewTaskIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getTaskIcon()));
            getImageviewTaskIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
            getTextviewTaskName().setText(item.getTaskName());
            getTextviewCategoryName().setText(item.getCategoryName());
            setPosition(pos);
        }

    }


    // ViewHolder of Category
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

    public class AddItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //** View Mode
        private ConstraintLayout mConstraintLayoutAddItemViewMode;

        //** Edit Mode
        private ConstraintLayout mConstraintLayoutAddItemEditMode;
        private TextView mTextviewAddItemCategory;
        private EditText mEdittextAddItemTask;

        private FrameLayout mFrameLayoutAddItemIcon;
        private FrameLayout mFrameLayoutAddItemIconHint;
        private ImageView mImageviewAddItemIcon;
        private ImageView mImageviewAddItemIconHint;

        private String mStrSelectedIconName = Constants.DEFAULT_TASK_ICON;
        private String mStrIconColor = Constants.DEFAULT_TASK_COLOR;

        public ConstraintLayout getConstraintLayoutAddItemViewMode() {
            return mConstraintLayoutAddItemViewMode;
        }

        public ConstraintLayout getConstraintLayoutAddItemEditMode() {
            return mConstraintLayoutAddItemEditMode;
        }

        public TextView getTextviewAddItemCategory() {
            return mTextviewAddItemCategory;
        }

        public EditText getEdittextAddItemTask() {
            return mEdittextAddItemTask;
        }

        public FrameLayout getFrameLayoutAddItemIcon() {
            return mFrameLayoutAddItemIcon;
        }

        public ImageView getImageviewAddItemIcon() {
            return mImageviewAddItemIcon;
        }

        public ImageView getImageviewAddItemIconHint() {
            return mImageviewAddItemIconHint;
        }

        public String getStrSelectedIconName() {
            return mStrSelectedIconName;
        }

        public String getStrIconColor() {
            return mStrIconColor;
        }

        public void setStrSelectedIconName(String strSelectedIconName) {
            mStrSelectedIconName = strSelectedIconName;
        }

        public void setStrIconColor(String strIconColor) {
            mStrIconColor = strIconColor;
        }

        public AddItemViewHolder(View v) {
            super(v);

            //** View Mode
            mConstraintLayoutAddItemViewMode = (ConstraintLayout) v.findViewById(R.id.constraintlayout_addtask_viewmode);
            mConstraintLayoutAddItemViewMode.setOnClickListener(this);

            //** Edit Mode
            // Set Category
            mTextviewAddItemCategory = (TextView) v.findViewById(R.id.textview_addtask_editmode_category);
            mTextviewAddItemCategory.setText(TimeManagementApplication.getAppContext().getResources().getString(R.string.default_category));
            mTextviewAddItemCategory.setOnClickListener(this);

            GradientDrawable gradientDrawable = (GradientDrawable) getTextviewAddItemCategory().getBackground();
            gradientDrawable.setColor(Color.parseColor(Constants.DEFAULT_CATEGORY_COLOR));

            // Set Task
            mEdittextAddItemTask = (EditText) v.findViewById(R.id.edittext_addtask_editmode_task);
            mEdittextAddItemTask.setOnClickListener(this);

            // Set Icon
            mImageviewAddItemIcon = v.findViewById(R.id.imageview_addtask_editmode_icon);
            getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(Constants.DEFAULT_TASK_ICON));

            mFrameLayoutAddItemIcon = v.findViewById(R.id.framelayout_addtask_editmode_icon);
            mFrameLayoutAddItemIcon.setOnClickListener(this);

            gradientDrawable = (GradientDrawable) getFrameLayoutAddItemIcon().getBackground();
            gradientDrawable.setColor(Color.parseColor(mStrIconColor));

            mImageviewAddItemIconHint = v.findViewById(R.id.imageview_addtask_editmode_category_hint);
            getImageviewAddItemIconHint().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

            mFrameLayoutAddItemIconHint = v.findViewById(R.id.framelayout_addtask_editmode_category_hint);
            mFrameLayoutAddItemIconHint.setOnClickListener(this);




            mConstraintLayoutAddItemEditMode = (ConstraintLayout) v.findViewById(R.id.constraintlayout_addtask_editmode);

            ((ImageView) v.findViewById(R.id.imageview_addtask_editmode_save)).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.imageview_addtask_editmode_cancel)).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_addtask_viewmode) {    // View mode

                mPresenter.refreshUi(Constants.MODE_PLAN_EDIT);

            } else if (v.getId() == R.id.imageview_addtask_editmode_save) {  // Edit mode - complete

                // [TODO] 未來可以一次新增多個 task (多加一個小打勾，像 trello 新增卡片)
                // 1. 取得現在時間當作 update_date
                Date curDate = new Date();
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strUpdateTime = simpleUpdateDateFormat.format(curDate);

                // 2. 新增兩個 List 以 (1) 存放要存回 database 的資料 (2) 要從 database 刪除的資料
                List<TaskDefineTable> taskList = new ArrayList<>();
                List<TaskDefineTable> deleteTaskList = new ArrayList<>();

                // 2.1 先針對現有所有目標清單做出 List<TaskDefineTable> 物件
                for (int i = 0 ; i < mTaskList.size() ; ++i) {

                    // only handle "Task" item in mTaskList array
                    if (Constants.ITEM_TASK.equals(mTaskList.get(i).getItemCatg())) {

                        // if user decides to delete this item, then delete from database
                        if (isDeleteArray[i] == true) { // only could delete task item

                            TaskDefineTable item = new TaskDefineTable(
                                    mTaskList.get(i).getCategoryName(),
                                    mTaskList.get(i).getTaskName(),
                                    mTaskList.get(i).getTaskColor(),
                                    mTaskList.get(i).getTaskIcon(),
                                    mTaskList.get(i).getTaskPriority(),
                                    false,
                                    strUpdateTime);

                            deleteTaskList.add(item);

                            Logger.d(Constants.TAG, MSG + "Delete item: ");
                            item.LogD();

                        } else {
                            // else add in database

                            TaskDefineTable item = new TaskDefineTable(
                                    mTaskList.get(i).getCategoryName(),
                                    mTaskList.get(i).getTaskName(),
                                    mTaskList.get(i).getTaskColor(),
                                    mTaskList.get(i).getTaskIcon(),
                                    mTaskList.get(i).getTaskPriority(),
                                    false,
                                    strUpdateTime);

                            taskList.add(item);

                            Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
                            item.LogD();
                        }
                    }
                }

                // 2.2 再把新 add 的 task 加在最後
                // [TODO] 此處需判斷每個字串是否為空，還有對輸入的時間做檢查
                if (getTextviewAddItemCategory().getText().toString().trim() != null &&
                        getEdittextAddItemTask().getText().toString().trim() != null) {

                    TaskDefineTable item = new TaskDefineTable(
                            getTextviewAddItemCategory().getText().toString().trim(),
                            getEdittextAddItemTask().getText().toString().trim(),
                            mStrIconColor,
                            mStrSelectedIconName,
                            100,
                            true,
                            strUpdateTime);

                    taskList.add(item);

                    Logger.d(Constants.TAG, MSG + "Add task: ");
                    item.LogD();
                }

                // 3. send asyncTask to update data
                mPresenter.saveTaskResults(taskList, deleteTaskList);

//                resetEditField();
                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.imageview_addtask_editmode_cancel) { // Edit mode - cancel

//                resetEditField();
                mPresenter.refreshUi(Constants.MODE_PLAN_VIEW);

            } else if (v.getId() == R.id.textview_addtask_editmode_category ||
                        v.getId() == R.id.framelayout_addtask_editmode_category_hint) {

                Logger.d(Constants.TAG, MSG + "click => textview_addtask_editmode_category");
                mPresenter.showCategoryListDialog();

            } else if (v.getId() == R.id.framelayout_addtask_editmode_icon) {

                mPresenter.showIconPickerDialog(mStrIconColor);
            }
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView() {

//            setPosition(pos);

            if (getIntTaskMode() == Constants.MODE_PLAN_VIEW) {

                mConstraintLayoutAddItemViewMode.setVisibility(View.VISIBLE);
                mConstraintLayoutAddItemEditMode.setVisibility(View.GONE);

            } else { // getIntTaskMode() == Constants.MODE_PLAN_EDIT

                mConstraintLayoutAddItemViewMode.setVisibility(View.GONE);
                mConstraintLayoutAddItemEditMode.setVisibility(View.VISIBLE);

                resetEditField();
            }
        }


        public void resetEditField() {

            // 切換為編輯模式預設內容
            getTextviewAddItemCategory().setText(TimeManagementApplication.getAppContext().getResources().getString(R.string.default_category));
            getEdittextAddItemTask().setText("");
            mStrSelectedIconName = Constants.DEFAULT_TASK_ICON;
            mStrIconColor = Constants.DEFAULT_TASK_COLOR;


            GradientDrawable gradientDrawable = (GradientDrawable) getFrameLayoutAddItemIcon().getBackground();
            gradientDrawable.setColor(Color.parseColor(mStrIconColor));

            getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(Constants.DEFAULT_TASK_ICON));
            getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
        }
    }


    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }


    // 由 main presenter 傳入在 tasklistFragment 中選擇的 task
    public void completeSelectCategory(GetCategoryTaskList bean) {

        Logger.d(Constants.TAG, MSG + "completeSelectCategory => select category: ");
        bean.LogD();

        mAddItemViewHolder.getTextviewAddItemCategory().setText(bean.getCategoryName());

        GradientDrawable gradientDrawable = (GradientDrawable) mAddItemViewHolder.getTextviewAddItemCategory().getBackground();
        gradientDrawable.setColor(Color.parseColor(bean.getCategoryColor()));

//        mGetCategoryTaskList = bean;
//        notifyDataSetChanged();
    }



    // ****** Icon Picker Dialog ****** //
    public void showIconSelected(IconDefineTable bean) {

        mAddItemViewHolder.getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(bean.getIconName()));
        mAddItemViewHolder.getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
        mAddItemViewHolder.setStrSelectedIconName(bean.getIconName());

    }

}
