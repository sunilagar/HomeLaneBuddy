package com.hl.homelanebuddy.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.hl.homelanebuddy.Constants;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code AlarmSchedulingService} to do some work.
 */
public class AlarmManagerReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {   

        Intent service = new Intent(context, AlarmSchedulingService.class);
        service.putExtra(Constants.BUNDLE, intent.getBundleExtra(Constants.BUNDLE));
        service.putExtra(Constants.DURATION, intent.getStringExtra(Constants.DURATION));
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }

    Bundle mBundle;

    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context, Bundle bundle) {
        String timeStamp = bundle.getString(Constants.Task.TASK_DATE);

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmManagerReceiver.class);
        intent.putExtra(Constants.BUNDLE, bundle);

        Calendar calendar = Calendar.getInstance();

        long nextAlaram = Long.parseLong(timeStamp);
        long currentTime = System.currentTimeMillis();

        if(currentTime <= (nextAlaram - Constants.DAY_1_MILLSECOND)) {
            calendar.setTimeInMillis(nextAlaram - Constants.DAY_1_MILLSECOND);
            intent.putExtra(Constants.DURATION, Constants.DURATION_1_DAY);
        }else if(currentTime <= (nextAlaram - Constants.HOUR_1_MILLSECOND)) {
            calendar.setTimeInMillis(nextAlaram - Constants.HOUR_1_MILLSECOND);
            intent.putExtra(Constants.DURATION, Constants.DURATION_1_HOUR);
        }else if(currentTime <= (nextAlaram - Constants.MINS_10_MILLSECOND)) {
            calendar.setTimeInMillis(nextAlaram - Constants.MINS_10_MILLSECOND);
            intent.putExtra(Constants.DURATION, Constants.DURATION_10_MINS);
        }
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);


        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), alarmIntent);

        // Enable {@code AlarmBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        
        // Disable {@code AlarmBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
