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

package com.realizeitstudio.deteclife.addtask;

import com.realizeitstudio.deteclife.BasePresenter;
import com.realizeitstudio.deteclife.BaseView;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;

import java.util.List;

/**
 * Created by Ken on 2018/10/14
 *
 * This specifies the contract between the view and the presenter.
 */
public interface AddTaskContract {

    interface View extends BaseView<Presenter> {

        void showIconSelected(IconDefineTable bean);

        void showCategoryListDialog();
    }

    interface Presenter extends BasePresenter {

        // 2-1. [Send-to-Model] database insert to update data (insert new task or edit existed tasks)
        // 2-2. [Send-to-Model] database delete to delete data (delete existed tasks)
        void saveTaskResults(List<TaskDefineTable> taskList, List<TaskDefineTable> deleteTaskList);

        void addTaskComplete();

        // ****** Icon Picker Dialog ****** //
        void showIconPickerDialog(String strColor);

        void showIconSelected(IconDefineTable bean);

    }
}