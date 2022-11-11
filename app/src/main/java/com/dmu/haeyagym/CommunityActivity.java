package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
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

        listViewGroupData = GetGroupList();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listViewGroupData.size(); i++){
                    for (String str : listViewGroupData.get(i).values()){
                        test.setText(str);
                    }
                }
            }
        });
        thread.start();
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

    private ArrayList<HashMap<String, String>> GetGroupList() {
        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference("board");


        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        dbRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        HashMap<String, String> datas = (HashMap<String, String>)snapshot.getValue();
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("imagePictogram", datas.get("Category"));
                        temp.put("textTitle", datas.get("Title"));
                        temp.put("textGroupName", datas.get("Category"));
                        Log.d("Test",temp.toString());
                        listViewGroupData.add(temp);
                    }
                    simpleAdapter.notifyDataSetChanged();
//                    for (int i = 0; i < temp.size(); i++){
//                        for (String str : temp.get(i).values()){
//                            Log.d("Taew", str);
//                        }
//                    }
                }
            }
        });

        return temp;
    }

    public void AddGroup(View view) {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivity(intent);
    }
}
