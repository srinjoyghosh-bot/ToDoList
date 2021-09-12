package com.example.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);
    @Update
    void update(Task task);
    @Delete
    void delete(Task task);
    @Query("DELETE FROM task_table")
    void deleteAllTasks();
    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAllTasks();
    @Query("SELECT * FROM task_table WHERE year==:year AND month==:month AND day==:day")
    LiveData<List<Task>> getDayTasks(int year,int month,int day);
}
