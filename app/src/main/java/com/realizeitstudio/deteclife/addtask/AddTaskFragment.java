package com.realizeitstudio.deteclife.addtask;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/14
 *
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment implements AddTaskContract.View, View.OnClickListener {

    private static final String MSG = "AddTaskFragment: ";
    private AddTaskContract.Presenter mPresenter;

    private ConstraintLayout mConstraintLayoutAddItemEditMode;
    private TextView mTextviewAddItemCategory;
    private EditText mEdittextAddItemTask;

    private FrameLayout mFrameLayoutAddItemIcon;
    private FrameLayout mFrameLayoutAddItemIconHint;

    private ImageView mImageviewAddItemIcon;
    private ImageView mImageviewAddItemIconHint;

    private boolean isRefresh = true;


    // Icon information
    private String mStrSelectedIconName;
    private String mStrIconColor;


    public AddTaskFragment() {
        // Required empty public constructor
    }

    public static AddTaskFragment newInstance() {
        return new AddTaskFragment();
    }

    @Override
    public void setPresenter(AddTaskContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] AddTaskFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);

        // Set Category
        mTextviewAddItemCategory = root.findViewById(R.id.textview_addtask_editmode_category);
        mTextviewAddItemCategory.setOnClickListener(this);

        // Set Task
        mEdittextAddItemTask = root.findViewById(R.id.edittext_addtask_editmode_task);
        mEdittextAddItemTask.setOnClickListener(this);

        // Set Icon
        mImageviewAddItemIcon = root.findViewById(R.id.imageview_addtask_editmode_icon);
        mFrameLayoutAddItemIcon = root.findViewById(R.id.framelayout_addtask_editmode_icon);
        mFrameLayoutAddItemIcon.setOnClickListener(this);

        mImageviewAddItemIconHint = root.findViewById(R.id.imageview_addtask_editmode_category_hint);
        mFrameLayoutAddItemIconHint = root.findViewById(R.id.framelayout_addtask_editmode_category_hint);
        mFrameLayoutAddItemIconHint.setOnClickListener(this);

        mConstraintLayoutAddItemEditMode = root.findViewById(R.id.constraintlayout_addtask_editmode);

        ((ImageView) root.findViewById(R.id.imageview_addtask_editmode_save)).setOnClickListener(this);
        ((ImageView) root.findViewById(R.id.imageview_addtask_editmode_cancel)).setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(); // mPresenter.start();
    }

    // reset UI to default data
    private void init() {

        mStrSelectedIconName = Constants.DEFAULT_TASK_ICON;
        mStrIconColor = Constants.DEFAULT_TASK_COLOR;

        // category label
        getTextviewAddItemCategory().setText(TimeManagementApplication.getAppContext().getResources().getString(R.string.default_category_hint));

        GradientDrawable gradientDrawable = (GradientDrawable) getTextviewAddItemCategory().getBackground();
        gradientDrawable.setColor(Color.parseColor(mStrIconColor));

        // task name
        getEdittextAddItemTask().setText("");

        // icon
        getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(mStrSelectedIconName));
        getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

        // icon background
        gradientDrawable = (GradientDrawable) getFrameLayoutAddItemIcon().getBackground();
        gradientDrawable.setColor(Color.parseColor(mStrIconColor));

        getImageviewAddItemIconHint().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色

        // [TODO] color
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = " + hidden);

        if (hidden) {  // 不在最前端介面顯示 (被 hide())
            ;
        } else {  //重新顯示到最前端 (被 show())
            Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = false => SHOW");

            if (isRefresh()) {
//            mPresenter.start();
                init();

            } else {    // false 表示正在新增 category，有收到回傳的 category
                setRefresh(true);   // 事件完成，下次再進 onHiddenChanged 就要更新畫面
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageview_addtask_editmode_save) {  // Edit mode - complete

            // [TODO] 未來可以一次新增多個 task (多加一個小打勾，像 trello 新增卡片)
            // 1. 取得現在時間當作 update_date
            Date curDate = new Date();
            SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
            // 透過SimpleDateFormat的format方法將 Date 轉為字串
            String strUpdateTime = simpleUpdateDateFormat.format(curDate);

            // [Edit/Delete]
            // 2. 新增兩個 List 以 (1) 存放要存回 database 的資料 (2) 要從 database 刪除的資料
            List<TaskDefineTable> taskList  = new ArrayList<>();
            List<TaskDefineTable> deleteTaskList = new ArrayList<>();
//
//            // 2.1 先針對現有所有目標清單做出 List<TaskDefineTable> 物件
//            for (int i = 0; i < mCategoryTaskList.size(); ++i) {
//
//                // only handle "Task" item in mCategoryTaskList array
//                if (Constants.ITEM_TASK.equals(mCategoryTaskList.get(i).getItemCatg())) {
//
//                    // if user decides to delete this item, then delete from database
//                    if (isDeleteArray[i] == true) { // only could delete task item
//
//                        TaskDefineTable item = new TaskDefineTable(
//                                mCategoryTaskList.get(i).getCategoryName(),
//                                mCategoryTaskList.get(i).getTaskName(),
//                                mCategoryTaskList.get(i).getTaskColor(),
//                                mCategoryTaskList.get(i).getTaskIcon(),
//                                mCategoryTaskList.get(i).getTaskPriority(),
//                                false,
//                                strUpdateTime);
//
//                        deleteTaskList.add(item);
//
//                        Logger.d(Constants.TAG, MSG + "Delete item: ");
//                        item.logD();
//
//                    } else {
//                        // else add in database
//
//                        TaskDefineTable item = new TaskDefineTable(
//                                mCategoryTaskList.get(i).getCategoryName(),
//                                mCategoryTaskList.get(i).getTaskName(),
//                                mCategoryTaskList.get(i).getTaskColor(),
//                                mCategoryTaskList.get(i).getTaskIcon(),
//                                mCategoryTaskList.get(i).getTaskPriority(),
//                                false,
//                                strUpdateTime);
//
//                        taskList.add(item);
//
//                        Logger.d(Constants.TAG, MSG + "Add/Edit item: ");
//                        item.logD();
//                    }
//                }
//            }


            // 2.2 把新 add 的 task 加在 List 最後
            // 檢查使用者是否有每個欄位確實填上資料，若有任何一個欄位沒填，則跳出 Toast 提醒
            if (TimeManagementApplication.getAppContext().getResources().getString(R.string.default_category_hint)
                    .equals(getTextviewAddItemCategory().getText().toString().trim())
                    || ("").equals(getEdittextAddItemTask().getText().toString().trim())
                    || Constants.DEFAULT_TASK_ICON.equals(mStrIconColor)) {

                Toast.makeText(getActivity(), Constants.TOAST_ADD_TASK_FAIL, Toast.LENGTH_SHORT).show();

            } else {    // 如果都有輸入，就可以新增項目

                TaskDefineTable item = new TaskDefineTable(
                        getTextviewAddItemCategory().getText().toString().trim(),
                        getEdittextAddItemTask().getText().toString().trim(),
                        mStrIconColor,
                        mStrSelectedIconName,
                        20,
                        true,
                        strUpdateTime);

                taskList.add(item);

                Logger.d(Constants.TAG, MSG + "Add task: ");
                item.logD();

                // 3. send asyncTask to update data
                mPresenter.saveTaskResults(taskList, deleteTaskList);
            }

        } else if (v.getId() == R.id.imageview_addtask_editmode_cancel) { // Edit mode - cancel

            mPresenter.addTaskComplete();

        } else if (v.getId() == R.id.textview_addtask_editmode_category
                || v.getId() == R.id.framelayout_addtask_editmode_category_hint) {

            showCategoryListDialog();

        } else if (v.getId() == R.id.framelayout_addtask_editmode_icon) {

            showIconPickerDialog();
        }
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


    // ****** Icon Picker Dialog ****** //

    // 從 Fragment 叫起 Dialog 不需要經過 presenter
    // show Icon Picker Dialog
    public void showIconPickerDialog() {

        mPresenter.showIconPickerDialog(mStrIconColor);
    }


    public void showIconSelected(IconDefineTable bean) {

        getImageviewAddItemIcon().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(bean.getIconName()));
        getImageviewAddItemIcon().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
        mStrSelectedIconName = bean.getIconName();
    }


    // ****** Category Picker Dialog ****** //
    @Override
    public void showCategoryListDialog() {

        Logger.d(Constants.TAG, MSG + "showCategoryListDialog: ");
        ((MainActivity) getActivity()).transToCategoryList();
    }


    // 由 main presenter 傳入在 tasklistFragment 中選擇的 task
    public void completeSelectCategory(GetCategoryTaskList bean) {

        setRefresh(false);

        Logger.d(Constants.TAG, MSG + "completeSelectCategory => select category: ");
        bean.logD();

        getTextviewAddItemCategory().setText(bean.getCategoryName());

        GradientDrawable gradientDrawable = (GradientDrawable) getTextviewAddItemCategory().getBackground();
        gradientDrawable.setColor(Color.parseColor(bean.getCategoryColor()));
    }

    // press backkey on category page (without choose any category)
    // task page shouldn't refresh
    public void backFromCategory() {

        setRefresh(false);
        Logger.d(Constants.TAG, MSG + "backFromCategory");
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

}
