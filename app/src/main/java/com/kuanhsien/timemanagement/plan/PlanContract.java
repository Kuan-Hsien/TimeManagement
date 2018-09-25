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

//        void showDetailUi(Article article);

//        void refreshUi();

    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);

        void getTaskWithPlanTime();

        void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean);

        void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState);

        void onScrolled(RecyclerView.LayoutManager layoutManager);
//
//        void openDetail(Article article);
//
//        void updateInterestedIn(Article article, boolean isInterestedIn);
//
//        void refresh();
    }
}
