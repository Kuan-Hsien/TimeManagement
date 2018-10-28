package com.realizeitstudio.deteclife.iconpicker;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public IconPickerAdapter(List<IconDefineTable> bean, IconPickerContract.Presenter presenter) {

        mPresenter = presenter;
        mIconList = new ArrayList<>();

        for (int i = 0; i < bean.size(); ++i) {
            this.mIconList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_icon_item, parent, false);

        return new IconItemViewHolder(view);
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((IconItemViewHolder) holder).bindView(mIconList.get(position), position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mIconList.size();
    }


    public void updateData(List<IconDefineTable> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mIconList.clear();

        for (int i = 0; i < bean.size(); ++i) {
            mIconList.add(bean.get(i));
        }

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
                mIconList.get(getCurrentPosition()).logD();
            }
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView(IconDefineTable item, int pos) {

            getImageviewIconItem().setImageDrawable(TimeManagementApplication.getIconResourceDrawable(item.getIconName()));
            getImageviewIconItem().setColorFilter(TimeManagementApplication.getAppContext().getResources().getColor(R.color.color_app_white)); // 設定圖案線條顏色
            setPosition(pos);
        }

    }
}
