package com.example.haeyagym;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class TimerActivity extends AppCompatActivity {


    private int setCount = 5;
    private int exerciseMin = 3, exerciseSec = 0;
    private int breakMin = 0, breakSec = 30;

    private int routineCount;           //루틴 인덱스값
    private TextView textCount;
    private TextView textExer;
    private TextView textBreak;
    private ListView routineList;
    private ArrayList<HashMap<String, String>> routineListData;

    private SimpleAdapter simpleAdapter;

    private ActivityResultContract<Intent, ActivityResult> resultContract;
    private ActivityResultCallback<ActivityResult> resultCallback;
    private ActivityResultLauncher<Intent> routineLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //============================================================================== 초기화 부분
        textCount = findViewById(R.id.textSetCount);
        textExer = findViewById(R.id.textTimerCount);
        textBreak = findViewById(R.id.textBreakCount);
        routineList = findViewById(R.id.routineList);
        routineListData = new ArrayList<>();


        routineListData.clear();
        simpleAdapter = new SimpleAdapter(this, routineListData, R.layout.exercise_item, new String[] {"exerName", "setCount", "exerTime", "breakTime"},
                new int[] {R.id.textItemExerciseName, R.id.textItemSetCount, R.id.textItemExerciseTime, R.id.textItemBreakTime});
        routineList.setAdapter(simpleAdapter);

        resultContract = new ActivityResultContracts.StartActivityForResult();
        resultCallback = new ActivityResultCallback<ActivityResult>() {             //콜백객체 초기화, 리절트 이벤트 발생 시 루틴 추가해줌
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("exerName", intent.getStringExtra("exerName"));
                    temp.put("setCount", intent.getStringExtra("setCount"));
                    temp.put("exerTime", intent.getStringExtra("exerTime"));
                    temp.put("breakTime", intent.getStringExtra("breakTime"));
                    routineListData.add(temp);
                    simpleAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getApplicationContext(), "루틴 추가 취소", Toast.LENGTH_SHORT).show();
            }
        };
        routineLauncher = registerForActivityResult(resultContract, resultCallback);
        //==============================================================================


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

    public void TimerStart(View view){
    }

    public void AddRoutine(View view){
        Intent intent = new Intent(getApplicationContext(), AddRoutineActivity.class);
        routineLauncher.launch(intent);
    }
}