package com.kuanhsien.timemanagement;

import android.graphics.Color;
import android.os.AsyncTask;
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
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.util.List;

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
        mButtomNavigation.setTextVisibility(false);

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
                case R.id.navigation_trace:

//                    mPresenter.transToTrace();
//                    return true;

                case R.id.navigation_plan:

                    mPresenter.transToPlan();
                    return true;

                case R.id.navigation_profile:

//                    mPresenter.transToProfile();
//                    return true;

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
    public void showPlanUi() {

        setToolbarTitle(getResources().getString(R.string.app_name));
    }


    public void transToPlan() {
        mPresenter.transToPlan();
    }

    public void transToSetTarget() {
        mPresenter.transToSetTarget();
    }






    private void roomDatabase() {

        // 和 Database 有關的操作不能放在 main-thread 中。不然會跳出錯誤：
        // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

        // 解決方式：(此處使用 2)
        // 1. 在取得資料庫連線時增加 allowMainThreadQueries() 方法，強制在主程式中執行
        // 2. 另開 thread 執行耗時工作 (建議採用此方法)，另開 thread 有多種寫法，按自己習慣作業即可。此處為測試是否寫入手機SQLite，故不考慮 callback，如下
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                DatabaseDao dao = AppDatabase.getDatabase(getApplicationContext()).getDatabaseDao();

                // [INSERT]
//                CategoryDefineTable categoryItem = new CategoryDefineTable(1, "Work", true, "Red", "Work", "High");
//                TaskDefineTable taskItem = new TaskDefineTable(1, "Prepare final test", "Work", true);
//
//                dao.addCategory(categoryItem);
//                dao.addTask(taskItem);
//                dao.insertAllCategory();
//                dao.insertAll(2, "First name", "Last name", "Address", null);


                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<CategoryDefineTable> categoryList = dao.getAllCategoryList();
                List<TaskDefineTable> taskList = dao.getTaskList();

                for (int i = 0 ; i < categoryList.size() ; ++i) {
                    Logger.d(Constants.TAG, categoryList.get(i).getCategoryName() + "" );
                }

                for (int i = 0 ; i < categoryList.size() ; ++i) {
                    Logger.d(Constants.TAG, taskList.get(i).getTaskName() + "" );
                }

            }
        });



    }

}
