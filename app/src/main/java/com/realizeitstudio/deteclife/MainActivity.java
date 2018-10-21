package com.realizeitstudio.deteclife;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/9/22
 */
public class MainActivity extends AppCompatActivity implements MainContract.View {
    private static final String MSG = "MainActivity: ";

    private MainContract.Presenter mPresenter;

    private DrawerLayout mDrawerLayout;
    private BottomNavigationViewEx mButtomNavigation;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    private PowerButtonReceiver mPowerButtonReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (UserManager.getInstance().isLoginStatus()) {
            init();
//        } else {
//            popLogin();
//        }
    }

    private void init() {

        Logger.i(Constants.TAG, MSG + "MainActivity.init");
        setContentView(R.layout.activity_main);

        setStatusBar();
        setToolbar();
        setBottomNavigation();
//        setDrawerLayout();

        mPresenter = new MainPresenter(this, getSupportFragmentManager());
        mPresenter.start();
    }

    /**
     * To change status bar to transparent.
     * @notice this method have to be used before setContentView.
     */
    private void setStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 確認取消半透明設置。
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // 跟系統表示要渲染 system bar 背景。
            window.setStatusBarColor(Color.TRANSPARENT); //calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
    }

    /**
     * Let toolbar to extend to status bar.
     *
     * @notice this method have to be used after setContentView.
     */
    private void setToolbar() {
        // Retrieve the AppCompact Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set the padding to match the Status Bar height
        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.textview_toolbar_title);
        mToolbarTitle.setText(getResources().getString(R.string.app_name));
    }

    /**
     * Set the title of toolbar.
     *
     * @param title
     */
    private void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    /**
     * plugin: BottomNavigationViewEx.
     */
    private void setBottomNavigation() {

        mButtomNavigation = (BottomNavigationViewEx) findViewById(R.id.navbutton_main);
        mButtomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mButtomNavigation.enableAnimation(false);
        mButtomNavigation.enableShiftingMode(false);
        mButtomNavigation.enableItemShiftingMode(false);
//        mButtomNavigation.setTextVisibility(false);
        mButtomNavigation.setTextVisibility(true);

        mButtomNavigation.setItemHeight((int) getResources().getDimension(R.dimen.height_navbutton_item));

        // this api use px value, so we have to divide density first
        mButtomNavigation.setIconSize(
                getResources().getDimension(R.dimen.size_navbutton_icon) / getResources().getDisplayMetrics().density,
                getResources().getDimension(R.dimen.size_navbutton_icon) / getResources().getDisplayMetrics().density);

        // 調整所有 icon 距離頂部位置，不加似乎預設是中間。也許放文字的時候需要改
//        mButtomNavigation.setIconsMarginTop((int) getResources().getDimension(R.dimen.margin_top_navbutton_icon));

    }

    /**
     * Set Drawer
     */
