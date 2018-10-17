package com.kuanhsien.timemanagement.addtask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.dml.GetResultDailySummary;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.task.CategoryTaskListAdapter;
import com.kuanhsien.timemanagement.task.CategoryTaskListContract;
import com.kuanhsien.timemanagement.task.CategoryTaskListPresenter;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

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

//    private CategoryTaskListContract.Presenter mCategroyTaskListContractPresenter;
//    private CategoryTaskListAdapter mCategoryTaskListAdapter;
//    private AlertDialog mDialog;

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
        mTextviewAddItemCategory.setText(Constants.CATEGORY_OTHERS);
        mTextviewAddItemCategory.setOnClickListener(this);

        // Set Task
        mEdittextAddItemTask = root.findViewById(R.id.edittext_addtask_editmode_task);
        mEdittextAddItemTask.setOnClickListener(this);

        mConstraintLayoutAddItemEditMode = root.findViewById(R.id.constraintlayout_addtask_editmode);

        ((ImageView) root.findViewById(R.id.imageview_addtask_editmode_save)).setOnClickListener(this);
        ((ImageView) root.findViewById(R.id.imageview_addtask_editmode_cancel)).setOnClickListener(this);


        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mPresenter.start();
        init();

    }

    private void init() {
        mTextviewAddItemCategory.setText(Constants.CATEGORY_OTHERS);
        mEdittextAddItemTask.setText("");
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
//            mPresenter.start();
            init();
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
//            for (int i = 0 ; i < mCategoryTaskList.size() ; ++i) {
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
//                        item.LogD();
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
//                        item.LogD();
//                    }
//                }
//            }


            // 2.2 把新 add 的 task 加在最後
            // [TODO] 此處需判斷每個字串是否為空
            if (getTextviewAddItemCategory().getText().toString().trim() != null &&
                    getEdittextAddItemTask().getText().toString().trim() != null) {

                TaskDefineTable item = new TaskDefineTable(
//                            getTextviewAddItemCategory().getText().toString().trim(),
                        getTextviewAddItemCategory().getText().toString().trim(),
                        getEdittextAddItemTask().getText().toString().trim(),
                        "#134D78",
                        "icon_drunk",
                        20,
                        true,
                        strUpdateTime);

                taskList.add(item);

                Logger.d(Constants.TAG, MSG + "Add task: ");
                item.LogD();
            }

            // 3. send asyncTask to update data
            mPresenter.saveTaskResults(taskList, deleteTaskList);

//            mPresenter.refreshCategoryTaskUi(Constants.MODE_PLAN_VIEW);

        } else if (v.getId() == R.id.imageview_addtask_editmode_cancel) { // Edit mode - cancel

            mPresenter.addTaskComplete();
//            mPresenter.refreshCategoryTaskUi(Constants.MODE_PLAN_VIEW);

        } else if (v.getId() == R.id.textview_addtask_editmode_category) {

            Toast.makeText(TimeManagementApplication.getAppContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
//            mPresenter.showCategoryListDialog();
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


//    @Override
//    public void setCategoryTaskListPresenter(CategoryTaskListContract.Presenter presenter) {
//        mCategroyTaskListContractPresenter = checkNotNull(presenter);
//    }
//    @Override
//    public void showTaskListDialog() {
//
//        // ****** 用預設的 mDialog 介面 ******
//        final String[] list_String = {"1", "2", "3", "4", "5"};
//
////        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////        builder.setTitle("標題");
////        builder.setIcon(R.mipmap.ic_launcher);
////        builder.setItems(list_String, new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface mDialog, int which) {    // 傳回的 which 表示點擊列表的第幾項
////                Toast.makeText(getActivity(), "點擊: " + list_String[which], Toast.LENGTH_SHORT).show();
////            }
////        });
////
////        AlertDialog mDialog = builder.create();
////        mDialog.show();
//
//        if (mCategroyTaskListContractPresenter == null) {
//            mCategroyTaskListContractPresenter = new CategoryTaskListPresenter(this);
//        }
//
//
//        // ****** 用自定義的 mDialog 介面 ******
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        View view = View.inflate(getActivity(), R.layout.dialog_categorytask_list, null);
//
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category_task_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
//
//        mCategoryTaskListAdapter = new CategoryTaskListAdapter(new ArrayList<GetCategoryTaskList>(), mCategroyTaskListContractPresenter);
//        recyclerView.setAdapter(mCategoryTaskListAdapter);
////        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));
//
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                mCategroyTaskListContractPresenter.onScrollStateChanged(
//                        recyclerView.getLayoutManager().getChildCount(),
//                        recyclerView.getLayoutManager().getItemCount(),
//                        newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                mCategroyTaskListContractPresenter.onScrolled(recyclerView.getLayoutManager());
//            }
//        });
//
//        mCategroyTaskListContractPresenter.start();
//
//        builder.setView(view);
////        builder.setCancelable(true);
////        TextView title= (TextView) view
////                .findViewById(R.id.title);        // 設置標題
////        EditText input_edt= (EditText) view
////                .findViewById(R.id.dialog_edit);  // 輸入内容
////        Button btn_cancel=(Button)view
////                .findViewById(R.id.btn_cancel);   // 取消按鈕
////        Button btn_comfirm=(Button)view
////                .findViewById(R.id.btn_comfirm);  // 確定按鈕
//
//        // 取消或確定按鈕監聽事件處理
//        mDialog = builder.create();
//        mDialog.show();
//        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
//
//    }
//
//    @Override
//    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
//        mCategoryTaskListAdapter.updateData(bean);
//    }
//
//    @Override
//    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
//        mDialog.dismiss();
//
//        Logger.d(Constants.TAG, MSG + "Category: " + bean.getCategoryName() + " Task: " + bean.getTaskName());
//        mAnalysisDailyAdapter.showCategoryTaskSelected(bean);
//    }
//
//    @Override
//    public void refreshCategoryTaskUi(int mode) {
//        setIntTaskMode(mode);
//        mCategoryTaskListAdapter.refreshUiMode(mode);
//    }
//
//    @Override
//    public void showCategoryListDialog() {
//
//    }
//
//    public int getIntTaskMode() {
//        return mIntTaskMode;
//    }
//
//    public void setIntTaskMode(int intTaskMode) {
//        mIntTaskMode = intTaskMode;
//    }

}
