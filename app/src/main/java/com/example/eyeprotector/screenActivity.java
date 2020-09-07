package com.example.eyeprotector;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.text.DecimalFormat;

public class screenActivity extends AppCompatActivity {

    private TextView mSetTimeTextView;
    private TextView mTimeLeftTextView;
    private ProgressBar mProgressBar;
    private Button mStartButton;
    private Button mCancelButton;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;

    private ScreenTimerModel mScreenTimerModel;
    private Handler mHandler;
    private BroadcastReceiver mBroadcast;
    private PowerManager mPowerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSetTimeTextView = findViewById(R.id.setTime);
        mTimeLeftTextView = findViewById(R.id.timeLeft);
        mTimeLeftTextView.setVisibility(View.INVISIBLE);
        mProgressBar = findViewById(R.id.progressBar);
        mStartButton = findViewById(R.id.startButton);
        mCancelButton = findViewById(R.id.cancelButton);
        mCancelButton.setVisibility(View.INVISIBLE);
        mScreenTimerModel = new ScreenTimerModel();
        mBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent resumeIntent = new Intent(context, TimerIntentService.class);
                Intent lightIntent = new Intent(context, LightService.class);

                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && mScreenTimerModel.ismRunning()){

                    mScreenTimerModel.resume();
                    TimerIntentService.shouldContinue = true;
                    mHandler.post(mUpdateTimerRunnable);
                    resumeIntent.putExtra(TimerIntentService.EXTRA_MILIS_LEFT, mScreenTimerModel.getRemainingMilliseconds());
                    context.startService(resumeIntent);
                    if (isMyServiceRunning(LightService.class) == false){
                        context.startService(lightIntent);
                    }

                }
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && mScreenTimerModel.ismRunning()){
                    mScreenTimerModel.pause();
                    TimerIntentService.shouldContinue = false;
                    mHandler.removeCallbacks(mUpdateTimerRunnable);
                    if (isMyServiceRunning(LightService.class) == false){
                        context.stopService(lightIntent);
                    }
                }
            }
        };

        registerReceiver(mBroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        NumberPicker.Formatter numFormat = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return new DecimalFormat("00").format(i);
            }
        };

        mHoursPicker = findViewById(R.id.hoursPicker);
        mHoursPicker.setMinValue(0);
        mHoursPicker.setMaxValue(99);
        mHoursPicker.setFormatter(numFormat);

        mMinutesPicker = findViewById(R.id.minutesPicker);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(59);
        mMinutesPicker.setFormatter(numFormat);

        mSecondsPicker = findViewById(R.id.secondsPicker);
        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setMaxValue(59);
        mSecondsPicker.setFormatter(numFormat);


        mHandler = new Handler();
        mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);

    }

    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(mBroadcast);
    }

    protected void onResume(){
        super.onResume();
        TimerIntentService.shouldContinue = false;

    }


protected void onStop(){
        super.onStop();
        boolean screen_off_not = mPowerManager.isInteractive();
    Intent lightIntent =  new Intent(this, LightService.class);
        if (mScreenTimerModel.ismRunning() && screen_off_not ){
            TimerIntentService.shouldContinue = true;
            Intent intent = new Intent(this, TimerIntentService.class);
            intent.putExtra(TimerIntentService.EXTRA_MILIS_LEFT, mScreenTimerModel.getRemainingMilliseconds());
            startService(intent);
           if (isMyServiceRunning(LightService.class) == false){
               startService(lightIntent);
           }
        }
        else{
            TimerIntentService.shouldContinue = false;
            mScreenTimerModel.pause();
            mHandler.removeCallbacks(mUpdateTimerRunnable);
            if (isMyServiceRunning(LightService.class)){
               stopService(lightIntent);
            }
        }
    }

@Override
    public void onBackPressed(){
        Intent mainActivities = new Intent(this, MainActivity.class);
        startActivity(mainActivities);

    }

    public void startButtonClick(View view){
        int hours = mHoursPicker.getValue();
        int minutes = mMinutesPicker.getValue();
        int seconds = mSecondsPicker.getValue();


        if (hours+minutes+seconds > 0){
            mTimeLeftTextView.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
            mStartButton.setVisibility(View.GONE);
            mCancelButton.setVisibility(View.VISIBLE);
            mSetTimeTextView.setVisibility(View.GONE);

            mScreenTimerModel.start(hours, minutes, seconds);
            mHandler.post(mUpdateTimerRunnable);

            Intent lightIntent =  new Intent(this, LightService.class);
                startService(lightIntent);



        }
    }

    public void cancelButtonClick(View view){
        TimerIntentService.shouldContinue = false;
        Intent lightIntent = new Intent(this, LightService.class);
        if (isMyServiceRunning(LightService.class) == true){
            stopService(lightIntent);
        }
        mTimeLeftTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        timerCompleted();

    }

    public void timerCompleted(){
        Intent lightIntent = new Intent(this, LightService.class);
        if (isMyServiceRunning(LightService.class) == true){
            stopService(lightIntent);
        }
        mScreenTimerModel.stop();
        mSetTimeTextView.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.GONE);
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

    private Runnable mUpdateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            mTimeLeftTextView.setText(mScreenTimerModel.toString());
            int progress = mScreenTimerModel.getProgressPercent();
            mProgressBar.setProgress(progress);

            if (progress == 100) {
                timerCompleted();
            }
            else {
                mHandler.postDelayed(this, 200);
            }
        }
    };
}