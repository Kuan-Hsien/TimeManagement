package com.kuanhsien.timemanagement.utils;

/**
 * Created by Ken on 2018/9/22.
 */

public class Constants {

    // Log
    public static final String TAG = "KEN_TMD";

    // User Manager
    public static final String USER_DATA = "user_data";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_ID = "user_id";

    // Activity
    public static final int LOGIN_ACTIVITY = 0x01;

    // Status
    public static final int LOGIN_SUCCESS = 0x11;
    public static final int LOGIN_EXIT = 0x12;

    // Server error
    public static final String GENERAL_ERROR = "Internal Server Error.";

    // RecyclerView Adapter
    public static final int VIEWTYPE_ADD_ITEM = 0;
    public static final int VIEWTYPE_TOP = 1;
    public static final int VIEWTYPE_NORMAL = 2;
    public static final int VIEWTYPE_LOADING = 3;
    public static final int VIEWTYPE_DETAIL_MAIN = 4;
    public static final int VIEWTYPE_DETAIL_COMMENT = 5;
    public static final int VIEWTYPE_CATEGORY = 10;
    public static final int VIEWTYPE_TASK = 11;

    // Bundle
    public static final String BUNDLE_DETAIL_ARTICLE = "bundle_detail_article";



    // Plan Fragment Tab Layout
    public static final String TAB_DAILY = "DAY";
    public static final String TAB_WEEKLY = "WEEK";
    public static final String TAB_MONTHLY = "MONTH";
    public static final String TAB_YEARLY = "YEAR";

    // Plan
    public static final int MODE_PLAN_VIEW = 1;
    public static final int MODE_PLAN_EDIT = 2;

    // Category & Task
    public static final String ITEM_CATEGORY = "CATEGORY";
    public static final String ITEM_TASK = "TASK";


    // Database
    // Plan (set target)
    public static final String MODE_PERIOD = "MODE_PERIOD";
    public static final String MODE_CALENDAR = "MODE_CALENDAR";
    public static final String MODE_DAILY = "MODE_DAILY";
    public static final String MODE_WEEKLY = "MODE_WEEKLY";
    public static final String MODE_MONTHLY = "MODE_MONTHLY";
    public static final String MODE_YEARLY = "MODE_YEARLY";

    // Default Tasks
    public static final String TASK_SLEEP = "SLEEP";
    public static final String TASK_FREE = "FREE";
    public static final String TASK_FAMILY = "FAMILY";
    public static final String TASK_FRIEND = "FRIEND";

    // Default Category
    public static final String CATEGORY_HEALTH = "HEALTH";
    public static final String CATEGORY_RELATIONSHIP = "RELATIONSHIP";
    public static final String CATEGORY_SELF = "SELF";


    // Job Scheduler ID
    public static final int SCHEDULE_JOB_ID_DAILY_SUMMARY = 101;
    public static final int SCHEDULE_JOB_ID_COMPLETE_TASK = 102;
    public static final int SCHEDULE_JOB_ID_REMAINING_TASK = 103;

    // Notification ID


}
