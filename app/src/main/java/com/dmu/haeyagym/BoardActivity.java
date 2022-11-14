package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BoardActivity extends AppCompatActivity {

    private TextView textTitle;
    private TextView textDescription;
    private TextView textDate;
    private TextView textUID;
    private TextView textCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Init();
    }

    private void Init() {
        textTitle = findViewById(R.id.textBoardTitle);
        textDescription = findViewById(R.id.textBoardDescription);
        textCategory = findViewById(R.id.textBoardCategory);
        textDate = findViewById(R.id.textBoardDate);
        textUID = findViewById(R.id.textBoardUID);

        Intent intentR = getIntent();
        textTitle.setText(intentR.getStringExtra("title"));
        textDescription.setText(intentR.getStringExtra("description"));
        textDate.setText(intentR.getStringExtra("date"));
        textUID.setText(intentR.getStringExtra("uid"));
        textCategory.setText(intentR.getStringExtra("category"));
    }

    public void ReturnCommunity(View view) {
        finish();
    }
}