package com.example.eyeprotector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AcceleratorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mSensor;
    private final String CHANNEL_ID_TIMER =   "channel_accelerator";

    private final int NOTIFICATION_ID = 2;
    public AcceleratorService() {
    }
@Override
    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        createTimerNotificationChannel();



    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
if (inRightPosition(sensorEvent.values[0], sensorEvent.values[1],sensorEvent.values[2]) == false){
    createTimerNotification(getString(R.string.check_position));
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }
}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void createTimerNotificationChannel(){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        CharSequence name = getString(R.string.channel_position);
        String description = getString(R.string.channel_position_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    private void createTimerNotification(String text){

        Intent back = new Intent(this, positionActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, back, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_TIMER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private boolean inRightPosition(float x, float y, float z){
        if (x <= 9.81 && x >= 4.0 && y >= 0 && y <= 8.5 && z >=0 && z <= 3){
            return true;
        }
        else if (x <= 0 && x >= -9.81 && y >= 0 && y <= 8.5 && z>= 0 && z <= 3){
            return true;
        }
        return false;
    }

}


