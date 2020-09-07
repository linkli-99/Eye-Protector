package com.example.eyeprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class positionActivity extends AppCompatActivity {

    private TextView mPositionTextView;
    private Button mPositionStart;
    private Button mPositionStop;
    private boolean isRunning;
    private PowerManager mPowerManager;
    private BroadcastReceiver mBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        isRunning = false;
        mPositionTextView = findViewById(R.id.textView);
        mPositionStart = findViewById(R.id.startPositionButton);
        mPositionStop = findViewById(R.id.cancelPositionButton);
        mPositionStop.setVisibility(View.INVISIBLE);
        mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);
        mBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent positionIntent = new Intent(context, AcceleratorService.class);
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && isRunning){
                    if (isMyServiceRunning(AcceleratorService.class) == false){
                        startService(positionIntent);
                    }
                }
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && isRunning){
                    if (isMyServiceRunning(AcceleratorService.class) == true){
                        stopService(positionIntent);
                    }
                }
            }
        };

        registerReceiver(mBroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onBackPressed(){
        Intent mainActivities = new Intent(this, MainActivity.class);
        startActivity(mainActivities);

    }

    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(mBroadcast);
    }

    protected void onStop(){
        super.onStop();
        boolean screen_off_not = mPowerManager.isInteractive();
        Intent intent = new Intent(this, AcceleratorService.class);
        if (screen_off_not == true){
            if (isMyServiceRunning(AcceleratorService.class) == false){
                startService(intent);
            }
        }
        else{
            if (isMyServiceRunning(AcceleratorService.class) == true){
                stopService(intent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startPositionButtonClick(View view){
        mPositionTextView.setVisibility(View.GONE);
        mPositionStart.setVisibility(View.GONE);
        mPositionStop.setVisibility(View.VISIBLE);
        isRunning = true;
        //System.out.println("start");
        Intent intent = new Intent(this, AcceleratorService.class);
        startService(intent);

    }

    public void cancelPositionButtonClick(View view){
        Intent intent = new Intent(this, AcceleratorService.class);
        if (isMyServiceRunning(AcceleratorService.class) == true){
            stopService(intent);
        }
        isRunning = false;
        mPositionTextView.setVisibility(View.VISIBLE);
        mPositionStart.setVisibility(View.VISIBLE);
        mPositionStop.setVisibility(View.GONE);
    }

}