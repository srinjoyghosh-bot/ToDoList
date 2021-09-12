package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addTask;
    private RecyclerView tasksRecyclerView;
    private RecyclerView overdueRecyclerView;
    private RecyclerView upcomingTaskRecyclerView;
    private View emptyView;
    private TaskAdapter mAdapter;
    private OverdueTaskAdapter overdueAdapter;
    private UpcomingTaskAdapter upcomingTaskAdapter;
    private TaskViewModel taskViewModel;
    public static final String EXTRA_DESCRIPTION="com.example.todolist.EXTRA_DESCRIPTION";
    public static final String EXTRA_YEAR="com.example.todolist.EXTRA_YEAR";
    public static final String EXTRA_MONTH="com.example.todolist.EXTRA_MONTH";
    public static final String EXTRA_DAY="com.example.todolist.EXTRA_DAY";
    public static final String EXTRA_HOUR="com.example.todolist.EXTRA_HOUR";
    public static final String EXTRA_MIN="com.example.todolist.EXTRA_MIN";
    public static final String EXTRA_STATUS="com.example.todolist.EXTRA_STATUS";
    public static final String EXTRA_ID="com.example.todolist.EXTRA_ID";
    private List<Task> overDueList=new ArrayList<>();
    private List<Task> todayList=new ArrayList<>();
    private List<Task> upcomingList=new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTask=findViewById(R.id.add_task_fab);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddTaskActivity.class);
                startActivity(intent);
            }
        });
        tasksRecyclerView=findViewById(R.id.tasks_rv);
        overdueRecyclerView=findViewById(R.id.overdue_rv);
        upcomingTaskRecyclerView=findViewById(R.id.upcoming_rv);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        overdueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upcomingTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setNestedScrollingEnabled(true);
        overdueRecyclerView.setHasFixedSize(true);
        overdueRecyclerView.setNestedScrollingEnabled(true);
        upcomingTaskRecyclerView.setHasFixedSize(true);
        upcomingTaskRecyclerView.setNestedScrollingEnabled(true);
        mAdapter=new TaskAdapter();
        overdueAdapter=new OverdueTaskAdapter();
        upcomingTaskAdapter=new UpcomingTaskAdapter();
        tasksRecyclerView.setAdapter(mAdapter);
        overdueRecyclerView.setAdapter(overdueAdapter);
        upcomingTaskRecyclerView.setAdapter(upcomingTaskAdapter);
        taskViewModel= ViewModelProviders.of(this).get(TaskViewModel.class);
        LocalDateTime dateTime=LocalDateTime.now();
        int hour=dateTime.getHour();
        int minute= dateTime.getMinute();
        int year=dateTime.getYear();
        int month=dateTime.getMonthValue();
        int day=dateTime.getDayOfMonth();
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                overDueList.clear();
                todayList.clear();
                upcomingList.clear();
                for (Task task:tasks)
                {
                    if (task.getYear()<year||task.getMonth()<month-1||task.getDay()<day)
                    {
                        if(task.getCompletion_status()==0)
                        {
                            overDueList.add(task);

                        }
                        else
                        {
                            taskViewModel.delete(task);
                        }


                    }
                    else if(task.getYear()==year && task.getMonth()==month -1 && task.getDay()==day)
                    {
                        todayList.add(task);

                    }
                    else
                    {
                        upcomingList.add(task);

                    }
                }
                mAdapter.setTasks(todayList);
                upcomingTaskAdapter.setTasks(upcomingList);
                overdueAdapter.setTasks(overDueList);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                Task swipedTask=mAdapter.getItem(position);
                taskViewModel.delete(swipedTask);

                Snackbar snackbar=Snackbar.make(tasksRecyclerView,"Task was deleted", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                taskViewModel.insert(swipedTask);
                                mAdapter.addItem(swipedTask,position);
                            }
                        });
                snackbar.show();
            }

        }).attachToRecyclerView(tasksRecyclerView);
        mAdapter.setOnItemClickListener(new TaskAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                sendIntent(task);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                Task swipedTask=overdueAdapter.getItem(position);
                taskViewModel.delete(swipedTask);

                Snackbar snackbar=Snackbar.make(overdueRecyclerView,"Task was deleted", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                taskViewModel.insert(swipedTask);
                                overdueAdapter.addItem(swipedTask,position);
                            }
                        });
                snackbar.show();
            }

        }).attachToRecyclerView(overdueRecyclerView);
        overdueAdapter.setOnItemClickListener(new OverdueTaskAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                sendIntent(task);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                Task swipedTask=upcomingTaskAdapter.getItem(position);
                taskViewModel.delete(swipedTask);

                Snackbar snackbar=Snackbar.make(upcomingTaskRecyclerView,"Task was deleted", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                taskViewModel.insert(swipedTask);
                                upcomingTaskAdapter.addItem(swipedTask,position);
                            }
                        });
                snackbar.show();
            }

        }).attachToRecyclerView(upcomingTaskRecyclerView);
        upcomingTaskAdapter.setOnItemClickListener(new UpcomingTaskAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                sendIntent(task);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_all:
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("All data will be lost")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface Dialog, int which) {
                                taskViewModel.deleteAll();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface Dialog, int which) {
                                Dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Delete all tasks?");
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
    public void sendIntent(Task task)
    {
        Intent intent=new Intent(MainActivity.this,AddTaskActivity.class);
        intent.putExtra(EXTRA_DESCRIPTION,task.getDescription());
        intent.putExtra(EXTRA_YEAR,task.getYear());
        intent.putExtra(EXTRA_MONTH,task.getMonth());
        intent.putExtra(EXTRA_DAY,task.getDay());
        intent.putExtra(EXTRA_HOUR,task.getHour());
        intent.putExtra(EXTRA_MIN,task.getMinute());
        intent.putExtra(EXTRA_STATUS,task.getCompletion_status());
        intent.putExtra(EXTRA_ID,task.getId());
        startActivity(intent);
    }

}