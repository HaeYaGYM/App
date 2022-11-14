package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BeatResultActivity extends AppCompatActivity {

    private TextView textMaxRate;
    private TextView textAverageRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_result);

        Init();
    }

    private void Init(){
        textMaxRate = findViewById(R.id.textMaxRate);
        textAverageRate = findViewById(R.id.textAverageRate);

        Intent intentR = getIntent();
        textMaxRate.setText(intentR.getStringExtra("max"));
        textAverageRate.setText(intentR.getStringExtra("avg"));

    }

    public void ReturnMenu(View view) {
        finish();
    }
}