//    private void setDrawerLayout() {
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawrlayout_main);
//        mDrawerLayout.setFitsSystemWindows(true);
//        mDrawerLayout.setClipToPadding(false);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navview_drawer);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // navview header
//        ImageView userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageview_drawer_userimage);
//
//        userImage.setOutlineProvider(new AuthorOutlineProvider());
//
//        userImage.setTag(UserManager.getInstance().getUserImage());
//        new ImageFromLruCache().set(userImage, UserManager.getInstance().getUserImage());
//
//        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.imageview_drawer_useremail))
//                .setText(UserManager.getInstance().getUserEmail());
//
//        // logout button
//        ((LinearLayout) findViewById(R.id.linearlayout_drawer_logoutbutton))
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Logger.d(Constants.TAG, "Drawer, Logout.onClick");
//
//                        UserManager.getInstance().logoutVoyage(new LogoutCallback() {
//                            @Override
//                            public void onCompleted() {
//                                Logger.d(Constants.TAG, "logoutVoyage.onCompleted");
//
//                                popLogin();
//                            }
//                        });
//                    }
//                });
//
//    }

    /**
     * @return height of status bar
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * It's the item selected listener of bottom navigation.
     */
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_add_record:

                    Logger.d(Constants.TAG, MSG + "BottomNavigation select: Record");
                    mPresenter.transToRecord();
                    return true;

                case R.id.navigation_plan:

                    Logger.d(Constants.TAG, MSG + "BottomNavigation select: Plan");
                    mPresenter.transToPlan();
                    return true;

                case R.id.navigation_analysis:

                    Logger.d(Constants.TAG, MSG + "BottomNavigation select: Analysis");
                    mPresenter.transToAnalysis();
                    return true;


                default:
            }

            return false;
        }
    };



    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showRecordUi() {

        setToolbarTitle(getResources().getString(R.string.page_title_record));

        mToolbar.setVisibility(View.GONE);
        mButtomNavigation.setVisibility(View.GONE);
    }

    @Override
    public void showPlanUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.VISIBLE);

        setToolbarTitle(getResources().getString(R.string.page_title_plan));
    }

    @Override
    public void showTraceUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.VISIBLE);

        setToolbarTitle(getResources().getString(R.string.page_title_trace));
    }

    @Override
    public void showAnalysisUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.VISIBLE);

        setToolbarTitle(getResources().getString(R.string.page_title_analysis));
    }

    @Override
    public void showTaskListUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.GONE);

        setToolbarTitle(getResources().getString(R.string.page_title_tasklist));
    }

    @Override
    public void showCategoryListUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.GONE);

        setToolbarTitle(getResources().getString(R.string.page_title_categorylist));
    }

    @Override
    public void showAddTaskUi() {

        mToolbar.setVisibility(View.VISIBLE);
        mButtomNavigation.setVisibility(View.GONE);

        setToolbarTitle(getResources().getString(R.string.page_title_addtask));
    }

    public void transToRecord() {
        mButtomNavigation.findViewById(R.id.navigation_add_record).performClick();   // mPresenter.transToAnalysis();
    }

    public void transToPlan() {
        mButtomNavigation.findViewById(R.id.navigation_plan).performClick();   // mPresenter.transToPlan();
    }

    public void transToAnalysis() {
        mButtomNavigation.findViewById(R.id.navigation_analysis).performClick();   // mPresenter.transToAnalysis();
    }

    public void transToTaskList() {
        mPresenter.transToTaskList();
    }

    public void transToCategoryList() {
        Logger.d(Constants.TAG, MSG + "transToCategoryList");
        mPresenter.transToCategoryList();
    }

    public void transToAddTask() {
        mPresenter.transToAddTask();
    }

    public void transToSetTarget() {
        mPresenter.transToSetTarget();
    }




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    // onBackPressed handle by activity
    @Override
    public void onBackPressed() {
        Logger.d(Constants.TAG, MSG + "onBackPressed: ");

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//
//        } else if (getIntLaunchMode() == LAUNCH_OTHER) {
//            // if launch by other application, then close the app (do not enter the other page)
//            super.onBackPressed();
//
//        } else

        // press backkey on category page (without choose any category)
        if (mPresenter.isFragmentCategoryListVisible()) {

            // call presenter to hide this fragment and show the parent task list
            mPresenter.backCategoryToTask();

        } else if (mPresenter.isFragmentTaskListVisible()) {

            // call presenter to hide this fragment and show the original one
            mPresenter.backTaskToPlan();

        } else if (mPresenter.isFragmentAddTaskVisible()) {

            // call presenter to hide this fragment and show the original one
            mButtomNavigation.setSelectedItemId(mButtomNavigation.getSelectedItemId());

        } else if (mPresenter.isFragmentRecordVisible()) {

            // block back-key
            ;

        } else {
            // if current view is main-fragment, than leave the app
            super.onBackPressed();
        }
    }
//
//    public void hideTaskListUi() {
//        mToolbarMain.setVisibility(View.VISIBLE);
//        mLinearLayoutMain.setVisibility(View.VISIBLE);
//
//        mFragmentTransactionMain = mFragmentManagerMain.beginTransaction();
//        mFragmentTransactionMain.hide(mFragmentDetail).commit();
//    }
}

