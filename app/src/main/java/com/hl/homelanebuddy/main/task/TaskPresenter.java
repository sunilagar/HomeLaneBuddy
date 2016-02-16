package com.hl.homelanebuddy.main.task;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hl.hlcorelib.HLCoreLib;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEvent;
import com.hl.hlcorelib.mvp.events.HLEventListener;
import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.hlcorelib.utils.HLFragmentUtils;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.alarm.AlarmManagerReceiver;
import com.hl.homelanebuddy.login.LoginPresenter;
import com.hl.homelanebuddy.main.review.UserReviewPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hl0395 on 3/2/16.
 */
public class TaskPresenter extends HLCoreFragment<TaskView> implements HLEventListener {

    TaskListAdapter mTaskAdapter;
    ArrayList<HLObject> taskArray = new ArrayList<>();
    AlarmManagerReceiver mAlarmMgr;
    long nextAlaram = 0;
    String nextTask;

    @Override
    protected Class<TaskView> getVuClass() {
        return TaskView.class;
    }

    @Override
    protected void onBindView() {
        super.onBindView();

        if (!hasEventListener(Constants.USER_REVIEW_EVENT, this)) {
            addEventListener(Constants.USER_REVIEW_EVENT, this);
        }
        if (!hasEventListener("Refresh", this)) {
            addEventListener("Refresh", this);
        }
        nextAlaram = 0;
        parseData();
    }

    RequestQueue volleyReqQueue;

    private void parseData() {

        mView.mProgressView.showProgress();
        final long currentTime = System.currentTimeMillis();
        volleyReqQueue = Volley.newRequestQueue(getActivity());
        String url = HLCoreLib.readProperty(Constants.APPConfig.get_task_details)+ LoginPresenter.mGoogleAccount.getEmail();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String jsonString) {

                                    try{

                                        taskArray.clear();
                                    JSONObject tasks = new JSONObject(jsonString);

                                    JSONArray taskList = tasks.getJSONArray("Details");

                                    for (int i = 0; i < taskList.length(); i++) {
                                        JSONObject task = taskList.getJSONObject(i);
                                        HLObject taskObj = new HLObject(Constants.Task.NAME);
                                        taskObj.put(Constants.Task.TASK_NAME, task.getString(Constants.Task.TASK_NAME));

                                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        if (task.getString(Constants.Task.TASK_DATE).length() > 15) {
                                            Date date = (Date) formatter.parse(task.getString(Constants.Task.TASK_DATE));

                                            taskObj.put(Constants.Task.TASK_DATE, date.getTime() + "");
                                            taskObj.put(Constants.Task.TASK_ASSIGNED_TO, task.getString(Constants.Task.TASK_ASSIGNED_TO));
                                            taskObj.put(Constants.Task.TASK_TYPE, task.getString(Constants.Task.TASK_TYPE));
                                            taskObj.save();
                                            taskArray.add(taskObj);

                                            if (date.getTime() > currentTime) {
                                                if (nextAlaram == 0) {
                                                    nextAlaram = date.getTime();
                                                    nextTask = task.getString(Constants.Task.TASK_NAME);
                                                } else {
                                                    if (nextAlaram > date.getTime()) {
                                                        nextAlaram = date.getTime();
                                                        nextTask = task.getString(Constants.Task.TASK_NAME);
                                                    }
                                                }


                                    }

                                }
                            }
                            mTaskAdapter = new TaskListAdapter(taskArray);
                            mView.mTaskList.setAdapter(mTaskAdapter);
                            mTaskAdapter.notifyDataSetChanged();
                            setAlarm();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mView.mProgressView.hideProgress();


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mView.mProgressView.hideProgress();

            }
        });

        volleyReqQueue.add(stringRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTaskAdapter != null)
            mTaskAdapter.notifyDataSetChanged();
    }

    private void setAlarm(){
        if(nextAlaram != 0) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.Task.TASK_DATE, nextAlaram + "");
            bundle.putString(Constants.Task.TASK_NAME, nextTask);

            mAlarmMgr = new AlarmManagerReceiver();

            mAlarmMgr.setAlarm(getActivity(), bundle);
        }
    }

    @Override
    public void onEvent(HLEvent hlEvent) {
        HLCoreEvent e = (HLCoreEvent) hlEvent;
        Bundle bundle = e.getmExtra();

        if (e.getType().equals(Constants.USER_REVIEW_EVENT)) {

            HLFragmentUtils.HLFragmentTransaction transaction =
                    new HLFragmentUtils.HLFragmentTransaction();
            transaction.mFrameId = R.id.fragment_frame;
            transaction.mParameters = bundle;
            transaction.mFragmentClass = UserReviewPresenter.class;
            push(transaction);
        }else if (e.getType().equals("Refresh"))
            parseData();

    }

    @Override
    protected void onDestroyHLView() {
        super.onDestroyHLView();

        removeEventListener(Constants.USER_REVIEW_EVENT, this);
        removeEventListener("Refresh", this);
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
