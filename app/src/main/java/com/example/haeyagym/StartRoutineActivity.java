package com.example.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class StartRoutineActivity extends AppCompatActivity {

    //ActType 열거형 객체로 지금이 운동 시간인지 휴식 시간인지 판단
    public enum ActType {
        EXERCISE,
        BREAK,
        NONE,
    }

    ActType actType;

    TextView textNowAct;
    TextView textRemainTime;
    TextView textRemainSetCount;
    Timer timer;

    int remainExerMin, remainExerSec;
    int remainBreakMin, remainBreakSec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_routine);
        //Initialize...
        timer = new Timer();
        textNowAct = findViewById(R.id.textNowAct);
        textRemainTime = findViewById(R.id.textRemainTime);
        textRemainSetCount = findViewById(R.id.textRemainSetCount);
        Intent intent = getIntent();
        //Initialize...

        remainExerMin = intent.getIntExtra("exerMin", 0);
        remainExerSec = intent.getIntExtra("exerSec", 30);
        remainBreakMin = intent.getIntExtra("breakMin", 0);
        remainBreakSec = intent.getIntExtra("breakSec", 30);

        textRemainTime.setText(remainExerMin + " : " + remainExerSec);


        actType = ActType.EXERCISE;         //기본 값을 EXERCISE로
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int min = 0, sec = 0;
                switch (actType) {
                    case EXERCISE:
                        if (min <= 0) {
                            remainExerMin -= 1;
                            remainExerSec = 59;
                        }
                        textRemainTime.setText(String.valueOf(remainExerMin) + " : " + String.valueOf(remainExerSec));
                        break;
                    case BREAK:
                        remainBreakSec--;
                        if (remainExerSec <= 0) {
                            remainExerMin -= 1;
                            remainExerSec = 59;
                        }
                        textRemainTime.setText(String.valueOf(remainExerMin) + " : " + String.valueOf(remainExerSec));
                        break;
                }
                min--;
            }
        };

        timer.schedule(timerTask, 0, 1000);
        switch (actType) {
            case EXERCISE:                          //운동 중...
//                textNowAct.setTextColor(Color.parseColor(String.valueOf(R.color.maintitle_color)));
//                textRemainTime.setTextColor(Color.parseColor(String.valueOf(R.color.maintitle_color)));
//                textRemainSetCount.setTextColor(Color.parseColor(String.valueOf(R.color.maintitle_color)));


                break;
            case BREAK:                             //휴식 중...
//                textNowAct.setTextColor(Color.parseColor(String.valueOf(R.color.white)));
//                textRemainTime.setTextColor(Color.parseColor(String.valueOf(R.color.white)));
//                textRemainSetCount.setTextColor(Color.parseColor(String.valueOf(R.color.white)));
                break;
            default:
                break;
        }
    }
}