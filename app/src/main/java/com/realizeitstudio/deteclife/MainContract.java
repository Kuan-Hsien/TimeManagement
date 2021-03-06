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


package com.realizeitstudio.deteclife;

import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;

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

        void showCategoryListUi();

        void showAddTaskUi();

//        void refreshLikedUi();
    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);
        void transToRecord();

        void transToPlan();

        void transToAnalysis();

        void transToTaskList();

        void transToCategoryList();

        void transToAddTask();



//        void transToDetail(Article article);
//
//        void refreshLiked();

        boolean isFragmentRecordVisible();

        boolean isFragmentPlanVisible();

        boolean isFragmentAnalysisVisible();

        boolean isFragmentTaskListVisible();

        boolean isFragmentCategoryListVisible();

        boolean isFragmentAddTaskVisible();


        // return select category to task page
        void selectCategoryToTask(GetCategoryTaskList bean);

        void backCategoryToTask();

        // return selected task to plan page
        void selectTaskToPlan(GetCategoryTaskList bean);

        void backTaskToPlan();

        // call when set task complete
        void addTaskComplete();

    }
}
