package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.List;
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



    }

    void Init(){
        //Initial View Object...
        bottomNav = findViewById(R.id.heartBeatBottomNav);
        textBeatRate = findViewById(R.id.textBeatRate);
        textCheckingStatus = findViewById(R.id.textCheckingStatus);
        btnStartingBeatRate = findViewById(R.id.btnStartingBeatRate);
        //



        //바텀 네비게이션
        bottomNav.setSelectedItemId(R.id.item_frag3);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
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

        //블루투스 권한 사용
        int bluetoothPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if(bluetoothPermission == PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissionList, 1);
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //

    }

    public void StartingCheckBeatRate(View view) {

        //주변 기기 탐색, 가져옴
        if(btAdapter.isEnabled()){
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if(pairedDevices.size() > 0){
                for (BluetoothDevice device: pairedDevices ) {
                    Log.d("BlueTooth", device.getName());
                    if(device.getName().trim().equals("HC-06")){
                        textCheckingStatus.setText("찾음");
                        bluetoothAddress = device.getAddress();
                        break;
                    }else{
                        textCheckingStatus.setText("연동 실패");
                    }
                }
            }
        }
        //

        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        bluetoothWatch = btAdapter.getRemoteDevice(bluetoothAddress);
        try {
            bluetoothSocket = bluetoothWatch.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        getBluetoothBuffer();
    }

    public void getBluetoothBuffer(){

        byte[] buffer = new byte[1024];
        int byteAvailable = 0;

        if(bluetoothSocket.isConnected()){
            try {
                bluetoothOutput = bluetoothSocket.getOutputStream();
                bluetoothInput = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
        while (true){
            try {
                byteAvailable = bluetoothInput.read(buffer);
                String strBuf = new String(buffer, 0, byteAvailable);
                Log.d("Byte", strBuf);
                SystemClock.sleep(1);
            }catch (IOException e){ e.printStackTrace();}
        }
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
