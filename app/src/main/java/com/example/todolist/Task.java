package com.example.todolist;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String description;
    private int completion_status;
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getDescription() {
        return description;
    }

    public int getCompletion_status() {
        return completion_status;
    }

    public int getId() {
        return id;
    }

    public Task(int year, int month, int day, int hour, int minute, String description, int completion_status) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.description = description;
        this.completion_status = completion_status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCompletion_status(int completion_status) {
        this.completion_status = completion_status;
    }
}
