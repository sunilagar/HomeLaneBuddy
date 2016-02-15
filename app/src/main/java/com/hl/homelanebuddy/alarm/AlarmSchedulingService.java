package com.hl.homelanebuddy.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.hl.hlcorelib.HLLoaderInterface;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.hlcorelib.orm.HLQuery;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.main.MainPresenter;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code AlarmManagerReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class AlarmSchedulingService extends IntentService implements HLLoaderInterface<HLObject> {
    public AlarmSchedulingService() {
        super("SchedulingService");
    }
    
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    Bundle mBundle;
    String mDuration;

    @Override
    protected void onHandleIntent(Intent intent) {

        mBundle = intent.getBundleExtra(Constants.BUNDLE);
        mDuration = intent.getStringExtra(Constants.DURATION);

        if (mDuration != null )
            sendNotification();

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmManagerReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    // Post a notification indicating whether a doodle was found.
    private void sendNotification() {
        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, MainPresenter.class), 0);
        String title = "";
        String content = "";

        if(mBundle != null){
                title = "HomeLane "+mBundle.getString(Constants.Task.TASK_NAME);
            if(mDuration.equals("1 day"))
                content = "Tomorrow ("+convertTime(Long.parseLong(mBundle.getString(Constants.Task.TASK_DATE)))+")";
            else if(mDuration.equals("1 hour"))
                content = "Today, in 1 hour ("+convertTime(Long.parseLong(mBundle.getString(Constants.Task.TASK_DATE)))+")";
            else
                content = "Today, in 1 min ("+convertTime(Long.parseLong(mBundle.getString(Constants.Task.TASK_DATE)))+")";
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(content).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setPriority(Notification.PRIORITY_HIGH);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mBuilder.setSound(alarmSound);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        load(Constants.Task.NAME);

    }

    @Override
    public void load(String name) {
        HLQuery query = new HLQuery(name);
        query.setMselect(new String[]{Constants.Task.TASK_DATE});
        query.query(new HLQuery.HLQueryCallback() {
            /**
             * Delegate method to be called on completion of the query
             *
             * @param list  the lsit of object fetched from database
             * @param error the error raised against the query
             */
            @Override
            public void onLoad(List<HLObject> list, HLQuery.HLQueryException error) {
                AlarmSchedulingService.this.onLoad(list);
            }
        });

    }

    @Override
    public void onLoad(List<HLObject> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                HLObject tasks = list.get(i);

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = (Date) formatter.parse(tasks.getString(Constants.Task.TASK_DATE));

                compare(date.getTime(), tasks.getString(Constants.Task.TASK_NAME));
            }
            if (NEXT_TIMESTAMP != 0) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.Task.TASK_DATE, NEXT_TIMESTAMP + "");
                bundle.putString(Constants.Task.TASK_NAME, NEXT_TASK);
                alarm.setAlarm(this, bundle);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    AlarmManagerReceiver alarm = new AlarmManagerReceiver();

    public void compare(long l1, String tName) {

        long current = System.currentTimeMillis();

        if(l1 > current){
            if(NEXT_TIMESTAMP == 0) {
                NEXT_TIMESTAMP = l1;
                NEXT_TASK = tName;
            }else{
                if(NEXT_TIMESTAMP > l1) {
                    NEXT_TIMESTAMP = l1;
                    NEXT_TASK = tName;
                }
            }

        }
    }

    long NEXT_TIMESTAMP = 0;
    String NEXT_TASK = "";

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

}
