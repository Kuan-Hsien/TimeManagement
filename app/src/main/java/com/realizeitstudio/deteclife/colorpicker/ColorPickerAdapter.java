package com.realizeitstudio.deteclife.colorpicker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2018/10/27
 */
public class ColorPickerAdapter extends RecyclerView.Adapter {
    private static final String MSG = "ColorPickerAdapter: ";

    private ColorPickerContract.Presenter mPresenter;
    private List<ColorDefineTable> mColorList;

    public ColorPickerAdapter(List<ColorDefineTable> bean, ColorPickerContract.Presenter presenter) {

        mPresenter = presenter;
        mColorList = new ArrayList<>();

        for (int i = 0; i < bean.size(); ++i) {
            this.mColorList.add(bean.get(i));
        }
    }

    // Create new views (invoked by the layout manager)
    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d(Constants.TAG, MSG + "onCreateViewHolder: viewType = " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_main, parent, false);

        return new ColorItemViewHolder(view);
    }

    //update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((ColorItemViewHolder) holder).bindView(mColorList.get(position), position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mColorList.size();
    }

    public void updateData(List<ColorDefineTable> bean) {
        Logger.d(Constants.TAG, MSG + "update data");

        mColorList.clear();

        for (int i = 0; i < bean.size(); ++i) {
            mColorList.add(bean.get(i));
        }

        notifyDataSetChanged();
    }

    // ViewHolder of Color
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ColorItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageviewColorItem;
        private ConstraintLayout mConstraintLayoutColorItem;

        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public ImageView getImageviewColorItem() {
            return mImageviewColorItem;
        }

        public ConstraintLayout getConstraintLayoutColorItem() {
            return mConstraintLayoutColorItem;
        }

        public ColorItemViewHolder(View v) {
            super(v);

            mPosition = 0;

            //** View mode
            mImageviewColorItem = (ImageView) v.findViewById(R.id.imageview_task_color_item);
            mConstraintLayoutColorItem = (ConstraintLayout) v.findViewById(R.id.constraintlayout_task_color_item);
            mConstraintLayoutColorItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.constraintlayout_task_color_item) {

                mPresenter.showColorSelected(mColorList.get(getCurrentPosition()));

                Logger.d(Constants.TAG, MSG + "select color: ");
                mColorList.get(getCurrentPosition()).logD();
            }
        }

        /**
         * call by onBindViewHolder
         */
        public void bindView(ColorDefineTable item, int pos) {

            GradientDrawable gradientDrawable = (GradientDrawable) getImageviewColorItem().getBackground();
            gradientDrawable.setColor(Color.parseColor(item.getColorName()));
            setPosition(pos);
        }

    }
}
