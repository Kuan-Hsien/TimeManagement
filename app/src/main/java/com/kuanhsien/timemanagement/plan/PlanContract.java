/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuanhsien.timemanagement.plan;

import android.support.v7.widget.RecyclerView;

import com.kuanhsien.timemanagement.BasePresenter;
import com.kuanhsien.timemanagement.BaseView;
import com.kuanhsien.timemanagement.GetTaskWithPlanTime;

import java.util.List;


/**
 * Created by Ken on 2018/9/23
 *
 * This specifies the contract between the view and the presenter.
 */
public interface PlanContract {

    interface View extends BaseView<Presenter> {

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
        void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime);

        // 2-2. [Send-to-Model] database delete to delete data (delete existed targets)
        // 2-3. [Send-to-View] request fragment to show data



        void showSetTargetUi();

    }
}
