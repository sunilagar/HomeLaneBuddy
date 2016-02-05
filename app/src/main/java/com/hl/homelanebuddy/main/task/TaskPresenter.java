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
