package com.websarva.wings.android.mytest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.websarva.wings.android.mytest.db.TaskContract;
import com.websarva.wings.android.mytest.db.TaskDbHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {
    private static int number;
    private static final Object Tag = "MainActivity";
    private TaskDbHelper _mHelper;
    private ListView _mTaskListView;
    private ArrayAdapter<String> _mAdapter;
    private AlarmManager _am;
    private PendingIntent _pending;
    private int requestCode;

    private int _year;
    private int _month;
    private int _dayOfMonth;
    private int _hourOfDay;
    private int _minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        _mHelper = new TaskDbHelper(getApplicationContext());
        _mTaskListView = (ListView) findViewById(R.id.list_todo);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        findViewById(R.id.schedule_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number += 1;
                EditText taskEditText = findViewById(R.id.taskEditText);
                String task = taskEditText.getText().toString();
                Log.d((String) Tag, "追加された項目:" + task);


                // データベースを読み込み値を設定する
                SQLiteDatabase db = _mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                values.put(TaskContract.TaskEntry.COL_TASK_SUBTITLE, number);
                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                db.close();

                // 入力した値の行のid値を取得し、requestCodeに設定する
                SQLiteDatabase db1 = _mHelper.getReadableDatabase();
                Cursor cursor = db1.query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry.COL_TASK_TITLE,TaskContract.TaskEntry._ID},
                        TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                        new String[] {task},null,null,null);
                cursor.moveToFirst();
                requestCode = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));

                // ダイアログに入力した値をcalendarクラスにセットする
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.YEAR, _year);
                calendar.set(Calendar.MONTH, _month);
                calendar.set(Calendar.DAY_OF_MONTH, _dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, _hourOfDay);
                calendar.set(Calendar.MINUTE, _minute);
                calendar.set(Calendar.SECOND, 0);

                String time = "通知　" + _year + "年" + _month + "月" +
                        _dayOfMonth + "日" + _hourOfDay + ":" + _minute;

                // 現在時刻より進んでいたら通知を行う
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {

                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent.putExtra("RequestCode", requestCode);
                    intent.putExtra("task", task);


                    _pending = PendingIntent.getBroadcast(
                            getApplicationContext(), requestCode, intent, 0);
                    _am = (AlarmManager) getSystemService(ALARM_SERVICE);

                    if (_am != null) {
                        _am.setExact(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(), _pending);
                    }

                }

                finish();

            }
        });

    }

    public void onTimeClick(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //年月日の取得
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d("test", String.format("%04d:%02d:%02d",year, month + 1, dayOfMonth));
                        _year = year;
                        _month = month;
                        _dayOfMonth = dayOfMonth;
                    }
                }, year, month, day);
        dialog.show();
    }

    public void showTimePickerDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //時刻取得
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("test", String.format("%02d:%02d", hourOfDay,minute));
                        _hourOfDay = hourOfDay;
                        _minute = minute;
                    }
                },
                hour,minute,false);
        dialog.show();
    }

    //保存ボタンをタップしたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnVal = true;
        int itemId = item.getItemId();
        if(itemId == android.R.id.home) {
            finish();
        }
        else {
            returnVal = super.onOptionsItemSelected(item);
        }
        return returnVal;
    }

}