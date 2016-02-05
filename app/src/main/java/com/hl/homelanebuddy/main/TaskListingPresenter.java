package com.hl.homelanebuddy.main;

import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;

/**
 * Created by hl0204 on 4/2/16.
 */
public class TaskListingPresenter extends HLCoreFragment<TaskListView> {


    @Override
    protected Class<TaskListView> getVuClass() {
        return TaskListView.class;
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
