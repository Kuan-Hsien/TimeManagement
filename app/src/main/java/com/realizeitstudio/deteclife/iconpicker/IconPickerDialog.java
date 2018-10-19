package com.realizeitstudio.deteclife.iconpicker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.addtask.AddTaskPresenter;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/18
 *
 * A simple {@link Fragment} subclass.
 */
public class IconPickerDialog implements IconPickerContract.View {

    private static final String MSG = "IconPickerDialog: ";

    // [TODO] x. 未來可以客製化 Dialog (extends AlertDialog)

    // Dialog: Icon Picker
    private IconPickerContract.Presenter mPresenter;
    private IconPickerAdapter mIconPickerAdapter;
    private AlertDialog mDialog;

    private MainActivity mMainActivity;

    private String mStrDialogColor;


//    public static IconPickerDialog newInstance() {
//        return new IconPickerDialog();
//    }

    public IconPickerDialog(MainActivity mainActivity, String strColor) {

//        mPresenter = new IconPickerPresenter(this);
        mMainActivity = mainActivity;

        mStrDialogColor = strColor;
    }


    @Override
    public void setPresenter(IconPickerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showDialog() {



// ****** [Dialog Sample]: 用預設的 mDialog 介面 ******

//        final String[] list_String = {"1", "2", "3", "4", "5"};

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("標題");
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setItems(list_String, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface mDialog, int which) {    // 傳回的 which 表示點擊列表的第幾項
//                Toast.makeText(getActivity(), "點擊: " + list_String[which], Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        AlertDialog mDialog = builder.create();
//        mDialog.show();

        // ****** 用自定義的 mDialog 介面 ******
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);

        View view = View.inflate(mMainActivity, R.layout.dialog_icon_list, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_icon_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(mMainActivity, Constants.ICON_SPAN_COUNT));

        mIconPickerAdapter = new IconPickerAdapter(new ArrayList<IconDefineTable>(), mPresenter);
        recyclerView.setAdapter(mIconPickerAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });


        mPresenter.start();

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
        Logger.d(Constants.TAG, MSG + "before dialog build:" );
        mDialog = builder.create();
        mDialog.show();
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);



        LinearLayout linearLayoutIconDialog = view.findViewById(R.id.linearlayout_icon_list_dialog);
        GradientDrawable gradientDrawable = (GradientDrawable) linearLayoutIconDialog.getBackground();
        gradientDrawable.setColor(Color.parseColor(mStrDialogColor));

    }


    @Override
    public void showIconList(List<IconDefineTable> bean) {
        mIconPickerAdapter.updateData(bean);
    }

    @Override
    public void closeDialog() {
        mDialog.dismiss();
    }

    @Override
    public void refreshUi(int mode) {
//        setIntTaskMode(mode);
        mIconPickerAdapter.refreshUiMode(mode);
    }

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
