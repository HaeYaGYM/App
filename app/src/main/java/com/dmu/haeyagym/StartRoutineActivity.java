package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


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

    private boolean isConnected;
    private BluetoothAdapter btAdapter;
    private String bluetoothAddress;
    private BluetoothDevice bluetoothWatch;
    private BluetoothSocket bluetoothSocket;
    private InputStream bluetoothInput;
    private int readBufferPos;
    private ArrayList<Integer> beatList;
    private int maxRate;
    private double avgRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_routine);

        Init();

        min = listRemainExerMin.get(0);
        sec = listRemainExerSec.get(0);

        textRemainTime.setText(listRemainExerMin + " : " + listRemainExerSec);

        InitialBluetooth();
        byte[] buffer = new byte[1024];
        readBufferPos = 0;
        Handler handler = new Handler();
        actType = ActType.EXERCISE;         //기본 값을 EXERCISE로
        //타이머 코드
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //운동, 휴식 사이클
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
                                    if(isConnected && beatList.size() > 0){
                                        ResultBeatRate();
                                        timer.cancel();
                                        Intent intent = new Intent(getApplicationContext(), BeatResultActivity.class);
                                        intent.putExtra("max", maxRate);
                                        intent.putExtra("avg", avgRate);
                                        intent.putExtra("menu", "timer");
                                        try {
                                            bluetoothSocket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                    }else{
                                        setResult(RESULT_OK);
                                        timer.cancel();
                                        finish();
                                    }
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
                //

                //블루투스
                if(isConnected) {

                    if (bluetoothSocket.isConnected()) {
                        try {
                            bluetoothInput = bluetoothSocket.getInputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                            finish();
                        }
                    }
                    int byteAvailable = 0;
                    try {
                        byteAvailable = bluetoothInput.available();
                        if (byteAvailable > 0) {
                            byte[] bytes = new byte[byteAvailable];
                            bluetoothInput.read(bytes);
                            for (int i = 0; i < byteAvailable; i++) {
                                byte comp = bytes[i];
                                if (comp == '\n') {
                                    byte[] encodedByte = new byte[readBufferPos];
                                    System.arraycopy(buffer, 0, encodedByte, 0, encodedByte.length);
                                    String text = new String(encodedByte, "US-ASCII");
                                    readBufferPos = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Integer.parseInt(text.trim()) > 0){

                                                beatList.add(Integer.parseInt(text.trim()));
                                                Log.d("Test!", text);
                                            }
                                        }
                                    });
                                } else {
                                    buffer[readBufferPos++] = comp;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //

            }
        };

        timer.schedule(timerTask, 0, 100);
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

    private void GetBluetoothDevice(){
        //주변 기기 탐색, 가져옴
        if (btAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if(pairedDevices.size() > 0){
                for (BluetoothDevice device: pairedDevices ) {
                    Log.d("BlueTooth", device.getName());
                    if(device.getName().trim().equals("HC-06")){
                        bluetoothAddress = device.getAddress();
                        break;
                    }
                }
            }
        }
    }

    private void InitialBluetooth() {
        if(GetBluetoothAdapter()){
            if (btAdapter == null) {
                Toast.makeText(getApplicationContext(), "블루투스 페어링이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(enableBtIntent, 1);
            }

            GetBluetoothDevice();
            UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            if(!bluetoothAddress.isEmpty()){

                bluetoothWatch = btAdapter.getRemoteDevice(bluetoothAddress);
                try {
                    bluetoothSocket = bluetoothWatch.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bluetoothWatch.getBondState() == BluetoothDevice.BOND_BONDED) {
                    beatList = new ArrayList<>();
                    isConnected = true;
                }else{
                    isConnected = false;
                }
            }else{
                isConnected = false;
            }

        }else{
            isConnected = false;
        }
    }
    private void ResultBeatRate(){
        if(beatList.isEmpty())
            return;

        int sum = 0;
        maxRate = beatList.get(0);
        for (int item: beatList) {
            sum += item;
            if(maxRate < item)
                maxRate = item;
        }
        avgRate = (double) (sum / beatList.size());
        beatList.clear();

    }

    private boolean GetBluetoothAdapter(){
        String[] permissionList = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        };
        int bluetoothPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionList, 1);
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null) {
            return true;
        }
        else {
            return false;
        }
    }
}