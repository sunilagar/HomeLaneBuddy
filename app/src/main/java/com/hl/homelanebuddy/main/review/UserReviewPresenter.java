package com.hl.homelanebuddy.main.review;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.hl.hlcorelib.CoreLogger;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEvent;
import com.hl.hlcorelib.mvp.events.HLEventListener;
import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.business.ServerConnection;
import com.hl.homelanebuddy.login.LoginPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hl0204 on 3/2/16.
 */
public class UserReviewPresenter extends HLCoreFragment<UserReviewView> implements HLEventListener {

    private ServerConnection mServerConnection;

    @Override
    protected void onBindView() {
        super.onBindView();
        if(!hasEventListener(UserReviewView.SUBMIT_CLICK_EVENT, this)){
            addEventListener(UserReviewView.SUBMIT_CLICK_EVENT, this);
        }
        mView.setTaskName(getArguments().getString(Constants.Task.TASK_NAME));
        mContext = getActivity();
    }

    private Context mContext;

    @Override
    protected Class<UserReviewView> getVuClass() {
        return UserReviewView.class;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        /**
         * View page
         */
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Current View is UserReviewView ")
                .putContentType("View Navigation")
                .putContentId("UserReviewView")
                .putCustomAttribute(Constants.CLASS_NAME, UserReviewPresenter.class.getName()));
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
    public void onEvent(HLEvent hlEvent) {
        final Bundle data = ((HLCoreEvent)hlEvent).getmExtra();

        postReview(data);

    }

    /**
     * function which will post the review to the server
     * @param data the bundle containing all the details about the review
     */
    private void postReview(final Bundle data){
        if(mServerConnection == null){
            mServerConnection = new ServerConnection(mContext);
        }
        final String email = LoginPresenter.mGoogleAccount.getEmail();//To be filled based on the user
        final String endPoint = "http://54.169.216.87/projectexp/v1/postRatings/"+email;//To be filled when API is ready
        try{
            JSONArray tasks = new JSONArray();

            final JSONObject object = new JSONObject();
            object.put("name", getArguments().get(Constants.Task.TASK_NAME));
            object.put("rating", data.get(UserReviewView.USER_REVIEW));
            object.put("type", data.get(UserReviewView.USER_COMMENT));

            tasks.put(object);

            ArrayList<HLObject> mTasks = getArguments().getParcelableArrayList(Constants.Task.NAME);

            for(int i=0; i < mTasks.size(); i++){
                HLObject task = mTasks.get(i);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",task.getString(Constants.Task.TASK_NAME));
                jsonObject.put("type",task.getString(Constants.Task.TASK_TYPE));
                tasks.put(jsonObject);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("taskRatings", tasks);

            mServerConnection.doPost(endPoint, null, jsonObject.toString(), -1,
                    new ServerConnection.ServerConnectionListener(){
                /**
                 * function which will be called on when the request succeed
                 *
                 * @param data the data which will be send across
                 */
                @Override
                public void onFinish(ServerConnection.ServerResponse data) {
                    if(data.error == null){
                        showToast("Your review posted we will keep you posted");
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pop();
                            }
                        });

                    }else{
                        showToast("Failed to post your review, please keep trying");
                        mView.toggleSubmit(true);
                    }
                }
            });
        }catch (JSONException e){
            CoreLogger.log(e);
        }
    }

    @Override
    protected void onDestroyHLView() {
        super.onDestroyHLView();
        mServerConnection = null;
        removeEventListener(UserReviewView.SUBMIT_CLICK_EVENT,this);
    }
}
