package com.dmu.haeyagym;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TimerActivity extends AppCompatActivity {

    private int setCount = 5;
    private int exerciseMin = 3, exerciseSec = 0;
    private int breakMin = 0, breakSec = 30;

    private int maxRoutineListCount;

    private SimpleDateFormat sDF;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private TextView textCount;
    private TextView textExer;
    private TextView textBreak;
    private EditText editTextRoutineName;
    private ListView routineList;
    private ArrayList<HashMap<String, String>> routineListData;

    private SimpleAdapter simpleAdapter;

    private ActivityResultContract<Intent, ActivityResult> resultContract;
    private ActivityResultCallback<ActivityResult> resultCallback;
    private ActivityResultLauncher<Intent> routineLauncher;

    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Init();

        resultContract = new ActivityResultContracts.StartActivityForResult();
        resultCallback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                switch (result.getResultCode()){
                    case RESULT_OK:
                        simpleAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "?????? ???", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        simpleAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };


        routineLauncher = registerForActivityResult(resultContract, resultCallback);

        //--- Swipe ???????????? ?????? ???????????? ????????? ??????
        // ???????????? ????????? ?????? ?????????
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(routineList, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                //???????????? ????????? ?????? ??????
                for (int position : reverseSortedPositions){
                    db.delete(DBContract.TABLE_NAME, "ID=?", new String[] {routineListData.get(position).get("id")});
                    routineListData.remove(position);
                    simpleAdapter.notifyDataSetChanged();
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
        //?????? ???????????? ????????? ??????...
        routineList.setOnTouchListener(touchListener);
        routineList.setOnScrollListener(touchListener.makeScrollListener());
        //---
    }

    private void Init() {
        //Initialize View Objects...
        textCount = findViewById(R.id.textSetCount);
        textExer = findViewById(R.id.textTimerCount);
        textBreak = findViewById(R.id.textBreakCount);
        routineList = findViewById(R.id.routineList);
        editTextRoutineName = findViewById(R.id.editTextRoutineName);
        bottomNav = findViewById(R.id.timerBottomNav);

        //Initialize View Objects...

        //Initialize DB, ListView, Adapter
        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        dbHelper.onCreate(db);

        routineListData = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, routineListData, R.layout.exercise_item, new String[] {"exerName", "setCount", "exerTime", "breakTime"},
                new int[] {R.id.textItemExerciseName, R.id.textItemSetCount, R.id.textItemExerciseTime, R.id.textItemBreakTime});

        routineListData.clear();
        routineList.setAdapter(simpleAdapter);

        sDF = new SimpleDateFormat("yyyy-MM-dd");

        //Initialize DB, ListView, Adapter

        //DB??? ?????? ???????????? ???????????? ??????
        Cursor cursor = db.rawQuery(DBContract.SQL_LOAD + " WHERE " + DBContract.ROU_DATE + " = '" + sDF.format(new Date(System.currentTimeMillis())) + "'", null);

        while(cursor.moveToNext()) {
            HashMap<String, String> temp = new HashMap<>();

            //Toast.makeText(getApplicationContext(),sDF.format(new Date(System.currentTimeMillis())) + ", " + cursor.getString(5), Toast.LENGTH_SHORT).show();
            if(!sDF.format(new Date(System.currentTimeMillis())).equals(cursor.getString(5))
                    || Integer.parseInt(cursor.getString(6)) == 1)
                continue;

            temp.put("id", cursor.getString(0));
            temp.put("exerName", cursor.getString(1));
            temp.put("setCount", cursor.getString(2));
            temp.put("exerTime", cursor.getString(3));
            temp.put("breakTime", cursor.getString(4));
            routineListData.add(temp);

        }

        //???????????? ???????????? ????????? ???????????? ?????? ??? ????????? ?????? ?????? ????????? ??????. Ex) id=5, id=9??? ?????? ?????? ?????? maxRoutineListCount??? 9??? ??????
        cursor = db.rawQuery("SELECT " + DBContract.ROU_ID + " FROM " + DBContract.TABLE_NAME, null);
        Log.d("error", String.valueOf(cursor.getCount()));
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                maxRoutineListCount = Math.max(maxRoutineListCount, Integer.parseInt(cursor.getString(0)));
            }
        }else{
            maxRoutineListCount = 0;
        }

