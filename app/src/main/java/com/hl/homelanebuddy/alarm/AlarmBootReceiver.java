package com.hl.homelanebuddy.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hl.hlcorelib.HLLoaderInterface;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.hlcorelib.orm.HLQuery;
import com.hl.homelanebuddy.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class AlarmBootReceiver extends BroadcastReceiver implements HLLoaderInterface<HLObject> {
    AlarmManagerReceiver alarm = new AlarmManagerReceiver();

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            mContext = context;
            load(Constants.Task.NAME);
        }
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
                    AlarmBootReceiver.this.onLoad(list);
            }
        });


    }

    @Override
    public void onLoad(List<HLObject> list) {

        for (int i = 0; i < list.size(); i++) {
            try {
                HLObject tasks = list.get(i);

//                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                Date date = (Date) formatter.parse(tasks.getString(Constants.Task.TASK_DATE));

                compare(Long.parseLong(tasks.getString(Constants.Task.TASK_DATE)), tasks.getString(Constants.Task.TASK_NAME));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(NEXT_TIMESTAMP != 0) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.Task.TASK_DATE, NEXT_TIMESTAMP+"");
            bundle.putString(Constants.Task.TASK_NAME, NEXT_TASK);
            alarm.setAlarm(mContext, bundle);
        }
    }

    /*
     * Function that compares the date and has the next alarm time
     * and task name
     */
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

}