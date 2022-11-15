package com.dmu.haeyagym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BeatResultActivity extends AppCompatActivity {

    private TextView textMaxRate;
    private TextView textAverageRate;
    private Intent intentR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_result);

        Init();
    }

    private void Init(){
        textMaxRate = findViewById(R.id.textMaxRate);
        textAverageRate = findViewById(R.id.textAverageRate);

        intentR = getIntent();
        textMaxRate.setText("최대 심박수는 "+ String.valueOf(intentR.getIntExtra("max", 0)) + "입니다.");
        textAverageRate.setText("평균 심박수는 " + String.valueOf(intentR.getIntExtra("avg", 0)) + "입니다.");
    }

    public void ReturnMenu(View view) {
        Intent intent;
        switch (intentR.getStringExtra("menu")){
            case "timer":
                intent = new Intent(getApplicationContext(), TimerActivity.class);
                break;
            case "beatRate":
                intent = new Intent(getApplicationContext(), CheckHeartbeatActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + intentR.getStringExtra("menu"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}