package com.realizeitstudio.deteclife.iconpicker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ken on 2018/10/18
 */
public class IconPickerAdapter extends RecyclerView.Adapter {
    private static final String MSG = "IconPickerAdapter: ";

    private IconPickerContract.Presenter mPresenter;
    private List<IconDefineTable> mIconList;
    private boolean[] isDeleteArray;
    private int mIntIconMode;

    public IconPickerAdapter(List<IconDefineTable> bean, IconPickerContract.Presenter presenter) {

        mPresenter = presenter;
        setIntIconMode(Constants.MODE_PLAN_VIEW);
        mIconList = new ArrayList<>();

        for( int i = 0 ; i < bean.size() ; ++i ) {
            this.mIconList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);

//        if (viewType == Constants.VIEWTYPE_ADD_ITEM) {
//
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_categorytask_add_task, parent, false);
//            return new AddItemViewHolder(view);
//
//        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_icon_item, parent, false);
            return new IconItemViewHolder(view);
//        }

    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        Logger.d(Constants.TAG, MSG + "onBindViewHolder: position " + position + " " + mIconList.get(position));

//        if (holder instanceof AddItemViewHolder) {
//                ((AddItemViewHolder) holder).bindView();
//        } else {
            ((IconItemViewHolder) holder).bindView(mIconList.get(position), position);
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

//        return mIconList.size() + 1;
        return mIconList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        Logger.d(Constants.TAG, MSG + "getItemViewType: position: " + position + "  View-Type: " + ((position == 0) ? Constants.VIEWTYPE_CATEGORY : Constants.VIEWTYPE_NORMAL));

//        if (position == mIconList.size()) { // last item would be add-item-layout
//            return Constants.VIEWTYPE_ADD_ITEM;
//        } else {
            return Constants.VIEWTYPE_NORMAL;
//        }
    }


    public void updateData(List<IconDefineTable> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mIconList.clear();

        for (int i = 0 ; i < bean.size() ; ++i) {
            mIconList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }


    public void refreshUiMode(int mode) {
        Logger.d(Constants.TAG, MSG + "refreshUiMode: " + (mode == Constants.MODE_PLAN_VIEW ? "VIEW_MODE" : "EDIT_MODE"));

        // if user request to change to MODE_PLAN_EDIT
        if (mode == Constants.MODE_PLAN_EDIT) {

            int intArraySize = mIconList.size();
        }

        setIntIconMode(mode);
        notifyDataSetChanged();
    }


    // ViewHolder of Icon
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class IconItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageviewIconItem;
        private ConstraintLayout mConstraintLayoutIconItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ImageView getImageviewIconItem() {
            return mImageviewIconItem;
        }

        public ConstraintLayout getConstraintLayoutIconItem() {
            return mConstraintLayoutIconItem;
        }

        public IconItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mImageviewIconItem = (ImageView) v.findViewById(R.id.imageview_icon_item);
            mConstraintLayoutIconItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_icon_item);
            mConstraintLayoutIconItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_icon_item) {

                mPresenter.showIconSelected(mIconList.get(getCurrentPosition()));

                Logger.d(Constants.TAG, MSG + "select icon: ");
                mIconList.get(getCurrentPosition()).LogD();
            }
        }


        /**
         * call by onBindViewHolder
         */
        public void bindView(IconDefineTable item , int pos) {

            // 把相對應位置的 task 顯示在此 viewHolder

//            GradientDrawable gradientDrawable = (GradientDrawable) getConstraintLayoutIconItem().getBackground();
//            gradientDrawable.setColor(Color.parseColor("#134D78"));

            getImageviewIconItem().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getIconName()));
            setPosition(pos);
        }

    }


    public int getIntIconMode() {
        return mIntIconMode;
    }

    public void setIntIconMode(int intIconMode) {
        mIntIconMode = intIconMode;
    }
}
