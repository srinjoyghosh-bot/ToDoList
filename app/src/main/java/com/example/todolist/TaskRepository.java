package com.example.todolist;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    public TaskRepository(Application application)
    {
        TaskDatabase database=TaskDatabase.getInstance(application);
        taskDao= database.taskDao();
        allTasks= taskDao.getAllTasks();
    }
    public void insert(Task task)
    {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }
    private static class InsertTaskAsyncTask extends AsyncTask<Task,Void,Void>{
        private TaskDao taskDao;

        public InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }
    public void update(Task task)
    {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }
    private static class UpdateTaskAsyncTask extends AsyncTask<Task,Void,Void>{
        private TaskDao taskDao;

        public UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }
    public void delete(Task task)
    {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }
    private static class DeleteTaskAsyncTask extends AsyncTask<Task,Void,Void>{
        private TaskDao taskDao;

        public DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }
    public void deleteAll()
    {
        new DeleteAllAsyncTask(taskDao).execute();
    }
    private static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private TaskDao taskDao;

        public DeleteAllAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.deleteAllTasks();
            return null;
        }
    }
    public LiveData<List<Task>> getDayTasks(int year,int month,int day)
    {
        return taskDao.getDayTasks(year,month,day);
    }
    public LiveData<List<Task>> getAllTasks()
    {
        return allTasks;
    }
}
