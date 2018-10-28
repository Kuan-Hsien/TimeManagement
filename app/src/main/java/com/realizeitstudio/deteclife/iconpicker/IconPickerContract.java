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

package com.realizeitstudio.deteclife.iconpicker;

import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.BasePresenter;
import com.realizeitstudio.deteclife.BaseView;
import com.realizeitstudio.deteclife.object.IconDefineTable;

import java.util.List;


/**
 * Created by Ken on 2018/10/18
 *
 * This specifies the contract between the view and the presenter.
 */
public interface IconPickerContract {

    interface View extends BaseView<Presenter> {

        // 1-2 request adapter to show the target list (get query result)
        void showIconList(List<IconDefineTable> bean);

        void showDialog(String strColor);

        void closeDialog();
    }

    interface Presenter extends BasePresenter {

        // 0-1. recyclerView Scroll event
        void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState);

        void onScrolled(RecyclerView.LayoutManager layoutManager);

        // 1-1. [Send-to-Model] database query to prepare data (query all targets)
        void getIconList();

        // 1-2. [Send-to-View] request fragment to show data
        void showIconList(List<IconDefineTable> bean);

        void showIconSelected(IconDefineTable bean);

    }
}

