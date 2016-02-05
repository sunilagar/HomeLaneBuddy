package com.hl.homelanebuddy.main.review;

import android.os.Bundle;

import com.hl.hlcorelib.CoreLogger;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEvent;
import com.hl.hlcorelib.mvp.events.HLEventListener;
import com.hl.hlcorelib.mvp.presenters.HLCoreFragment;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.business.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

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
    }

    @Override
    protected Class<UserReviewView> getVuClass() {
        return UserReviewView.class;
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

    }

    /**
     * function which will post the review to the server
     * @param data the bundle containing all the details about the review
     */
    private void postReview(final Bundle data){
        if(mServerConnection == null){
            mServerConnection = new ServerConnection(getActivity());
        }
        final String email = "";//To be filled based on the user
        final String endPoint = "";//To be filled when API is ready
        try{
            final JSONObject object = new JSONObject();
            object.put("email", email);
            object.put(Constants.Task.TASK_ID, getArguments().get(Constants.Task.TASK_ID));
            object.put(UserReviewView.USER_COMMENT, data.get(UserReviewView.USER_COMMENT));
            object.put(UserReviewView.USER_REVIEW, data.get(UserReviewView.USER_REVIEW));
            mServerConnection.doPost(endPoint, null, object.toString(), -1,
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
}
