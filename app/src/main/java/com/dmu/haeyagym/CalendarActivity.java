package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    //View Objects
    private MaterialCalendarView materialCalendarView;
    private ListView calendarList;

    //BD Associated
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cursor;

    //Date Format (yyyy-MM-dd)
    private SimpleDateFormat sDF;

    //Calendar Associated
    private ArrayList<HashMap<String, String>> calendarListData;
    private SimpleAdapter simpleAdapter;
    ArrayList<CalendarDay> dateArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Init();
    }

    private void Init(){
        //Initial View Object...
        materialCalendarView = findViewById(R.id.calendarView);
        calendarList = findViewById(R.id.calendarList);

        sDF = new SimpleDateFormat("yyyy-MM-dd");

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        dateArrayList = new ArrayList<>();
        calendarListData = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, calendarListData, R.layout.exercise_item, new String[] {"exerName", "setCount", "exerTime", "breakTime"},
                new int[] {R.id.textItemExerciseName, R.id.textItemSetCount, R.id.textItemExerciseTime, R.id.textItemBreakTime});

        calendarListData.clear();
        calendarList.setAdapter(simpleAdapter);
        //


        //캘린더뷰 클릭 시
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                calendarListData.clear();
                cursor = db.rawQuery(DBContract.SQL_LOAD + " WHERE " + DBContract.ROU_DATE + " = '" + sDF.format(date.getDate()) + "'", null);
                    while (cursor.moveToNext()){
                        HashMap<String, String> dateList = new HashMap<>();
                        //커서를 돌면서 그 날 수행한 루틴인지, 수행했다면 추가, 아니면 추가하지 않음
                        if(Integer.parseInt(cursor.getString(6)) == 1){
                            dateList.put("id", cursor.getString(0));
                            dateList.put("exerName", cursor.getString(1));
                            dateList.put("setCount", cursor.getString(2));
                            dateList.put("exerTime", cursor.getString(3));
                            dateList.put("breakTime", cursor.getString(4));
                            calendarListData.add(dateList);

                        }
//                    }
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });

        //캘린더뷰 마킹
        cursor = db.rawQuery(DBContract.SQL_LOAD, null);

        //루틴을 수행한 날만 마킹
        while (cursor.moveToNext()){
            if(Integer.parseInt(cursor.getString(6)) == 1)          //루틴 수행 조건 검사...
            {
                String[] tempDate = cursor.getString(5).split("-");
                dateArrayList.add(CalendarDay.from(
                        Integer.parseInt(tempDate[0]),
                        Integer.parseInt(tempDate[1]) - 1,
                        Integer.parseInt(tempDate[2])));
            }
        }
        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(this, R.color.maintitle_color_light), dateArrayList));
        //
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
        dbHelper.close();
        cursor.close();
    }
}