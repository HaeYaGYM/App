package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> listViewGroupData;

    private ListView listViewGroup;
    private SimpleAdapter simpleAdapter;
    private Spinner spinnerRegion;
    private String region;

    private BottomNavigationView bottomNav;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbRef;
    private Thread listViewRefresh;

    private TextView communityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        Init();

        String content = communityTitle.getText().toString();
        SpannableString spannableString = new SpannableString(content);

        String word = "날씨 좋은 날,";
        int start = content.indexOf(word);
        int end = start + word.length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#2EA7E0")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.0f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        communityTitle.setText(spannableString);

        listViewGroup.setOnItemClickListener((parent, view, position, id) -> {
            Button button = view.findViewById(R.id.btnEnterGroup);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                intent.putExtra("title", listViewGroupData.get(position).get("Title"));
                intent.putExtra("description", listViewGroupData.get(position).get("Description"));
                intent.putExtra("date", listViewGroupData.get(position).get("Date"));
                intent.putExtra("uid", listViewGroupData.get(position).get("UID"));
                intent.putExtra("category", listViewGroupData.get(position).get("Category"));
                startActivity(intent);
            });
        });
    }

    private void Init() {
        //Initial View Objects...
        listViewGroup = findViewById(R.id.listViewGroup);
        bottomNav = findViewById(R.id.communityBottomNav);
        spinnerRegion = findViewById(R.id.communityRegion);
        communityTitle = findViewById(R.id.textSub);

        listViewGroupData = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, listViewGroupData, R.layout.group_item,
                new String[] {"textTitle", "textCategory"},
                new int[] {R.id.textTitle, R.id.textCategory});
        listViewGroupData.clear();
        listViewGroup.setAdapter(simpleAdapter);
        //

        //바텀 네비게이션
        bottomNav.setSelectedItemId(R.id.item_frag1);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.item_frag1:
                        intent = new Intent(getApplicationContext(), CommunityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag2:
                        intent = new Intent(getApplicationContext(), CheckHeartbeatActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag3:
                        intent = new Intent(getApplicationContext(), TimerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.item_frag4:
                        intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        //

        //스피너 초기화
        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listViewGroupData.clear();
                region = parent.getItemAtPosition(position).toString();
                GetList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                region = "서울";
            }
        });
        //

        //리스트 가져오기
        listViewRefresh = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            simpleAdapter.notifyDataSetChanged();
                        }
                    });
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
                        if(temp.get("Region").equals(region)){
                            temp.put("textTitle", temp.get("Title"));
                            temp.put("textCategory", temp.get("Category") + " 크루");
                            temp.put("Region", temp.get("Region"));
                            temp.put("Date", temp.get("Date"));
                            temp.put("Description", temp.get("Description"));
                            temp.put("UID", temp.get("UID"));
                            listViewGroupData.add(temp);
                        }
                    }
                }
            }
        });
    }

    public void AddGroup(View view) {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivity(intent);
    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}