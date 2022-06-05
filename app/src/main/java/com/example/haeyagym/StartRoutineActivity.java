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

    int remainSetcount;
    int remainExerMin, remainExerSec;
    int remainBreakMin, remainBreakSec;
    int min = 0, sec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_routine);
        //Initialize...
        timer = new Timer();
        textNowAct = findViewById(R.id.textNowAct);
        textRemainTime = findViewById(R.id.textRemainTime);
        textRemainSetCount = findViewById(R.id.textRemainSetCount);
        Intent intentR = getIntent();
        //Initialize...

        remainSetcount = intentR.getIntExtra("setCount", 3);
        remainExerMin = intentR.getIntExtra("exerMin", 0);
        remainExerSec = intentR.getIntExtra("exerSec", 30);
        remainBreakMin = intentR.getIntExtra("breakMin", 0);
        remainBreakSec = intentR.getIntExtra("breakSec", 30);

        min = remainExerMin;
        sec = remainExerSec;

        textRemainTime.setText(remainExerMin + " : " + remainExerSec);



        actType = ActType.EXERCISE;         //기본 값을 EXERCISE로
        //타이머 코드
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                sec--;
                switch (actType) {
                    case EXERCISE:                  //운동 중일때..
                        textRemainSetCount.setText("운동 중");
                        if (sec < 0) {
                            if (remainExerMin == 0 && remainExerSec == 30) {
                                sec = 30;
                            }
                            else{
                                min -= 1;
                                sec = 59;
                            }
                        }
                        if(min == 0 && sec <= 0){
                            actType = ActType.BREAK;
                            min = remainBreakMin;
                            sec = remainBreakSec;
                        }
                        break;
                    case BREAK:                     //휴식 중일때..
                        textRemainSetCount.setText("휴식 중");
                        if (sec < 0) {
                            if (remainBreakMin == 0 && remainBreakSec == 30) {
                                sec = 30;
                            }
                            else{
                                min -= 1;
                                sec = 59;
                            }
                        }
                        if(min == 0 && sec <= 0){
                            actType = ActType.EXERCISE;
                            min = remainExerMin;
                            sec = remainExerSec;
                            remainSetcount--;
                            if(remainSetcount == 0) {
                                setResult(TimerActivity.RESULT_START);
                                finish();
                            }
                        }
                        break;
                }
                //텍스트 설정, 항상 초 단위는 두 자리 수로
                textRemainTime.setText(String.valueOf(min) + " : " + ((sec >= 10) ? String.valueOf(sec) : "0" + String.valueOf(sec)));
            }
        };

        timer.schedule(timerTask, 0, 100);
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