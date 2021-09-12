package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.ErrorManager;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText taskText;
    private TextView dateText, timeText;
    private FloatingActionButton saveButton;
    private boolean isTimeSet, isTaskSet;
    private ImageButton datePickerButton, timePickerButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TaskViewModel taskViewModel;
    private  AlertDialog.Builder builder;
    private boolean isEditing=false;
    private boolean isTaskUpdated=false,isTimeUpdated=false,isDateUpdated=false;
    private Intent intent;
    private int id;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        taskText = findViewById(R.id.task_editText);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.time_text);
        saveButton = findViewById(R.id.save_button);
        datePickerButton = findViewById(R.id.date_picker);
        timePickerButton = findViewById(R.id.time_picker);
        isTimeSet = isTaskSet = false;
        builder = new AlertDialog.Builder(this);
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        intent=getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_DESCRIPTION))
        {
            isTimeSet = isTaskSet = true;
            mYear = intent.getIntExtra(MainActivity.EXTRA_YEAR,calendar.get(Calendar.YEAR));
            mMonth = intent.getIntExtra(MainActivity.EXTRA_MONTH,calendar.get(Calendar.MONTH));
            mDay = intent.getIntExtra(MainActivity.EXTRA_DAY,calendar.get(Calendar.DAY_OF_MONTH));
            mHour = intent.getIntExtra(MainActivity.EXTRA_HOUR,calendar.get(Calendar.HOUR_OF_DAY));
            mMinute = intent.getIntExtra(MainActivity.EXTRA_MIN,calendar.get(Calendar.MINUTE));
            dateText.setText(getFormattedDateString(mYear,mMonth,mDay));
            timeText.setText(getStringTime(mHour,mMinute));
            taskText.setText(intent.getStringExtra(MainActivity.EXTRA_DESCRIPTION));
            isEditing=true;
            setTitle("Edit Task");
            id=intent.getIntExtra(MainActivity.EXTRA_ID,0);
        }
        else
        {
            isTimeSet = isTaskSet = false;
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
            isEditing=false;

        }

        datePickerButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        taskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isEditing)
                {
                    isTaskUpdated=true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) {
                    isTaskSet = true;
                }
                else
                {
                    isTaskSet=false;
                }

            }
        });

        taskViewModel= ViewModelProviders.of(this).get(TaskViewModel.class);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                dateText.setText(getFormattedDateString(mYear,mMonth,mDay));
                                if (isEditing)
                                {
                                    isDateUpdated=true;
                                }


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.time_picker:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                if (mHour < 12)
                                    timeText.setText(getStringTime(mHour, mMinute) + " AM");
                                else
                                    timeText.setText(getStringTime(mHour, mMinute));
                                isTimeSet = true;
                                if (isEditing)
                                    isTimeUpdated=true;
                            }
                        }, mHour, mMinute, DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();
                break;
            case R.id.save_button:
                if (isTimeSet && isTaskSet)
                {
                    String taskString=taskText.getText().toString();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_saved, (ViewGroup) findViewById(R.id.toast_saved_layout));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    if (!isEditing)
                    {
                        Task task=new Task(mYear,mMonth,mDay,mHour,mMinute,taskString,0);
                        taskViewModel.insert(task);
                    }
                    else
                    {
                        Task task=new Task(mYear,mMonth,mDay,mHour,mMinute,taskString,intent.getIntExtra(MainActivity.EXTRA_STATUS,0));
                        task.setId(id);
                        taskViewModel.update(task);
                        TextView toastText=layout.findViewById(R.id.save_toast_text);
                        toastText.setText("Task Updated!");
                    }




                    toast.setView(layout);
                    toast.show();
                    finish();


                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_save_error, (ViewGroup) findViewById(R.id.toast_layout));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if(isEditing)
        {
            if (isTimeUpdated||isTaskUpdated||isDateUpdated)
            {
                showAlertDialog();
            }
            else
            {
                finish();
            }
        }
        else if (isTaskSet || isTimeSet)
        {
            showAlertDialog();
        }
        else
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFormattedDateString(int year, int month, int day)
    {
        String stringDate=getStringDate(year, month, day);
        LocalDate dt = LocalDate.parse(stringDate);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("E, dd MMMM yyyy");
        return formatter.format(dt);

    }
    private void showAlertDialog(){
        builder.setMessage("Changed data will be lost")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface Dialog, int which) {
                        Dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface Dialog, int which) {
                        Dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Discard Changes?");
        alert.show();
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
        String hr = String.valueOf(hour), min = String.valueOf(minute);
        if (hour < 10)
            hr = "0" + hr;
        if (minute < 10)
            min = "0" + min;
        return (hr + ":" + min);

    }
}