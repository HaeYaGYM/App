package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddGroupActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbRef;

    private Spinner spinnerRegion;
    private Spinner spinnerCategory;
    private EditText textTitle;
    private EditText textDescription;
    private Button btnAdd;

    private int brdid;
    private String region;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Init();

    }

    void Init() {
        spinnerRegion = findViewById(R.id.addGroupRegion);
        spinnerCategory = findViewById(R.id.addGroupCategory);
        textTitle = findViewById(R.id.textTitleGroup);
        textDescription = findViewById(R.id.textDescriptionGroup);
        btnAdd = findViewById(R.id.btnAddGroupConfirm);


        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference("board");

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                region = "서울";
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "러닝";
            }
        });
    }

    public void AddGroupConfirm(View view) {
        if (textTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요...", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            long now = System.currentTimeMillis();
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String key = snapshot.getKey();
                        brdid = (Integer.max(brdid, Integer.parseInt(key)));
                    }
                    brdid += 1;
                } else {
                    brdid = 0;
                }
                dbRef.child(String.valueOf(brdid)).child("Title").setValue(textTitle.getText().toString());
                dbRef.child(String.valueOf(brdid)).child("Date").setValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date(now)));
                dbRef.child(String.valueOf(brdid)).child("Region").setValue(region);
                dbRef.child(String.valueOf(brdid)).child("Category").setValue(category);
                dbRef.child(String.valueOf(brdid)).child("Description").setValue(textDescription.getText().toString());
                dbRef.child(String.valueOf(brdid)).child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                finish();
            }
        });
    }
}