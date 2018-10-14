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


package com.kuanhsien.timemanagement;

import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;

/**
 * Created by Ken on 2018/9/23.
 *
 * This specifies the contract between the view and the presenter.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void showRecordUi();

        void showPlanUi();

        void showTraceUi();

        void showAnalysisUi();

        void showTaskListUi();

        void showAddTaskUi();

//        void refreshLikedUi();
    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);
        void transToRecord();

        void transToPlan();

        void transToAnalysis();

        void transToTaskList();

        void transToAddTask();

        void transToSetTarget();



//        void transToDetail(Article article);
//
//        void refreshLiked();

        boolean isFragmentRecordVisible();

        boolean isFragmentPlanVisible();

        boolean isFragmentAnalysisVisible();

        boolean isFragmentTaskListVisible();

        boolean isFragmentAddTaskVisible();


        // return select task
        void selectTaskToPlan(GetCategoryTaskList bean);

        // call when set task complete
        void addTaskComplete();

    }
}
