package com.example.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import javax.xml.transform.Result;

public class AddRoutineActivity extends AppCompatActivity {

    int setCount = 5;
    int exerciseMin = 3, exerciseSec = 0;
    int breakMin = 0, breakSec = 30;

    TextView textCount;
    TextView textExer;
    TextView textBreak;
    EditText routineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);

        routineName = findViewById(R.id.routineName);
        textCount = findViewById(R.id.textSetCount);
        textExer = findViewById(R.id.textTimerCount);
        textBreak = findViewById(R.id.textBreakCount);



    }

    public void setCountPlus(View view) {
        if (setCount < 10) {
            setCount++;
        }
        textCount.setText(String.valueOf(setCount));
    }

    public void setCountMinus(View view) {
        if (setCount > 1)
            setCount--;
        textCount.setText(String.valueOf(setCount));
    }

    public void timerPlus(View view) {
        exerciseSec += 30;

        if (exerciseSec == 60) {
            exerciseMin++;
            exerciseSec = 0;
        }

        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
    }

    public void timerMinus(View view) {
        if (exerciseMin <= 0 && exerciseSec == 30)
            return;

        exerciseSec -= 30;
        if (exerciseSec < 0) {
            exerciseMin -= 1;
            exerciseSec = 30;
        }
        textExer.setText(String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
    }

    public void breakPlus(View view) {
        breakSec += 30;

        if (breakSec == 60) {
            breakMin++;
            breakSec = 0;
        }

        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void breakMinus(View view) {
        if (breakMin <= 0 && breakSec == 30)
            return;

        breakSec -= 30;
        if (breakSec < 0) {
            breakMin -= 1;
            breakSec = 30;
        }
        textBreak.setText(String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
    }

    public void SaveRoutine(View view) {

        Intent intent = new Intent();
        intent.putExtra("exerName", routineName.getText().toString());
        intent.putExtra("setCount", "세트 " + String.valueOf(setCount));
        intent.putExtra("exerTime", "운동 시간 " + String.valueOf(exerciseMin) + ':' + (exerciseSec == 0 ? "00" : "30"));
        intent.putExtra("breakTime", "휴식 시간 " + String.valueOf(breakMin) + ':' + (breakSec == 0 ? "00" : "30"));
        setResult(TimerActivity.RESULT_ADD, intent);
        finish();
    }
}