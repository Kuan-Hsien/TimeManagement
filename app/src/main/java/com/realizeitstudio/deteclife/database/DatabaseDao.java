package com.realizeitstudio.deteclife.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.dml.GetTraceDetail;
import com.realizeitstudio.deteclife.dml.GetTraceSummary;
import com.realizeitstudio.deteclife.object.CategoryDefineTable;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.object.TimeTracingTable;

import java.util.List;

/**
 * Created by Ken on 2018/9/24
 */
@Dao
public interface DatabaseDao {

    //****** Category ******
    //
    @Query("SELECT category_name, category_color, category_priority, is_user_def FROM category_define_table ORDER BY category_priority")
    List<CategoryDefineTable> getAllCategoryList();

    @Query("SELECT * FROM category_define_table "
            + "WHERE category_name LIKE :categoryName LIMIT 1")   //[TODO] 應該不用卡 LIMIT
    CategoryDefineTable findCategoryByName(String categoryName);


    @Insert
    void insertAllCategory(CategoryDefineTable... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCategory(CategoryDefineTable item);


    @Delete
    void deleteAllCategory(CategoryDefineTable item);

    @Delete()
    void deleteCategoryList(List<CategoryDefineTable> item);

    @Delete()
    void deleteCategory(CategoryDefineTable item);



    //****** Task ******

    @Query("SELECT * FROM task_define_table")
    List<TaskDefineTable> getTaskList();

    @Query("SELECT category_name, task_name, task_color, task_icon, task_priority, is_user_def FROM task_define_table")
    List<TaskDefineTable> getAllTaskList();

    @Query("SELECT * FROM "
            + "(SELECT 'TASK' AS item_catg, c.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority "
            + "   FROM task_define_table t "
            + "  INNER JOIN category_define_table c "
            + "     ON t.category_name = c.category_name "
            + "  UNION ALL "
            + " SELECT 'CATEGORY' AS item_catg, c.category_name, c.category_color, c.category_priority, '', '', '', 0 "
            + "   FROM category_define_table c ) "
            + "  ORDER BY category_priority, item_catg, task_priority")
    List<GetCategoryTaskList> getCategoryTaskList();

    @Query("SELECT * FROM task_define_table "
            + "WHERE category_name LIKE :categoryName "
            + "  AND task_name LIKE :taskName LIMIT 1")   //[TODO] 應該不用卡 LIMIT
    TaskDefineTable findTaskByTaskName(String categoryName, String taskName);


    @Insert
    void insertAll(TaskDefineTable... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTask(TaskDefineTable item);

    @Delete
    void deleteAllTask(TaskDefineTable item);

    @Delete()
    void deleteTaskList(List<TaskDefineTable> item);

    @Delete()
    void deleteTask(TaskDefineTable item);



    // ****** Plan ******
    // MODE_PERIOD: List tasks plan in a specific period
    // MODE_CALENDAR: List to-do items in calendar

    // 1.1 Query all plan list
    @Query("SELECT * FROM time_planning_table")
    List<TimePlanningTable> getAllPlanList();

    // 1.5 Query target-list (tasks inner join with TimePlanningTable to get plan time) (both daily or weekly)
    @Query("WITH target "
            + " AS (SELECT p.mode, p.category_name, p.task_name, p.start_time, p.end_time, p.cost_time "
            + "       FROM time_planning_table p "
            + "      WHERE p.mode = :mode "
            + "        AND :startTime >= p.start_time AND :endTime <= p.end_time "
            + "    ) "
            + "SELECT target.mode         AS mode, "
            + "       t.category_name     AS category_name, "
            + "       c.category_color    AS category_color, "
            + "       c.category_priority AS category_priority, "
            + "       t.task_name         AS task_name, "
            + "       t.task_color        AS task_color, "
            + "       t.task_icon         AS task_icon, "
            + "       t.task_priority     AS task_priority, "
            + "       target.start_time   AS start_time, "
            + "       target.end_time     AS end_time, "
            + "       IFNULL(target.cost_time, 0) AS cost_time "
            + "  FROM target target "
            + "  LEFT JOIN task_define_table t USING (category_name, task_name) "
            + "  LEFT JOIN category_define_table c USING (category_name)"
            + " ORDER BY category_priority, task_priority")
    List<GetTaskWithPlanTime> getTaskListWithPlanTime(String mode, String startTime, String endTime);

    @Query("SELECT * FROM time_planning_table p "
            + " WHERE p.category_name = :categoryName "
            + "   AND p.task_name = :taskName "
            + " LIMIT 1")   //[TODO] 應該不用卡 LIMIT
    TimePlanningTable findPlanTimeByTaskName(String categoryName, String taskName);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPlanItem(TimePlanningTable item);

    @Delete()
    void deleteTargetList(List<TimePlanningTable> item);

    @Delete()
    void deleteTarget(TimePlanningTable item);


    // ****** Trace ******


    // 2.1 Query all trace list
    @Query("SELECT * FROM time_tracing_table")
    List<TimeTracingTable> getAllTraceList();


    @Query("SELECT * FROM time_tracing_table t "
            + "  WHERE t.category_name = :categoryName "
            + "    AND t.task_name = :taskName "
            + "  LIMIT 1")
    TimeTracingTable getTraceTimeByTaskName(String categoryName, String taskName);


    // Query current trace task
    @Query("SELECT * FROM time_tracing_table t "
            + " WHERE t.ver_no = :verNo "
            + "   AND t.end_time IS NULL "
            + " ORDER By t.start_time DESC "
            + " LIMIT 1")
    TimeTracingTable getCurrentTraceTask(String verNo);


//    @Insert
//    void insertAll(TaskDefineTable... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTraceItem(TimeTracingTable item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTraceList(List<TimeTracingTable> item);


//    @Delete
//    void deleteAllTrace(TimeTracingTable item);

    @Delete()
    void deleteTraceList(List<TimeTracingTable> item);

    @Delete()
    void deleteTraceItem(TimeTracingTable item);


    // Query summary trace result in a specific time period
    // category and task could be ALL
    @Query("SELECT t.ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + " FROM time_tracing_table t "
            + "WHERE t.ver_no >= :startVerNo "
            + "  AND t.ver_no <= :endVerNo "
            + "  AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "  AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "GROUP BY t.ver_no, t.category_name, t.task_name "
            + "ORDER BY t.ver_no, t.category_name, t.task_name")
    List<GetTraceSummary> getTraceSummary(String startVerNo, String endVerNo, String categoryList, String taskList);


    // Daily 會撈出一週七天的版本 + Weekly 一整週 (如果輸入星期天到星期四，就是總共撈 5 天資料)
    // Query record-list (records inner join with task/category master table to get detail trace result) (both daily or weekly)
    // mode could be one of { "DAILY", "WEEKLY", "ALL" }
    // category and task also could be ALL
    @Query("SELECT record.mode, record.ver_no, t.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority, record.cost_time AS cost_time "
            + " FROM (SELECT 'MODE_DAILY' AS mode, t.ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "         FROM time_tracing_table t "
            + "        WHERE ( (:mode = 'MODE_DAILY') OR (:mode = 'ALL') ) "
            + "          AND t.ver_no >= :startVerNo "
            + "          AND t.ver_no <= :endVerNo "
            + "          AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "          AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        GROUP BY t.ver_no, t.category_name, t.task_name"
            + "        UNION ALL "
            + "       SELECT 'MODE_WEEKLY' AS mode, :startVerNo AS ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "         FROM time_tracing_table t "
            + "        WHERE ( (:mode = 'MODE_WEEKLY') OR (:mode = 'ALL') ) "
            + "          AND t.ver_no >= :startVerNo "
            + "          AND t.ver_no <= :endVerNo "
            + "          AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "          AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        GROUP BY t.category_name, t.task_name"
            + "      ) record "
            + "INNER JOIN task_define_table t "
            + "   ON record.task_name = t.task_name "
            + "INNER JOIN category_define_table c "
            + "   ON record.category_name = c.category_name "
            + "ORDER BY record.mode, record.ver_no, c.category_priority, t.task_priority")
    List<GetTraceDetail> getTraceDetail(String mode, String startVerNo, String endVerNo, String categoryList, String taskList);


    // Daily 會撈出 endVerNo 當天的版本 + Weekly 一整週 (如果輸入星期天到星期四，就是總共撈 5 天資料)
    @Query("SELECT record.mode, record.ver_no, t.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority, record.cost_time AS cost_time "
            + " FROM (SELECT 'MODE_DAILY' AS mode, t.ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "         FROM time_tracing_table t "
            + "        WHERE ( (:mode = 'MODE_DAILY') OR (:mode = 'ALL') ) "
            + "          AND t.ver_no = :endVerNo "
            + "          AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "          AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        GROUP BY t.ver_no, t.category_name, t.task_name"
            + "        UNION ALL "
            + "       SELECT 'MODE_WEEKLY' AS mode, :startVerNo AS ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "         FROM time_tracing_table t "
            + "        WHERE ( (:mode = 'MODE_WEEKLY') OR (:mode = 'ALL') ) "
            + "          AND t.ver_no >= :startVerNo "
            + "          AND t.ver_no <= :endVerNo "
            + "          AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "          AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        GROUP BY t.category_name, t.task_name"
            + "      ) record "
            + "INNER JOIN task_define_table t "
            + "   ON record.task_name = t.task_name "
            + "INNER JOIN category_define_table c "
            + "   ON record.category_name = c.category_name "
            + "ORDER BY record.mode, record.ver_no, c.category_priority, t.task_priority")
    List<GetTraceDetail> getTraceDailySummary(String mode, String startVerNo, String endVerNo, String categoryList, String taskList);


    // ****** 撈出 Daily + Weekly 的 record + target
    // Daily 會撈出 endVerNo 當天的版本 + Weekly 一整週 (如果輸入星期一到星期四，就是總共撈 4 天資料)
    // trace 的日期精準到 (long) mills
    //   第一版先做成過濾掉正在進行的項目，不列入統計 (等 user 存入現在在進行的項目才會被撈出來)
    // plan 的 start_time 和 end_time 用來當作版號，是 yyyy/mm/dd 的字串 (即 trace 的 ver_no)


    // [TODO] plan 目前沒有分版本，未來可再把版號加回 p..ver_no = :endVerNo，現在先用 endVerNo 當作 daily 版號，用 startVerNo 當作 weekly 版號 (因為撈出 daily 和 weekly 之後就直接顯示了)

    @Query("WITH record "
            + " AS (SELECT 'MODE_DAILY' AS mode, t.ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "       FROM time_tracing_table t "
            + "      WHERE ( (:mode = 'MODE_DAILY') OR (:mode = 'ALL') ) "
            + "        AND t.ver_no = :endVerNo"            // Daily 只看一天: endVerNo
            + "        AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "        AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        AND t.end_time IS NOT NULL "
            + "      GROUP BY t.ver_no, t.category_name, t.task_name"
            + "      UNION ALL "
            + "     SELECT 'MODE_WEEKLY' AS mode, :startVerNo AS ver_no, t.category_name, t.task_name, SUM(t.cost_time) AS cost_time "
            + "       FROM time_tracing_table t "
            + "      WHERE ( (:mode = 'MODE_WEEKLY') OR (:mode = 'ALL') ) "
            + "        AND t.ver_no >= :startVerNo "
            + "        AND t.ver_no <= :endVerNo "
            + "        AND ( (:categoryList = 'ALL') OR (t.category_name IN (:categoryList)) ) "
            + "        AND ( (:taskList = 'ALL') OR (t.task_name IN (:taskList)) ) "
            + "        AND t.end_time IS NOT NULL "
            + "      GROUP BY t.category_name, t.task_name"
            + "    ), "
            + "    target "
            + " AS (SELECT p.mode, :endVerNo AS ver_no, p.category_name, p.task_name, p.cost_time AS plan_time "
            + "       FROM time_planning_table p "
            + "      WHERE ( (:mode = 'MODE_DAILY') OR (:mode = 'ALL') ) "
            + "        AND p.mode = 'MODE_DAILY' "
            + "        AND (:endVerNo >= p.start_time) "    // Daily 只看一天: endVerNo
            + "        AND (:endVerNo <= p.end_time) "
            + "        AND ( (:categoryList = 'ALL') OR (p.category_name IN (:categoryList)) ) "
            + "        AND ( (:taskList = 'ALL') OR (p.task_name IN (:taskList)) ) "
            + "      UNION ALL "
            + "     SELECT p.mode, :startVerNo AS ver_no, p.category_name, p.task_name, p.cost_time AS plan_time "
            + "       FROM time_planning_table p "
            + "      WHERE (:mode = 'MODE_WEEKLY' OR :mode = 'ALL') "
            + "        AND p.mode = 'MODE_WEEKLY' "
            + "        AND (:startVerNo >= p.start_time) "
            + "        AND (:endVerNo <= p.end_time) "
            + "        AND ( (:categoryList = 'ALL') OR (p.category_name IN (:categoryList)) ) "
            + "        AND ( (:taskList = 'ALL') OR (p.task_name IN (:taskList)) ) "
            + "    ),"
            + "    result "     // record vs target
            + " AS (SELECT r.mode, r.ver_no, r.category_name, r.task_name, r.cost_time, t.plan_time"
            + "       FROM record r "
            + "       LEFT JOIN target t "
            + "      USING (mode, category_name, task_name) "
            + "      UNION ALL "
            + "     SELECT t.mode, t.ver_no, t.category_name, t.task_name, r.cost_time, t.plan_time "
            + "       FROM target t "
            + "       LEFT JOIN record r "
            + "      USING (mode, category_name, task_name) "
            + "      WHERE r.cost_time IS NULL "    // removes rows that already included in the result set of the first SELECT statement. (only need to append rows which has plan but no record(no cost_time))
            + "    ) "
            + "SELECT result.mode             AS mode, "
            + "       result.ver_no           AS ver_no, "
            + "       result.category_name    AS category_name, "
            + "       c.category_color        AS category_color, "
            + "       c.category_priority     AS category_priority, "
            + "       result.task_name        AS task_name, "
            + "       t.task_color            AS task_color, "
            + "       t.task_icon             AS task_icon, "
            + "       t.task_priority         AS task_priority, "
            + "       IFNULL(result.cost_time, 0) AS cost_time, "
            + "       IFNULL(result.plan_time, 0) AS plan_time "
            + "  FROM result result "
            + " INNER JOIN task_define_table t USING(category_name, task_name) "
            + " INNER JOIN category_define_table c USING(category_name) "
            + " ORDER BY mode, ver_no, category_priority, task_priority ")
    List<GetResultDailySummary> getResultDailySummary(String mode, String startVerNo, String endVerNo, String categoryList, String taskList);


    // ****** Icon ******

    // Query all icon list
    @Query("SELECT * FROM icon_define_table")
    List<IconDefineTable> getAllIconList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addIconItem(IconDefineTable item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addIconList(List<IconDefineTable> item);


    // ****** Color ******

    // Query all color list
    @Query("SELECT * FROM color_define_table")
    List<ColorDefineTable> getAllColorList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addColorItem(ColorDefineTable item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addColorList(List<ColorDefineTable> item);

}
