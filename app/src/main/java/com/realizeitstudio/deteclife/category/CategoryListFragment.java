package com.realizeitstudio.deteclife.category;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/19.
 *
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment implements CategoryListContract.View {

    private static final String MSG = "CategoryListFragment: ";

    private CategoryListContract.Presenter mCategroyListContractPresenter;
    private AlertDialog mDialog;

    private CategoryListContract.Presenter mPresenter;
    private CategoryListAdapter mCategoryListAdapter;
    private int mIntPlanMode;
    private int mIntCategoryMode;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public void setPresenter(CategoryListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] CategoryListFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mCategoryListAdapter = new CategoryListAdapter(new ArrayList<GetCategoryTaskList>(), mPresenter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_category_list, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_categorylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mCategoryListAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = " + hidden);

        if (hidden) {  // 不在最前端介面顯示 (被 hide())
            ;
        } else {  //重新顯示到最前端 (被 show())
            Logger.d(Constants.TAG, MSG + "onHiddenChanged: hidden = false => SHOW");
            mPresenter.start();
        }
    }

    @Override
    public void refreshUi(int mode) {
        setIntCategoryMode(mode);
        mCategoryListAdapter.refreshUiMode(mode);
    }

    @Override
    public void showCategoryList(List<GetCategoryTaskList> bean) {
        mCategoryListAdapter.updateData(bean);
    }

    @Override
    public void showCategorySelected(GetCategoryTaskList bean) {

//        Logger.d(Constants.TAG, MSG + "Select Category: ");
//        bean.logD();
//
//        // (1) 把選到的 category 傳回 (e.g. Plan page)
//        // (2) 把自己這頁關掉
//        mPresenter.show
//
//        Bundle bundle = new Bundle();
//        bundle.pu
//        bundle.putString("article_id", mArticlesList.get(mPosition).getId());
//        bundle.putString("article_author_id", mArticlesList.get(mPosition).getAuthor().getId());
//        bundle.putString("article_author_name", mArticlesList.get(mPosition).getAuthor().getName());
//        bundle.putString("article_author_image", mArticlesList.get(mPosition).getAuthor().getImage());
//        bundle.putString("article_title", mArticlesList.get(mPosition).getTitle());
//        bundle.putString("article_content", mArticlesList.get(mPosition).getContent());
//        bundle.putString("article_createdtime", mArticlesList.get(mPosition).getCreatedTime());
//        bundle.putString("article_place", mArticlesList.get(mPosition).getPlace());
//        // [TODO] picture need to send all the list item
//        bundle.putString("article_picture", mArticlesList.get(mPosition).getPictures().toString());
//        bundle.putInt("article_interests", mArticlesList.get(mPosition).getInterests());
//        bundle.putInt("article_interested_in", mArticlesList.get(mPosition).isInterestedIn() == true ? 1 : 0);
//
//        mMainActivity.getFragmentDetail().setArguments(bundle);
//        mMainActivity.displayArticleDetail();
//
//
//
//        mCategoryListAdapter.showCategorySelected(bean);


    }


    public int getIntCategoryMode() {
        return mIntCategoryMode;
    }

    public void setIntCategoryMode(int intCategoryMode) {
        mIntCategoryMode = intCategoryMode;
    }

    @Override
    public void showToast(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}