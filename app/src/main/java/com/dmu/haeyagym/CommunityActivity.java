package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> listViewGroupData;

    private ListView listViewGroup;
    private SimpleAdapter simpleAdapter;

    private BottomNavigationView bottomNav;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbRef;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        Init();
    }

    private void Init() {
        //Initial View Objects...
        listViewGroup = findViewById(R.id.listViewGroup);
        bottomNav = findViewById(R.id.communityBottomNav);

        listViewGroupData = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, listViewGroupData, R.layout.group_item,
                new String[] {"imagePictogram", "textRange", "textGroupName", "buttonEnterGroup"},
                new int[] {R.id.imagePictogram, R.id.textRange, R.id.textGroupName, R.id.buttonEnterGroup});

        listViewGroupData.clear();
        listViewGroupData = GetGroupList();
        //
        
        listViewGroup.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();

        //바텀 네비게이션
        bottomNav.setSelectedItemId(R.id.item_frag4);
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
    }

    private ArrayList<HashMap<String, String>> GetGroupList(){
        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference("brdid");
        
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        return temp;
    }
}