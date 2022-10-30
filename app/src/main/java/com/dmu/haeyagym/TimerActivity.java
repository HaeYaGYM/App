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
                        Toast.makeText(getApplicationContext(), "루틴 끝", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "루틴 중지", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };


        routineLauncher = registerForActivityResult(resultContract, resultCallback);

        //--- Swipe 제스처를 통해 리스트뷰 아이템 제거
        // 스와이프 리스너 객체 초기화
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(routineList, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                //리스트뷰 아이템 제거 코드
                for (int position : reverseSortedPositions){
                    db.delete(DBContract.TABLE_NAME, "ID=?", new String[] {routineListData.get(position).get("id")});
                    routineListData.remove(position);
                    simpleAdapter.notifyDataSetChanged();
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
        //루틴 리스트에 리스너 등록...
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

        //DB에 있는 데이터로 리스트뷰 매핑
        Cursor cursor = db.rawQuery(DBContract.SQL_LOAD + " WHERE " + DBContract.ROU_DATE + " = '" + sDF.format(new Date(System.currentTimeMillis())) + "'", null);

        while(cursor.moveToNext()) {
            HashMap<String, String> temp = new HashMap<>();

            Toast.makeText(getApplicationContext(),sDF.format(new Date(System.currentTimeMillis())) + ", " + cursor.getString(5), Toast.LENGTH_SHORT).show();
            if(!sDF.format(new Date(System.currentTimeMillis())).equals(cursor.getString(5)))
                continue;

            temp.put("id", cursor.getString(0));
            temp.put("exerName", cursor.getString(1));
            temp.put("setCount", cursor.getString(2));
            temp.put("exerTime", cursor.getString(3));
            temp.put("breakTime", cursor.getString(4));
            routineListData.add(temp);

        }

        //초기화시 아이디간 공백이 존재해도 가장 큰 아이디 값을 초기 값으로 가짐. Ex) id=5, id=9가 있을 경우 최초 maxRoutineListCount는 9를 가짐
        cursor = db.rawQuery("SELECT MAX(" + DBContract.ROU_ID + ") FROM " + DBContract.TABLE_NAME, null);
        if(cursor != null){
            while (cursor.moveToNext()){
                maxRoutineListCount = Integer.parseInt(cursor.getString(0));
            }
        }else{
            maxRoutineListCount = 0;
        }

//        maxRoutineListCount = Math.max(maxRoutineListCount, Integer.parseInt(routineListData.get(i).get("id")));
//        i++;

        cursor.close();
        simpleAdapter.notifyDataSetChanged();
        //

        //바텀 네비게이션 초기화
        bottomNav.setSelectedItemId(R.id.item_frag1);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.item_frag1:
                        Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_frag2:
                        intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_frag3:
                        Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_frag4:
                        Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        //

        //Initialize...
    }

    public void SetCountPlus(View view) {               //세트 카운트를 추가하는 메소드, 10개가 최대
        if(setCount < 10){
            setCount++;
        }
        textCount.setText(String.valueOf(setCount));
    }

    public void SetCountMinus(View view) {              //세트 카운트를 감소하는 메소드, 1개가 최소
        if(setCount > 1)
            setCount--;
        textCount.setText(String.valueOf(setCount));
    }

    public void TimerPlus(View view) {                  //운동 시간을 추가하는 메소드, 30초씩 증가하고 시간 상한선 X
        exerciseSec += 30;

        if(exerciseSec == 60){
            exerciseMin++;
            exerciseSec = 0;
        }
        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));

    }

    public void TimerMinus(View view) {                 //운동 시간을 감소하는 메소드, 최소 시간 30초
        if(exerciseMin <= 0 && exerciseSec == 30)
            return;

        exerciseSec -= 30;
        if(exerciseSec < 0){
            exerciseMin -= 1;
            exerciseSec = 30;
        }
        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
    }

    public void BreakPlus(View view) {                  //휴식 시간을 추가하는 메소드, 상한선 X
        breakSec += 30;

        if(breakSec == 60){
            breakMin++;
            breakSec = 0;
        }

        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void BreakMinus(View view) {                 //휴식 시간을 감소하는 메소드, 최소 시간 30초
        if(breakMin <= 0 && breakSec == 30)
            return;
        breakSec -= 30;
        if(breakSec < 0){
            breakMin -= 1;
            breakSec = 30;
        }
        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void TimerStart(View view){                  //루틴 시작 버튼 (StartRoutineActivity 로 인텐트 전송)

        if(simpleAdapter.getCount() == 0 && simpleAdapter.isEmpty()){
            Toast.makeText(getApplicationContext(), "루틴이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), StartRoutineActivity.class);
        //리스트 최댓값 전송 (루틴 매핑을 위해)
        intent.putExtra("listCount", routineList.getCount());
        //루틴 전송
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

    public void AddRoutine(View view){                  //루틴 추가 메소드
        HashMap<String, String> temp = new HashMap<>();
        maxRoutineListCount++;
        if(editTextRoutineName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "루틴 이름을 적어주세요...", Toast.LENGTH_SHORT).show();
            return;
        }

        //리스트뷰에 데이터 넣어줌
        temp.put("id", String.valueOf(maxRoutineListCount));
        temp.put("exerName", editTextRoutineName.getText().toString());
        temp.put("setCount", "세트 " + textCount.getText().toString());
        temp.put("exerTime", "운동 시간 " + String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
        temp.put("breakTime", "휴식 시간 " + String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
        routineListData.add(temp);
        //SQLite에 데이터 넣어줌
        ContentValues values = new ContentValues();
        values.put("ID", String.valueOf(maxRoutineListCount));
        values.put("NAME", editTextRoutineName.getText().toString());
        values.put("COUNT", "세트 " + textCount.getText().toString());
        values.put("EXER", "운동 시간 " + String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
        values.put("BREAK", "휴식 시간 " + String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
        String nowDate = sDF.format(new Date(System.currentTimeMillis()));
        values.put("DATE", nowDate);
        values.put("DIDROUTINE", false);
        //db 업데이트, 리스트뷰 업데이트
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