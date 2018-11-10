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

package com.realizeitstudio.deteclife.record;

import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.BasePresenter;
import com.realizeitstudio.deteclife.BaseView;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.object.TimeTracingTable;

import java.util.List;


/**
 * Created by Ken on 2018/10/07
 *
 * This specifies the contract between the view and the presenter.
 */
public interface RecordContract {

    interface View extends BaseView<Presenter> {

        // 0-2 request adapter refresh UI with different mode
        void refreshUi(int mode); // change mode (view_mode <-> edit_mode)

        // 1-2 request adapter to show the target list (get query result)
        void showCategoryTaskList(List<GetCategoryTaskList> bean);

        void showCurrentTraceItem(TimeTracingTable bean);

        void showPlanUi();

        void showStatisticUi();

        void showAddTaskUi();
    }

    interface Presenter extends BasePresenter {

        // 0-1. recyclerView Scroll event
        void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState);

        void onScrolled(RecyclerView.LayoutManager layoutManager);

        // 0-2. [Send-to-View] request fragment to refresh adapter (base on mode (view or edit))
        void refreshUi(int mode); // change mode (view_mode <-> edit_mode), and trigger adapter to update

        // 1-1. [Send-to-Model] database query to prepare data (query all targets)
        void getCategoryTaskList();

        // 1-2. [Send-to-View] request fragment to show data
        void showCategoryTaskList(List<GetCategoryTaskList> bean);


        /////////////////.......0000----
        // 1-1. [Send-to-Model] database query to prepare data (query all targets)
        void getCurrentTraceItem(String strVerNo);

        // 1-2. [Send-to-View] request fragment to show data
        void showCurrentTraceItem(TimeTracingTable bean);



        // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
        // 2-2. [Send-to-Model] database delete to delete data (delete existed targets)
//        void saveTaskResults(List<TaskDefineTable> targetList, List<TaskDefineTable> deleteTargetList);
        void saveTraceResults(List<TimeTracingTable> traceList, String startVerNo, String endVerNo, String categoryList, String taskList);


        // 2-3. [Send-to-View] request fragment to show data
        // once update data, query the target list again to refresh UI
        // (1-1, 1-2)

        void showAddTaskUi();

    }
}
