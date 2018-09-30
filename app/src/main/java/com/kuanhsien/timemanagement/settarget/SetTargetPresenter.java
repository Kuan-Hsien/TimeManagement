package com.kuanhsien.timemanagement.settarget;

import android.util.Log;

import com.kuanhsien.timemanagement.SetTargetAsyncTask;
import com.kuanhsien.timemanagement.SetTargetCallback;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/9/25.
 */
public class SetTargetPresenter implements SetTargetContract.Presenter {

    private final SetTargetContract.View mSetTargetView;
//    private Article mArticle;

    private String mStartValue = null;

    public SetTargetPresenter(SetTargetContract.View detailView) {
        mSetTargetView = checkNotNull(detailView, "detailView cannot be null!");
        mSetTargetView.setPresenter(this);

//        mArticle = article;
    }

//    @Override
//    public void result(int requestCode, int resultCode) {
//
//    }

    @Override
    public void start() {
//        mSetTargetView.showArticle(mArticle);
//        loadComments();
    }

//    public void setStartValue(String startValue) {
//        mStartValue = startValue;
//    }

//    @Override
//    public void showToolbar() {
//        mSetTargetView.setToolbarVisibility(true);
//    }
//
//    @Override
//    public void hideToolbar() {
//        mSetTargetView.setToolbarVisibility(false);
//    }

//    @Override
//    public void loadComments() {
//        if (!isLoading()) {
//            setLoading(true);
//            new GetCommentsTask(mArticle.getId(), mStartValue, new GetCommentsCallback() {
//                @Override
//                public void onCompleted(GetComments bean) {
//                    setLoading(false);
//                    setStartValue(bean.getStart());
//                    if (mStartValue.equals("")) {
//                        mDetailView.setLoadMoreButtonVisibility(false);
//                    }
//                    mDetailView.showComments(bean);
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//                    setLoading(false);
//                    Log.e(Constants.TAG, "GetArticlesTask.onError, errorMessage: " + errorMessage);
//                }
//            }).execute();
//        }
//    }

////    @Override
////    public void updateInterestedIn(Article article, boolean isInterestedIn) {
////        Voyage.getVoyageSqliteHelper().updateInterestedIn(article, isInterestedIn);
////    }
////
////    @Override
////    public void clickLoadMoreComments() {
////        if (!mStartValue.equals("")) {
////            loadComments();
////        }
////    }
////
////    @Override
////    public void clickItemOptions(String commentId, String comment, int position) {
////        mDetailView.showEditOptionDialog(commentId, comment, position);
////    }
////
////    @Override
////    public void clickEditComment(String commentId, String comment, int position) {
////        mDetailView.showEditCommentDialog(commentId, comment, position);
////    }
////
////    @Override
////    public void clickDeleteComment(String commentId, final int position) {
////        if (!isLoading()) {
////            setLoading(true);
////            new DeleteCommentTask(commentId, new DeleteCommentCallback() {
////                @Override
////                public void onCompleted(DeleteComment bean) {
////                    setLoading(false);
////                    mDetailView.deleteCommentItem(position);
////                }
////
////                @Override
////                public void onError(String errorMessage) {
////                    setLoading(false);
////                    Log.e(Constants.TAG, "DeleteCommentTask.onError: " + errorMessage);
////                }
////            }).execute();
////        }
////    }
//
    @Override
    public void sendNewTarget(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

//        // insert time_planning_table
//        new SetTargetAsyncTask(strMode, strCategory, strTask, strStartTime, strEndTime, strCostTime, new SetTargetCallback() {
//
//            @Override
//            public void onCompleted(TimePlanningTable bean) {
//                // [TODO] insert 資料後更新畫面
//                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新
//                // (1) 方法 1: 用 LiveData 更新
//                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
//
//                Logger.d(Constants.TAG, "SetTarget onCompleted, TaskName: " + bean.getTaskName());
//                mSetTargetView.showPlanUi();
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//                Logger.d(Constants.TAG, "SetTarget onError, errorMessage: " + errorMessage);
//                mSetTargetView.showPlanUi();
//            }
//        }).execute();
    }
//
//    @Override
//    public void sendEditComment(String commentId, String comment, final int position) {
//        if (!isLoading()) {
//            setLoading(true);
//            new UpdateCommentTask(commentId, comment, new UpdateCommentCallback() {
//                @Override
//                public void onCompleted(UpdateComment bean) {
//                    setLoading(false);
//                    mDetailView.updateCommentItem(position, bean.getComment());
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//                    setLoading(false);
//                    Log.e(Constants.TAG, "UpdateCommentTask.onError: " + errorMessage);
//                }
//            }).execute();
//        }
//    }
//
//    @Override
//    public void loadCommentsAndMoveToEnd() {
//        if (!isLoading()) {
//            setLoading(true);
//            mStartValue = null;
//            new GetCommentsTask(mArticle.getId(), mStartValue, new GetCommentsCallback() {
//                @Override
//                public void onCompleted(GetComments bean) {
//                    setLoading(false);
//                    setStartValue(bean.getStart());
//                    if (mStartValue.equals("")) {
//                        mDetailView.setLoadMoreButtonVisibility(false);
//                    }
//                    mDetailView.showComments(bean);
//
//                    mDetailView.moveToCommentPosition(bean.getComments().size() + 1);
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//                    setLoading(false);
//                    Log.e(Constants.TAG, "GetArticlesTask.onError, errorMessage: " + errorMessage);
//                }
//            }).execute();
//        }
//    }
}
