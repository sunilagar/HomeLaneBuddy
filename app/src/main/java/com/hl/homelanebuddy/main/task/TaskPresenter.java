package com.hl.homelanebuddy.main.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import com.hl.hlcorelib.HLCoreLib;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEvent;
import com.hl.hlcorelib.mvp.events.HLEventListener;
import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.hlcorelib.utils.HLFragmentUtils;
import com.hl.hlcorelib.utils.HLNetworkUtils;
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
public class TaskPresenter extends HLCoreFragment<TaskView> implements HLEventListener, SwipeRefreshLayout.OnRefreshListener {

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
       /* mTaskAdapter = new TaskListAdapter(null);
        mView.mTaskList.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();*/
//        hideErrorText();


//        mView.mSwipeRefreshLayout.setColorSchemeColors(0,0,0,0);

        mView.mSwipeRefreshLayout.setOnRefreshListener(this);

        mView.mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mView.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
             /*   mView.mTaskList.setVisibility(View.VISIBLE);
                mView.mErrorText.setVisibility(View.GONE);*/
                mView.mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Function to show the snack bar
     */
    private void showSnackBar() {

        final Snackbar snackbar = Snackbar.make(mView.mRelativeLayout,
                getResources().getString(R.string.internet_connection), Snackbar.LENGTH_LONG);
//        showErrorText(getResources().getString(R.string.internet_connection));

        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetParseData();
            }
        }).show();

    }

    /**
     * Function to show error text
     *
     * @param text error text
     */
    private void showErrorText(String text) {
        if (mView.mTaskList != null)
            mView.mTaskList.setVisibility(View.GONE);
       /* mView.mErrorText.setVisibility(View.VISIBLE);
        mView.mErrorText.setText(text);*/

        mView.mScrollView.setVisibility(View.VISIBLE);
        mView.mErrorText.setVisibility(View.VISIBLE);
        mView.mErrorText.setText(text);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkInternetParseData();
    }

    /**
     * Function to check the internet if yes parse the data
     * else shows Snack bar
     */
    private void checkInternetParseData() {
        if (HLNetworkUtils.isNetworkAvailable(getActivity()))
            parseData();
        else {
            mView.mSwipeRefreshLayout.setRefreshing(false);
            showSnackBar();
        }
    }

    RequestQueue volleyReqQueue;


    private void parseData() {
        mView.showProgress();
        final long currentTime = System.currentTimeMillis();
        volleyReqQueue = Volley.newRequestQueue(getActivity());
        String url = HLCoreLib.readProperty(Constants.APPConfig.get_task_details) + LoginPresenter.mGoogleAccount.getEmail();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String jsonString) {

                        try {
                            mView.mSwipeRefreshLayout.setRefreshing(true);

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
                                            if (nextAlaram < date.getTime() &&
                                                    (nextAlaram - Constants.MINS_10_MILLSECOND) < currentTime) {

                                                nextAlaram = date.getTime();
                                                nextTask = task.getString(Constants.Task.TASK_NAME);
                                            }
                                        }


                                    }

                                }
                            }
                            if (taskArray.size() > 0) {
                                mTaskAdapter = new TaskListAdapter(taskArray);
                                mView.mTaskList.setVisibility(View.VISIBLE);
                                mView.mScrollView.setVisibility(View.GONE);
                                mView.mErrorText.setVisibility(View.GONE);
                                mView.mTaskList.setAdapter(mTaskAdapter);
                                mTaskAdapter.notifyDataSetChanged();
                                setAlarm();
                            } else {
                                showErrorText(getResources().getString(R.string.no_tasks));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mView.hideProgress();
                        mView.mSwipeRefreshLayout.setRefreshing(false);

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mView.hideProgress();
                mView.mSwipeRefreshLayout.setRefreshing(false);
                if (isVisible())
                    showErrorText(getResources().getString(R.string.volley_error));
            }
        });

        volleyReqQueue.add(stringRequest);

    }

    public void hideErrorText() {
        mView.mTaskList.setVisibility(View.VISIBLE);
        mView.mErrorText.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Current View is TaskView ")
                .putContentType("View Navigation")
                .putContentId("TaskView")
                .putCustomAttribute(Constants.CLASS_NAME, TaskPresenter.class.getName()));
        if (mTaskAdapter != null)
            mTaskAdapter.notifyDataSetChanged();
    }

    private void setAlarm() {
        if (nextAlaram != 0) {
            if ((System.currentTimeMillis() + Constants.MINS_10_MILLSECOND) <= nextAlaram) {

                Bundle bundle = new Bundle();
                bundle.putString(Constants.Task.TASK_DATE, nextAlaram + "");
                bundle.putString(Constants.Task.TASK_NAME, nextTask);

                mAlarmMgr = new AlarmManagerReceiver();

                mAlarmMgr.setAlarm(getActivity(), bundle);
            }
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
        } else if (e.getType().equals("Refresh"))
            checkInternetParseData();

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

    @Override
    public void onRefresh() {
        checkInternetParseData();
    }
}
