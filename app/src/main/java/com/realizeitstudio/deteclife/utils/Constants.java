package com.realizeitstudio.deteclife.utils;

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


    // Main Function Flow
    // SharePreference
    public static final String FILENAME_USER_DATA = "FILENAME_USER_DATA";
    public static final String IS_FIRST_FLAG = "IS_FIRST_FLAG";


    // Plan Fragment Tab Layout
    public static final String TAB_DAILY = "DAY";
    public static final String TAB_WEEKLY = "WEEK";
    public static final String TAB_MONTHLY = "MONTH";
    public static final String TAB_YEARLY = "YEAR";

    public static final int TAB_DAILY_MODE = 1;
    public static final int TAB_WEEKLY_MODE = 2;
    public static final int TAB_MONTHLY_MODE = 3;
    public static final int TAB_YEARLY_MODE = 4;

    // Plan
    public static final int MODE_PLAN_VIEW = 1;
    public static final int MODE_PLAN_EDIT = 2;

    // Category & Task
    public static final String ITEM_CATEGORY = "CATEGORY";
    public static final String ITEM_TASK = "TASK";


    // Task Page Identifier
    public static final String PAGE_ADD_TASK = "PAGE_ADD_TASK";
    public static final String PAGE_TASK_LIST = "PAGE_TASK_LIST";


    // Record
    public static final int RECORD_TASK_SPAN_COUNT = 4;


    // Add Task - icon
//    public static final String DEFAULT_TASK_ICON = "btn_edit_pencil";
//    public static final String DEFAULT_TASK_COLOR = "#2963C6";

    public static final String DEFAULT_TASK_ICON = "btn_add_line";
    public static final String DEFAULT_TASK_COLOR = "#D9D9D9";
    public static final String DEFAULT_CATEGORY_COLOR = "#4682B4";


    // Toast
    public static final String TOAST_ADD_TASK_FAIL = "Input all fields to add a new item";
    public static final String TOAST_COST_TIME_EXCEEDED = "Exceeded time limit";


    // icon
    public static final int ICON_SPAN_COUNT = 4;



    // ****** Notification ******

    // APP ICON
    public static final String APP_ICON_SMALL = "deteclife_icon_line";
    public static final String APP_ICON_BIG = "ic_launcher_round";
//    public static final String APP_ICON_BIG = "deteclife_icon_fill";


    // notification ID
    public static final int NOTIFY_ID_LOCKSCREEN_RECEIVER = 9000;
    public static final int NOTIFY_ID_ONGOING = 9001;
    public static final int NOTIFY_ID_SUMMARY = 9002;
    public static final int NOTIFY_ID_COMPLETE = 9003;
    public static final int NOTIFY_ID_REMAINING_TIME = 9004;


    // notification time
//    public static final int NOTIFICATION_TIME_ONGOING = 9001;
//    public static final int NOTIFICATION_TIME_COMPLETE = 9001;
//    public static final int NOTIFICATION_TIME_REMAINING_TIME = 9001;
    public static final int NOTIFICATION_TIME_SUMMARY = 1000;

    public static final String NOTIFICATION_TIME_DAILY_DATA_VERGEN = "00:00:00";
    public static final String NOTIFICATION_TIME_DAILY_SUMMARY = "10:17:00";

    // (> Android 8.0) 通知頻道的 ID
    public static final String NOTIFICATION_CHANNEL_ID_LOCKSCREEN_RECEIVER = "lock_screen_track";
    public static final String NOTIFICATION_CHANNEL_ID_ONGOING = "ongoing_task";
    public static final String NOTIFICATION_CHANNEL_ID_SUMMARY = "summary";
    public static final String NOTIFICATION_CHANNEL_ID_COMPLETE = "complete_task";
    public static final String NOTIFICATION_CHANNEL_ID_REMAINING_TIME = "remaining_time";


    // (> Android 8.0) 通知頻道的 NAME
    public static final String NOTIFICATION_CHANNEL_NAME_LOCKSCREEN_RECEIVER = "Lock screen track";
    public static final String NOTIFICATION_CHANNEL_NAME_ONGOING = "Ongoing task";
    public static final String NOTIFICATION_CHANNEL_NAME_SUMMARY = "Summary";
    public static final String NOTIFICATION_CHANNEL_NAME_COMPLETE = "Complete task";
    public static final String NOTIFICATION_CHANNEL_NAME_REMAINING_TIME = "Remaining time";

    // Job Scheduler ID
//    public static final int SCHEDULE_JOB_ID_ONGOING = xxx;    // 不用 job scheduler
    public static final int SCHEDULE_JOB_ID_DAILY_DATA_VERGEN = 100;
    public static final int SCHEDULE_JOB_ID_DAILY_SUMMARY = 101;
    public static final int SCHEDULE_JOB_ID_COMPLETE_TASK = 102;
    public static final int SCHEDULE_JOB_ID_REMAINING_TASK = 103;



    // ****** Database ******

    // Version format
    public static final String DB_FORMAT_VER_NO = "yyyy/MM/dd";
    public static final String DB_FORMAT_UPDATE_DATE = "yyyy/MM/dd HH:mm:ss";   // HH 是 24 小時制，hh 是 12 小時制
    public static final String DB_FORMAT_HHMM = "HH:mm";                        // HH 是 24 小時制，hh 是 12 小時制
    public static final String DB_FORMAT_HMS = "HH:mm:ss";

    public static final String DB_ENDLESS_DATE = "4000/01/01";

    // Plan (set target)
    public static final String MODE_PERIOD = "MODE_PERIOD";
    public static final String MODE_CALENDAR = "MODE_CALENDAR";
    public static final String MODE_DAILY = "MODE_DAILY";
    public static final String MODE_WEEKLY = "MODE_WEEKLY";
    public static final String MODE_MONTHLY = "MODE_MONTHLY";
    public static final String MODE_YEARLY = "MODE_YEARLY";

    // Default Tasks
    public static final String TASK_SLEEP = "Sleep";
    public static final String TASK_FREE = "Free";
    public static final String TASK_FAMILY = "Family";
    public static final String TASK_FRIEND = "Friend";

    // Default Category
    public static final String CATEGORY_OTHERS = "Others";
    public static final String CATEGORY_HEALTH = "Health";
    public static final String CATEGORY_RELATIONSHIP = "Relationship";
    public static final String CATEGORY_SELF = "Self";


}
