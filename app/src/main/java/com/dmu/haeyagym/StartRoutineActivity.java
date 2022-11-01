package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.wave.MultiWaveHeader;

import java.util.ArrayList;
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

    private ImageView background;
    private TextView textSetName;
    private TextView textRemainTime;
    private TextView textRemainSetCount;
    private MultiWaveHeader wave;
    private Timer timer;

    private ArrayList<String> listExerName;
    private ArrayList<Integer> listID;
    private ArrayList<Integer> listRemainSetcount;
    private ArrayList<Integer> listRemainExerMin, listRemainExerSec;
    private ArrayList<Integer> listRemainBreakMin, listRemainBreakSec;

    private AnimationSet animation;
    private Animation fadeIn, fadeOut;

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private int listCount;
    int min = 0, sec = 0, idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_routine);

        Init();

        min = listRemainExerMin.get(0);
        sec = listRemainExerSec.get(0);

        textRemainTime.setText(listRemainExerMin + " : " + listRemainExerSec);


        actType = ActType.EXERCISE;         //기본 값을 EXERCISE로
        //타이머 코드
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                textSetName.setText(listExerName.get(idx));
                sec--;
                switch (actType) {
                    case EXERCISE:                  //운동 중일때..
                        textRemainSetCount.setText("운동 중");
                        textRemainSetCount.setTextColor(getColor(R.color.maintitle_color));
                        if (sec < 0) {
                            if (listRemainExerMin.get(idx) == 0 && listRemainExerSec.get(idx) == 30) {
                                sec = 30;
                            }
                            else{
                                min -= 1;
                                sec = 59;
                            }
                        }
                        if(min == 0 && sec <= 0){
                            background.startAnimation(animation.getAnimations().get(0));
                            actType = ActType.BREAK;
                            min = listRemainBreakMin.get(idx);
                            sec = listRemainBreakSec.get(idx);
                        }
                        break;
                    case BREAK:                     //휴식 중일때..
                        textRemainSetCount.setText("휴식 중");
                        textRemainSetCount.setTextColor(getColor(R.color.white));
                        if (sec < 0) {
                            if (listRemainBreakMin.get(idx) == 0 && listRemainBreakSec.get(idx) == 30) {
                                sec = 30;
                            }
                            else{
                                min -= 1;
                                sec = 59;
                            }
                        }
                        if(min == 0 && sec <= 0){
                            background.startAnimation(animation.getAnimations().get(1));
                            listRemainSetcount.set(idx, listRemainSetcount.get(idx) - 1);
                            if(listRemainSetcount.get(idx) == 0) {
                                String strSQL = "UPDATE " + DBContract.TABLE_NAME + " SET " + DBContract.ROU_DID + " = " + true + " WHERE " + DBContract.ROU_ID + " = " + listID.get(idx).toString();
                                db = dbHelper.getWritableDatabase();
                                db.execSQL(strSQL);
                                idx++;
                                if(idx == listCount){
                                    setResult(RESULT_OK);
                                    timer.cancel();
                                    finish();
                                }
                            }
                            if(idx < listCount) {
                                min = listRemainExerMin.get(idx);
                                sec = listRemainExerSec.get(idx);
                                actType = ActType.EXERCISE;
                            }
                        }
                        break;
                }
                //텍스트 설정, 항상 초 단위는 두 자리 수로
                textRemainTime.setText(String.valueOf(min) + " : " + ((sec >= 10) ? String.valueOf(sec) : "0" + String.valueOf(sec)));
            }
        };

        timer.schedule(timerTask, 0, 10);
    }

    private void Init(){
        //Initialize View Objects...
        timer = new Timer();
        background = findViewById(R.id.background);
        textSetName = findViewById(R.id.textSetName);
        textRemainTime = findViewById(R.id.textRemainTime);
        textRemainSetCount = findViewById(R.id.textRemainSetCount);
        wave = findViewById(R.id.wave);
        //Initialize View Objects...


        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        listID = new ArrayList<>();
        listExerName = new ArrayList<>();
        listRemainSetcount = new ArrayList<>();
        listRemainExerMin = new ArrayList<>();
        listRemainExerSec = new ArrayList<>();
        listRemainBreakMin = new ArrayList<>();
        listRemainBreakSec = new ArrayList<>();

        Intent intentR = getIntent();
        listCount = intentR.getIntExtra("listCount", 0);

        for (int i = 0; i < listCount; i++){

            listID.add(Integer.parseInt(intentR.getStringExtra("id" + i)));
            listExerName.add(intentR.getStringExtra("exerName" + i));
            listRemainSetcount.add(Integer.parseInt(intentR.getStringExtra("setCount" + i).substring(3)));

            String exerTime = intentR.getStringExtra("exerTime" + i);
            //문자열 자르기(Ex 운동 시간 1:00 -> 1:00)
            exerTime = exerTime.substring(6);
            listRemainExerMin.add(Integer.parseInt(exerTime.split(":")[0].trim()));
            listRemainExerSec.add(Integer.parseInt(exerTime.split(":")[1].trim()));
            String breakTime = intentR.getStringExtra("breakTime0");
            //문자열 자르기(Ex 휴식 시간 1:00 -> 1:00)
            breakTime = breakTime.substring(6);
            listRemainBreakMin.add(Integer.parseInt(breakTime.split(":")[0].trim()));
            listRemainBreakSec.add(Integer.parseInt(breakTime.split(":")[1].trim()));
        }

        //애니메이션 초기화 (FadeIn = 0, FadeOut = 1)
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                background.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                background.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                background.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                background.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        // 시작할 때 FadeOut
        background.startAnimation(animation.getAnimations().get(1));
    }
}