package com.kuanhsien.timemanagement.plan.weekly;

import android.support.v7.widget.RecyclerView;

import com.kuanhsien.timemanagement.BasePresenter;
import com.kuanhsien.timemanagement.BaseView;
import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.plan.weekly.PlanWeeklyContract;

import java.util.List;

/**
 * Created by Ken on 2018/9/29
 *
 * This specifies the contract between the view and the presenter.
 */
public interface PlanWeeklyContract {

    interface View extends BaseView<PlanWeeklyContract.Presenter> {

        void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean);

        void refreshUi(int mode); // change mode (view_mode <-> edit_mode)

        void showSetTargetUi();

    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);

        // 0-1. recyclerView Scroll event
        void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState);

        void onScrolled(RecyclerView.LayoutManager layoutManager);

        // 0-2. [Send-to-View] request fragment to refresh adapter (base on mode (view or edit))
        void refreshUi(int mode); // change mode (view_mode <-> edit_mode), and trigger adapter to update

        // 1-1. [Send-to-Model] database query to prepare data (query all targets)
        void getTaskWithPlanTime();

        // 1-2. [Send-to-View] request fragment to show data
        void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean);

        // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
//        void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime);
        void saveTargetResults(List<TimePlanningTable> targetList, List<TimePlanningTable> deleteTargetList);

        // 2-2. [Send-to-Model] database delete to delete data (delete existed targets)
        // 2-3. [Send-to-View] request fragment to show data



        void showSetTargetUi();

    }
}
