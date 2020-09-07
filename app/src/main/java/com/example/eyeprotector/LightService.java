package com.example.eyeprotector;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LightService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mSensor;
    private float threshold = 0.5f;
    private static boolean shouldContinue = true;
    private final String CHANNEL_ID_TIMER =   "channel_light";

    private final int NOTIFICATION_ID = 1;

    public LightService() {
    }
@Override
    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
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
    float value = sensorEvent.values[0];
    if (value <= threshold){

      createTimerNotification(getString(R.string.lightWarning));

    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void createTimerNotificationChannel(){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        CharSequence name = getString(R.string.channel_light);
        String description = getString(R.string.channel_light_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    private void createTimerNotification(String text){

        Intent back = new Intent(this, screenActivity.class);
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

}
