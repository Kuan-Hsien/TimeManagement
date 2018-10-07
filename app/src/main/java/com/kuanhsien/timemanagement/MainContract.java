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

/**
 * Created by Ken on 2018/9/23.
 *
 * This specifies the contract between the view and the presenter.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void showPlanUi();

        void showTraceUi();

        void showStatisticUi();

        void showRecordUi();

//        void refreshLikedUi();

    }

    interface Presenter extends BasePresenter {

//        void result(int requestCode, int resultCode);

        void transToPlan();

        void transToTrace();

        void transToStatistic();

        void transToSetTarget();

        void transToRecord();

//        void transToDetail(Article article);
//
//        void refreshLiked();

    }
}
