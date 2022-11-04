package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

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
            Manifest.permission.BLUETOOTH_ADMIN
    };
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
        ActivityCompat.requestPermissions(this, permissionList, 1);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //
    }
}
