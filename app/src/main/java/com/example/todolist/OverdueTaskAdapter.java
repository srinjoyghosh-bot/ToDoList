package com.example.todolist;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OverdueTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Task> tasks=new ArrayList<>();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private onItemClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==TYPE_ITEM)
        {
            View itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item,parent,false);
            return new TaskHolder(itemView);
        }
        else if ((viewType==TYPE_HEADER))
        {
            View itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_header,parent,false);
            return new HeaderViewHolder(itemView);
        }
        else
            return null;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskHolder)
        {
            TaskHolder taskHolder=(TaskHolder) holder;
            Task currentTask= tasks.get(position);
            taskHolder.checkBox.setText(currentTask.getDescription());
            String date=getFormattedDateString(currentTask.getYear(), currentTask.getMonth(), currentTask.getDay());
            String time=getStringTime(currentTask.getHour(), currentTask.getMinute());
            taskHolder.dateTimeText.setText(date+", "+time);
//            taskHolder.dateTimeText.setTextColor(Color.parseColor("#FF0000"));
            if (currentTask.getCompletion_status()==1)
            {
                taskHolder.checkBox.setChecked(true);
                taskHolder.checkBox.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                taskHolder.itemView.setBackgroundResource(R.drawable.task_item_checked);

            }
            else
            {
                taskHolder.checkBox.setChecked(false);
                taskHolder.checkBox.setPaintFlags(0);
                taskHolder.itemView.setBackgroundResource(R.drawable.task_item_bg);
            }

        }
        else if (holder instanceof HeaderViewHolder)
        {

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerText.setText("Overdue");
            headerViewHolder.headerText.setTextColor(Color.parseColor("#FF0000"));

        }
    }
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }
    public void setTasks(List<Task> tasks)
    {
        this.tasks=tasks;
        notifyDataSetChanged();
    }
    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public HeaderViewHolder(View view) {
            super(view);
            headerText=view.findViewById(R.id.rv_header_text);

        }
    }


    class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CheckBox checkBox;
        private TextView dateTimeText;
        private TaskViewModel taskViewModel;
        private View view;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.task_checkBox);
            dateTimeText=itemView.findViewById(R.id.date_time_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION&&listener!=null)
                    {
                        listener.onItemClick(tasks.get(position));
                    }
                }
            });
            checkBox.setOnClickListener(this);
            taskViewModel=ViewModelProviders.of((FragmentActivity) itemView.getContext()).get(TaskViewModel.class);
            view=itemView;

        }

        @Override
        public void onClick(View v) {
            if (v.getId()==checkBox.getId())
            {
                if (checkBox.isChecked())
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Completion would delete the task");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Task task=getItem(getAdapterPosition());
                            taskViewModel.delete(task);
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkBox.setChecked(false);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert= builder.create();
                    alert.setTitle("Confirm Completion");
                    alert.show();

                }

                else
                {
                    checkBox.setPaintFlags(0);
                    Task task=getItem(getAdapterPosition());
                    task.setCompletion_status(0);
                    taskViewModel.update(task);
                    view.setBackgroundResource(R.drawable.task_item_bg);
//                    Toast.makeText(v.getContext(), "Item unchecked!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFormattedDateString(int year, int month, int day)
    {
        String stringDate=getStringDate(year, month, day);
        LocalDate dt = LocalDate.parse(stringDate);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("E, dd MMMM yyyy");
        return formatter.format(dt);

    }

    private String getStringDate(int year, int month, int day) {
        String yyyy = String.valueOf(year), mm = String.valueOf(month + 1), dd = String.valueOf(day);
        if (month < 9)
            mm = "0" + mm;
        if (day < 10)
            dd = "0" + dd;
//        return (dd + "/" + mm + "/" + yyyy);
        return (yyyy+"-"+mm+"-"+dd);
    }

    private String getStringTime(int hour, int minute) {
        int hourModified;
        String timePostfix;
        if (hour>12)
        {
            hour=hour-12;
            timePostfix=" PM";
        }
        else
        {
            timePostfix=" AM";
        }
        String hr = String.valueOf(hour), min = String.valueOf(minute);
        if (hour < 10)
            hr = "0" + hr;
        if (minute < 10)
            min = "0" + min;
        return (hr + ":" + min+timePostfix);

    }
    public Task getItem(int position)
    {
        Task currentEntry= tasks.get(position);

        return currentEntry;
    }
    public void addItem(Task entry,int position)
    {
        tasks.add(position,entry);
        notifyDataSetChanged();
    }
    public interface onItemClickListener{
        void onItemClick(Task task);
    }
    public void setOnItemClickListener(onItemClickListener listener)
    {
        this.listener=listener;
    }
}
