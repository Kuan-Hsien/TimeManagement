package com.realizeitstudio.deteclife.settarget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/9/25.
 *
 * A simple {@link Fragment} subclass.
 */
public class SetTargetFragment extends Fragment implements SetTargetContract.View, View.OnClickListener {

    private SetTargetContract.Presenter mPresenter;
//    private DetailAdapter mDetailAdapter;

//    private EditOptionDialog mEditOptionDialog;
//    private EditCommentDialog mEditCommentDialog;


    // [TODO] 確認是否需要放全域變數
    private EditText mEditTextSetTargetCategory;
    private EditText mEditTextSetTargetTask;



    public SetTargetFragment() {
        // Required empty public constructor
    }

    public static SetTargetFragment newInstance() {
        return new SetTargetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((MainActivity) getActivity()).showUserInfoLog();
//        mPresenter.hideToolbar();
//        mDetailAdapter = new DetailAdapter(new Article(), mPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setPresenter(SetTargetContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mPresenter.result(requestCode, resultCode);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_set_target, container, false);

//        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_detail);
//        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
//        recyclerView.setAdapter(mDetailAdapter);

        mEditTextSetTargetCategory = root.findViewById(R.id.edittext_set_target_category);
        mEditTextSetTargetTask = root.findViewById(R.id.edittext_set_target_task);

        ((ImageView) root.findViewById(R.id.imageview_set_target_send)).setOnClickListener(this);
        ((ImageView) root.findViewById(R.id.imageview_set_target_cancel)).setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mPresenter.showToolbar();
//        ((MainActivity) getActivity()).refreshLikedUi();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageview_set_target_send) {

            // 取得現在時間
            Date curDate = new Date();
            // 定義時間格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
            // 透過SimpleDateFormat的format方法將 Date 轉為字串
            String strCurrentTime = simpleDateFormat.format(curDate);

            // [TODO] 此處需判斷每個字串是否為空，還有對輸入的時間做檢查
            mPresenter.sendNewTarget(
                    Constants.MODE_PERIOD,
                    ((EditText) getView().findViewById(R.id.edittext_set_target_category)).getText().toString().trim(),
                    ((EditText) getView().findViewById(R.id.edittext_set_target_task)).getText().toString().trim(),
                    strCurrentTime,
                    strCurrentTime,
                    "8 hr"
            );

        } else if (v.getId() == R.id.imageview_set_target_cancel) {
            showPlanUi();
        }
    }


    @Override
    public void showPlanUi() {
        ((MainActivity) getActivity()).transToPlan();
    }

    /**
     * Show Option Dialog
     * @param commentId
     * @param comment
     * @param position: the position of data list.
     */
//    @Override
//    public void showEditOptionDialog(String commentId, String comment, int position) {
//        if (mEditOptionDialog == null || !mEditOptionDialog.isShowing()) {
//
//            mEditOptionDialog = new EditOptionDialog(getActivity(), mPresenter, commentId, comment, position);
//            mEditOptionDialog.show();
//        }
//    }

    /**
     * Show Edit comment dialog
     * @param commentId
     * @param comment
     * @param position: the position of data list.
     */
//    @Override
//    public void showEditCommentDialog(String commentId, String comment, int position) {
//
//        if (mEditCommentDialog == null || !mEditCommentDialog.isShowing()) {
//
//            mEditCommentDialog = new EditCommentDialog(getActivity(), mPresenter, commentId, comment, position);
//            mEditCommentDialog.show();
//        }
//    }

//    @Override
//    public void updateCommentItem(int position, Comment comment) {
//        mDetailAdapter.updateCommentItem(position, comment);
//    }

//    @Override
//    public void deleteCommentItem(int position) {
//        mDetailAdapter.deleteCommentItem(position);
//    }

    /**
     * RecyclerView move to position.
     * @param position
     */
//    @Override
//    public void moveToCommentPosition(int position) {
//        ((RecyclerView) getView().findViewById(R.id.recyclerview_detail)).smoothScrollToPosition(position);
//    }

//    @Override
//    public void setToolbarVisibility(boolean visible) {
//        ((VoyageActivity) getActivity()).setToolbarVisibility(visible);
//    }

//    @Override
//    public void showArticle(Article article) {
//        mDetailAdapter.updateArticle(article);
//    }

//    @Override
//    public void showComments(GetComments bean) {
//        mDetailAdapter.updateComments(bean);
//    }

//    @Override
//    public void showNoData() {
//        Logger.d(Constants.TAG, "DetailFragment no data");
//        Toast.makeText(TimeManagementApplication.getAppContext(), "Error, this item has no data.", Toast.LENGTH_SHORT).show();
//        this.getFragmentManager().popBackStack();
//    }

//    @Override
//    public void setLoadMoreButtonVisibility(boolean visible) {
//        mDetailAdapter.setLoadMoreButtonVisibility(visible);
//    }

//    @Override
//    public void refreshUi(boolean isMoveToEnd) {
//        ((EditText) getView().findViewById(R.id.edittext_detail_inputtext)).setText("");
//        mDetailAdapter.initData(isMoveToEnd);
//    }




}
