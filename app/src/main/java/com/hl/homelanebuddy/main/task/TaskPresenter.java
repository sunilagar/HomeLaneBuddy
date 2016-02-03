package com.hl.homelanebuddy.main.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;
import com.hl.homelanebuddy.main.MainPresenter;

import java.util.Calendar;

/**
 * Created by hl0395 on 3/2/16.
 */
public class TaskPresenter extends HLCoreFragment<TaskView> {

    @Override
    protected Class<TaskView> getVuClass() {
        return TaskView.class;
    }

    @Override
    protected void onBindView() {
        super.onBindView();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 18); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 19);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pi = PendingIntent.getService(getActivity(), 0,
                new Intent(getActivity(), MainPresenter.class),PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pi);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

    }

    @Override
    protected int getMenuLayout() {
        return 0;
    }

    @Override
    protected int[] getDisabledMenuItems() {
        return new int[0];
    }
}