//        maxRoutineListCount = Math.max(maxRoutineListCount, Integer.parseInt(routineListData.get(i).get("id")));
//        i++;

        cursor.close();
        simpleAdapter.notifyDataSetChanged();
        //

        //?????? ??????????????? ?????????
        bottomNav.setSelectedItemId(R.id.item_frag3);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.item_frag1:
                        intent = new Intent(getApplicationContext(), CommunityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag2:
                        intent = new Intent(getApplicationContext(), CheckHeartbeatActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag3:
                        return true;
                    case R.id.item_frag4:
                        intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        //

        //Initialize...
    }

    public void SetCountPlus(View view) {               //?????? ???????????? ???????????? ?????????, 10?????? ??????
        if(setCount < 10){
            setCount++;
        }
        textCount.setText(String.valueOf(setCount));
    }

    public void SetCountMinus(View view) {              //?????? ???????????? ???????????? ?????????, 1?????? ??????
        if(setCount > 1)
            setCount--;
        textCount.setText(String.valueOf(setCount));
    }

    public void TimerPlus(View view) {                  //?????? ????????? ???????????? ?????????, 30?????? ???????????? ?????? ????????? X
        exerciseSec += 30;

        if(exerciseSec == 60){
            exerciseMin++;
            exerciseSec = 0;
        }
        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));

    }

    public void TimerMinus(View view) {                 //?????? ????????? ???????????? ?????????, ?????? ?????? 30???
        if(exerciseMin <= 0 && exerciseSec == 30)
            return;

        exerciseSec -= 30;
        if(exerciseSec < 0){
            exerciseMin -= 1;
            exerciseSec = 30;
        }
        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
    }

    public void BreakPlus(View view) {                  //?????? ????????? ???????????? ?????????, ????????? X
        breakSec += 30;

        if(breakSec == 60){
            breakMin++;
            breakSec = 0;
        }

        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void BreakMinus(View view) {                 //?????? ????????? ???????????? ?????????, ?????? ?????? 30???
        if(breakMin <= 0 && breakSec == 30)
            return;
        breakSec -= 30;
        if(breakSec < 0){
            breakMin -= 1;
            breakSec = 30;
        }
        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void TimerStart(View view){                  //?????? ?????? ?????? (StartRoutineActivity ??? ????????? ??????)

        if(simpleAdapter.getCount() == 0 && simpleAdapter.isEmpty()){
            Toast.makeText(getApplicationContext(), "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), StartRoutineActivity.class);
        //????????? ????????? ?????? (?????? ????????? ??????)
        intent.putExtra("listCount", routineList.getCount());
        //?????? ??????
        for (int i = 0; i < routineList.getCount(); i++){
            HashMap<String, String> temp = (HashMap<String, String>) simpleAdapter.getItem(i);
            intent.putExtra(String.valueOf("id" + i), temp.get("id"));
            intent.putExtra(String.valueOf("exerName" + i), temp.get("exerName"));
            intent.putExtra(String.valueOf("setCount" + i), temp.get("setCount"));
            intent.putExtra(String.valueOf("exerTime" + i), temp.get("exerTime"));
            intent.putExtra(String.valueOf("breakTime" + i), temp.get("breakTime"));
        }
        routineLauncher.launch(intent);
    }

    public void AddRoutine(View view){                  //?????? ?????? ?????????
        HashMap<String, String> temp = new HashMap<>();
        maxRoutineListCount++;
        if(editTextRoutineName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "?????? ????????? ???????????????...", Toast.LENGTH_SHORT).show();
            return;
        }

        //??????????????? ????????? ?????????
        temp.put("id", String.valueOf(maxRoutineListCount));
        temp.put("exerName", editTextRoutineName.getText().toString());
        temp.put("setCount", "?????? " + textCount.getText().toString());
        temp.put("exerTime", "?????? ?????? " + String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
        temp.put("breakTime", "?????? ?????? " + String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
        routineListData.add(temp);
        //SQLite??? ????????? ?????????
        ContentValues values = new ContentValues();
        values.put("ID", String.valueOf(maxRoutineListCount));
        values.put("NAME", editTextRoutineName.getText().toString());
        values.put("COUNT", "?????? " + textCount.getText().toString());
        values.put("EXER", "?????? ?????? " + String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
        values.put("BREAK", "?????? ?????? " + String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
        String nowDate = sDF.format(new Date(System.currentTimeMillis()));
        values.put("DATE", nowDate);
        values.put("DIDROUTINE", false);
        //db ????????????, ???????????? ????????????
        db = dbHelper.getWritableDatabase();
        db.insert(DBContract.TABLE_NAME, null, values);
        simpleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
        dbHelper.close();
    }
}