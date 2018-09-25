package com.kuanhsien.timemanagement.settarget;

import com.kuanhsien.timemanagement.BasePresenter;
import com.kuanhsien.timemanagement.BaseView;

import org.w3c.dom.Comment;

/**
 * Created by Ken on 2018/9/25.
 *
 * This specifies the contract between the view and the presenter.
 */
public interface SetTargetContract {

    interface View extends BaseView<Presenter> {

        void showPlanUi();

//        void setToolbarVisibility(boolean visible);
//
//        void showComments(GetComments bean);
//
//        void showNoData();
//
//        void showArticle(Article article);
//
//        void setLoadMoreButtonVisibility(boolean visible);
//
//        void showEditOptionDialog(String commentId, String comment, int position);
//
//        void showEditCommentDialog(String commentId, String comment, int position);
//
//        void updateCommentItem(int position, Comment comment);
//
//        void deleteCommentItem(int position);
//
//        void refreshUi(boolean isMoveToEnd);
//
//        void moveToCommentPosition(int position);

    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);
//
//        void loadComments();
//
//        void updateInterestedIn(Article article, boolean isInterestedIn);
//
//        void showToolbar();
//
//        void hideToolbar();
//
//        void clickLoadMoreComments();
//
//        void clickItemOptions(String commentId, String comment, int position);
//
//        void clickEditComment(String commentId, String comment, int position);
//
//        void clickDeleteComment(String commentId, int position);
//
        void sendNewTarget(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime);

//        void sendEditComment(String commentId, String comment, int position);
//
//        void loadCommentsAndMoveToEnd();
    }
}
