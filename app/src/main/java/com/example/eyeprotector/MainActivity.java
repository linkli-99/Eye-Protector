package com.example.eyeprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Button mPositionButton;
    private Button mScreenTimeButton;
    private Button mDistanceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPositionButton = findViewById(R.id.positionDetection);
        mScreenTimeButton = findViewById(R.id.screenTracking);
        mDistanceButton = findViewById(R.id.distanceTracking);


    }

    public void startScreenTime(View view){
        openScreenTimerActivity();
    }

    public void openScreenTimerActivity(){
        Intent intent = new Intent(this, screenActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void startPosition(View view){
        openPositionActivity();
    }

    public void openPositionActivity(){
        Intent intent = new Intent(this, positionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


}