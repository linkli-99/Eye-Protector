package com.example.eyeprotector;

import android.os.SystemClock;

public class ScreenTimerModel {
    private long mTargetTime;
    private long mTimeLeft;
    private long mDurationMillis;
    private boolean mRunning;

    public ScreenTimerModel(){
        mRunning = false;
    }

    public boolean ismRunning(){
        return mRunning;
    }

    public void start(long millisLeft){
        mDurationMillis = millisLeft;
        mTargetTime = SystemClock.uptimeMillis() + mDurationMillis;
        mRunning = true;
    }

    public void start(int hours, int minutes, int seconds) {
        mDurationMillis = (hours * 60 * 60 + minutes * 60 + seconds + 1) * 1000;
        mTargetTime = SystemClock.uptimeMillis() + mDurationMillis;
        mRunning = true;
    }

    public void stop(){
        mTimeLeft = mTargetTime - SystemClock.uptimeMillis();
        mRunning = false;
    }
    public void pause() {
        mTimeLeft = mTargetTime - SystemClock.uptimeMillis();
        mRunning = false;
    }
    public void resume() {
        mTargetTime = SystemClock.uptimeMillis() + mTimeLeft;
        mRunning = true;
    }

    public long getRemainingMilliseconds() {
        if (mRunning) {
            return Math.max(0, mTargetTime - SystemClock.uptimeMillis());
        }
        return 0;
    }

    public int getRemainingSeconds() {
        if (mRunning) {
            return (int) ((getRemainingMilliseconds() / 1000) % 60);
        }
        return 0;
    }

    public int getRemainingMinutes() {
        if (mRunning) {
            return (int) (((getRemainingMilliseconds() / 1000) / 60) % 60);
        }
        return 0;
    }

    public int getRemainingHours() {
        if (mRunning) {
            return (int) (((getRemainingMilliseconds() / 1000) / 60) / 60);
        }
        return 0;
    }

    public int getProgressPercent() {
        if (mDurationMillis != 1000) {
            return Math.min(100, 100 - (int) ((getRemainingMilliseconds() - 1000) * 100 /
                    (mDurationMillis - 1000)));
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", getRemainingHours(),
                getRemainingMinutes(), getRemainingSeconds());
    }

}
