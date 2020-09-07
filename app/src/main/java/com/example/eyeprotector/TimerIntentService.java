package com.example.eyeprotector;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class TimerIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String EXTRA_MILIS_LEFT = "com.example.eyeprotector.action.EXTRA_MILIS_LEFT";

    public static boolean shouldContinue = true;

    private final String CHANNEL_ID_TIMER =   "channel_timer";

    private final int NOTIFICATION_ID = 0;

    public TimerIntentService() {
        super("TimerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {




            long millisLeft = intent.getLongExtra(EXTRA_MILIS_LEFT, 0);
            ScreenTimerModel screenTimerModel = new ScreenTimerModel();
            screenTimerModel.start(millisLeft);

            createTimerNotificationChannel();


            while (screenTimerModel.ismRunning()) {
                try {
                    if (shouldContinue == false){
                        cancelNotification();
                        stopSelf();
                        break;
                    }
                    createTimerNotification(screenTimerModel.toString());
                    Thread.sleep(1000);

                    if (screenTimerModel.getRemainingMilliseconds() == 0) {
                        screenTimerModel.stop();
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        createTimerNotification("You have reached the maximum time of screen usage");
                        v.vibrate(1000);
                    }

                } catch (InterruptedException e) {

                }
            }




    }

    private void createTimerNotificationChannel(){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
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

    private void cancelNotification(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
