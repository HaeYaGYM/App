package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class CheckHeartbeatActivity extends AppCompatActivity {

    //Bottom Navigation
    private BottomNavigationView bottomNav;
    //View Objects
    private TextView textCheckingStatus;
    private TextView textBeatRate;
    private BluetoothAdapter btAdapter;
    private Button btnStartingBeatRate;
    //

    //Bluetooth Connection Associated
    private final static int REQUEST_ENABLE_BT = 1;
    private String[] permissionList = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private String bluetoothAddress;
    private int readBufferPos;
    private ArrayList<Integer> beatList;
    private int maxRate;
    private double avgRate;
    private boolean isChecking;

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothWatch;
    private InputStream bluetoothInput;
    private OutputStream bluetoothOutput;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_heartbeat);

        Init();


        //블루투스 권한 사용
        GetBluetoothAdapter();

        if (btAdapter == null) {
            textCheckingStatus.setText("페어링 실패");
            textCheckingStatus.setTextColor(getResources().getColor(R.color.gray));
            Toast.makeText(getApplicationContext(), "블루투스 페어링이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        GetBluetoothDevice();
        BluetoothConnect();
        //
    }

    void Init() {
        //Initial View Object...
        bottomNav = findViewById(R.id.heartBeatBottomNav);
        textBeatRate = findViewById(R.id.textBeatRate);
        textCheckingStatus = findViewById(R.id.textCheckingStatus);
        btnStartingBeatRate = findViewById(R.id.btnStartingBeatRate);
        //

        isChecking = false;

        //바텀 네비게이션
        bottomNav.setSelectedItemId(R.id.item_frag3);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    if (bluetoothSocket != null)
                        bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.item_frag1:
                        intent = new Intent(getApplicationContext(), TimerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag2:
                        intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag3:
                        intent = new Intent(getApplicationContext(), CheckHeartbeatActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag4:
                        intent = new Intent(getApplicationContext(), CommunityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        //

    }

    private void GetBluetoothAdapter() {
        int bluetoothPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionList, 1);
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void StartingCheckBeatRate(View view) {
        GetBluetoothAdapter();

        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스 페어링이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        GetBluetoothDevice();
        if(isChecking == false){
            BluetoothConnect();
        }
        else {
            try {
                BluetoothDisconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void BluetoothConnect() {
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothWatch = btAdapter.getRemoteDevice(bluetoothAddress);
        try {
            bluetoothSocket = bluetoothWatch.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bluetoothWatch.getBondState() == BluetoothDevice.BOND_BONDED) {
            beatList = new ArrayList<>();
            GetBluetoothBuffer();
        }
    }

    private void BluetoothDisconnect() throws IOException {
        bluetoothSocket.close();
        ResultBeatRate();
    }

    public void GetBluetoothBuffer(){
        byte[] buffer = new byte[1024];
        readBufferPos = 0;
        Handler handler = new Handler();
        if(bluetoothSocket.isConnected()){
            textCheckingStatus.setText("측정 중...");
            try {
                bluetoothInput = bluetoothSocket.getInputStream();
                bluetoothOutput = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
        else{
            textCheckingStatus.setText("연결 실패");
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    int byteAvailable = 0;
                    try {
                        byteAvailable = bluetoothInput.available();
                        if(byteAvailable > 0){
                            byte[] bytes = new byte[byteAvailable];
                            bluetoothInput.read(bytes);
                            for (int i = 0; i < byteAvailable; i++){
                                byte comp = bytes[i];
                                if(comp == '\n'){
                                    byte[] encodedByte = new byte[readBufferPos];
                                    System.arraycopy(buffer, 0, encodedByte, 0, encodedByte.length);
                                    String text = new String(encodedByte, "US-ASCII");
                                    readBufferPos = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            textBeatRate.setText(text + "BPM");
                                            beatList.add(Integer.parseInt(text.trim()));
                                        }
                                    });
                                }else{
                                    buffer[readBufferPos++] = comp;
                                }
                            }
                        }
                    }catch (IOException e){e.printStackTrace();}
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();
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
                        textCheckingStatus.setTextColor(getResources().getColor(R.color.maintitle_color));
                        textCheckingStatus.setText("찾음");
                        bluetoothAddress = device.getAddress();
                        isChecking = !isChecking;
                        break;
                    }else{
                        textCheckingStatus.setText("연동 실패");
                    }
                }
            }
        }
        //
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
        avgRate = sum / beatList.size();
        beatList.clear();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        try {
            bluetoothSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bluetoothInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bluetoothOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
