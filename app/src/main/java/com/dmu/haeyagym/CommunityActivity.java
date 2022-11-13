package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> listViewGroupData;

    private ListView listViewGroup;
    private SimpleAdapter simpleAdapter;
    private TextView test;

    private BottomNavigationView bottomNav;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbRef;
    private Thread listViewRefresh;

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
        test = findViewById(R.id.textSub);

        listViewGroupData = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, listViewGroupData, R.layout.group_item,
                new String[] {"textTitle", "textCategory"},
                new int[] {R.id.textTitle, R.id.textCategory});
        listViewGroupData.clear();
        listViewGroup.setAdapter(simpleAdapter);
        //

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

        //리스트 가져오기
        GetList();
        //
        listViewRefresh = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(listViewGroupData.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                simpleAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    try {
                        Thread.sleep(1000);
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        });
        listViewRefresh.start();
        //
    }

    private void GetList(){
        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference("board");

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot result = task.getResult();
                    for (DataSnapshot data : result.getChildren()){
                        HashMap<String, String> temp = (HashMap<String, String>)data.getValue();
                        temp.put("textTitle", temp.get("Title"));
                        temp.put("textCategory", temp.get("Category"));
                        listViewGroupData.add(temp);
                    }
                }
            }
        });
    }

    public void AddGroup(View view) {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivity(intent);
    }
}
