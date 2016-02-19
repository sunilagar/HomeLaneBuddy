package com.hl.homelanebuddy.main.review;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.hl.hlcorelib.CoreLogger;
import com.hl.hlcorelib.HLCoreLib;
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
        mView.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mView.setStarColor(rating);
                ratingBar.setRating(rating);
            }
        });
        mContext = getActivity();

        mView.mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mView.mSendFeedback.isChecked()) {
                    mView.mSendFeedback.setChecked(false);
//                    mView.mComments.setVisibility(View.GONE);
                    collapse(mView.mComments);
                }
                else {
                    mView.mSendFeedback.setChecked(true);
//                    mView.mComments.setVisibility(View.VISIBLE);
                    expand(mView.mComments);
                }
            }
        });
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(750);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(750);
        v.startAnimation(a);
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
        final String endPoint = HLCoreLib.readProperty(Constants.APPConfig.review_post)+email;//To be filled when API is ready
        try{
            JSONArray tasks = new JSONArray();

            final JSONObject object = new JSONObject();
            object.put("name", getArguments().get(Constants.Task.TASK_NAME));
            object.put("rating", data.get(UserReviewView.USER_REVIEW));
            Toast.makeText(getActivity(), data.getString(UserReviewView.USER_REVIEW) , Toast.LENGTH_LONG).show();
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
            Log.d("ratings---",jsonObject.toString());

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

//    /**
//     * Notification that the rating has changed. Clients can use the
//     * fromUser parameter to distinguish user-initiated changes from those
//     * that occurred programmatically. This will not be called continuously
//     * while the user is dragging, only when the user finalizes a rating by
//     * lifting the touch.
//     *
//     * @param ratingBar The RatingBar whose rating has changed.
//     * @param rating    The current rating. This will be in the range
//     *                  0..numStars.
//     * @param fromUser  True if the rating change was initiated by a user's
//     */
//    @Override
//    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//        mView.setStarColor(rating);
//    }
}
