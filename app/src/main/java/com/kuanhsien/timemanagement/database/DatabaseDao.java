package com.kuanhsien.timemanagement.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
import com.kuanhsien.timemanagement.dml.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.object.TimePlanningTable;

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

    @Query("SELECT * FROM category_define_table " +
            "WHERE category_name LIKE :categoryName LIMIT 1")   //[TODO] 應該不用卡 LIMIT
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

//    @Query("SELECT c.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority " +
//             "FROM task_define_table t " +
//            "INNER JOIN category_define_table c " +
//               "ON t.category_name = c.category_name " +
//            "ORDER BY c.category_priority, t.task_priority")
//    List<GetCategoryTaskList> getCategoryTaskList();

    @Query("SELECT * FROM " +
            "(SELECT 'TASK' AS item_catg, c.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority " +
               "FROM task_define_table t " +
              "INNER JOIN category_define_table c " +
                 "ON t.category_name = c.category_name " +
              "UNION ALL " +
            "SELECT 'CATEGORY' AS item_catg, c.category_name, c.category_color, c.category_priority, '', '', '', 0 " +
               "FROM category_define_table c ) " +
            "ORDER BY category_priority, item_catg, task_priority")
    List<GetCategoryTaskList> getCategoryTaskList();

    @Query("SELECT * FROM task_define_table " +
            "WHERE category_name LIKE :categoryName " +
            "AND task_name LIKE :taskName LIMIT 1")   //[TODO] 應該不用卡 LIMIT
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

    // 1.2 Query all plan list of a specific period (daily or weekly)
//    @Query("SELECT * FROM time_planning_table WHERE mode = :mode AND period = :period AND start_time = :startTime")
//    List<TimePlanningTable> getPlanListByPeriod(String mode, String period, String startTime);

    // 1.3 Query all plan list of a specific period (daily or weekly) join with TaskDefineTable to get task color and icons
//    @Query("SELECT p.uid, p.category_name, p.task_name, p.cost_time, t.task_color, t.task_icon " +
//             "FROM time_planning_table p " +
//            "INNER JOIN task_define_table t ON p.task_name = t.task_name " +
//            "WHERE p.mode = :mode " +
//              "AND p.period = :period ")
//    List<TimePlanningTable> getPlanTaskListByPeriod(String mode, String period);


    // *******
    // 1.4 Query all tasks and left join with TimePlanningTable to get plan time (both daily or weekly)
//    @Query("SELECT t.category_name, t.task_name, t.task_color, t.task_icon, IFNULL(p.cost_time, \"\") AS cost_time " +
//             "FROM task_define_table t " +
//             "LEFT JOIN (SELECT p.category_name, p.task_name, p.cost_time " +
//                          "FROM time_planning_table p " +
//                         "WHERE p.mode = :mode " +
//                           "AND p.start_time >= :startTime AND p.end_time < :endTime" +
//                       ") p " +
//               "ON t.task_name = p.task_name")
//    List<GetTaskWithPlanTime> getAllTaskListWithPlanTime(String mode, String startTime, String endTime);

    // 1.5 Query target-list (tasks inner join with TimePlanningTable to get plan time) (both daily or weekly)
    @Query("SELECT p.mode, t.category_name, c.category_color, c.category_priority, t.task_name, t.task_color, t.task_icon, t.task_priority, p.start_time, p.end_time, IFNULL(p.cost_time, \"\") AS cost_time " +
             "FROM task_define_table t " +
            "INNER JOIN (SELECT p.mode, p.category_name, p.task_name, p.start_time, p.end_time, p.cost_time " +
                          "FROM time_planning_table p " +
                         "WHERE p.mode = :mode " +
                           "AND p.start_time >= :startTime AND p.end_time < :endTime" +
                       ") p " +
               "ON t.task_name = p.task_name " +
            "INNER JOIN category_define_table c " +
               "ON t.category_name = c.category_name " +
            "ORDER BY c.category_priority, t.task_priority")
    List<GetTaskWithPlanTime> getTaskListWithPlanTime(String mode, String startTime, String endTime);



    @Query("SELECT * FROM time_planning_table p " +
            "WHERE p.category_name = :categoryName " +
              "AND p.task_name = :taskName " +
            "LIMIT 1")   //[TODO] 應該不用卡 LIMIT
    TimePlanningTable findPlanTimeByTaskName(String categoryName, String taskName);
//
//
//    @Insert
//    void insertAll(TaskDefineTable... items);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPlanItem(TimePlanningTable item);

//    @Delete
//    void deleteAllTask(TimePlanningTable item);

    @Delete()
    void deleteTargetList(List<TimePlanningTable> item);

    @Delete()
    void deleteTarget(TimePlanningTable item);


    // *******
    // 1.6 (Insert) add a new target
    // Query all tasks and left join with TimePlanningTable to get plan time (both daily or weekly)
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//
//    INSERT INTO time_planning_table()
//    @Query("SELECT t.uid as task_id, t.category_name, t.task_name, t.task_color, t.task_icon, IFNULL(p.uid, -1) AS plan_id, IFNULL(p.cost_time, \"\") AS cost_time " +
//            "FROM task_define_table t " +
//            "LEFT JOIN (SELECT p.uid, p.category_name, p.task_name, p.cost_time " +
//            "FROM time_planning_table p " +
//            "WHERE p.mode = :mode " +
//            "AND p.start_time >= :startTime AND p.end_time < :endTime" +
//            ") p " +
//            "ON t.task_name = p.task_name")
//    List<GetTaskWithPlanTime> getAllTaskListWithPlanTime(String mode, String startTime, String endTime);

}
