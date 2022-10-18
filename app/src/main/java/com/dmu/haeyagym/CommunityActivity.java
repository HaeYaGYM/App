package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> listViewGroupData;

    private ListView listViewGroup;
    private SimpleAdapter simpleAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        Init();
    }

    private void Init(){
        listViewGroup = findViewById(R.id.listViewGroup);
        listViewGroupData = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, listViewGroupData, R.layout.group_item,
                new String[] {"imagePictogram", "textRange", "textGroupName", "buttonEnterGroup"},
                new int[] {R.id.imagePictogram, R.id.textRange, R.id.textGroupName, R.id.buttonEnterGroup});

        listViewGroupData.clear();
        listViewGroupData = GetGroupList();

        listViewGroup.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }

    private ArrayList<HashMap<String, String>> GetGroupList(){
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        return temp;
    }
}