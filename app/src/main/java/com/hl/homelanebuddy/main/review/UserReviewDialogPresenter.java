package com.hl.homelanebuddy.main.review;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.hl.hlcorelib.CoreLogger;
import com.hl.hlcorelib.HLCoreLib;
import com.hl.hlcorelib.mvp.presenters.HLCoreFragmentDialogPresenter;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.hlcorelib.utils.HLPreferenceUtils;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.business.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hl0204 on 3/2/16.
 */
public class UserReviewDialogPresenter extends HLCoreFragmentDialogPresenter<UserReviewView> {

    private ServerConnection mServerConnection;

    @Override
    protected void onBindView() {
        super.onBindView();
        mView.setTaskName(getArguments().getString(Constants.Task.TASK_NAME));
        mView.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mView.setStarColor(rating);
                ratingBar.setRating(rating);
                if (rating != 0.0)
                    mView.mPostButton.setEnabled(true);
                else
                    mView.mPostButton.setEnabled(false);
            }
        });
        mContext = getActivity();

        String[] status = getResources().getStringArray(R.array.status);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, status);
        mView.mTaskStatus.setAdapter(arrayAdapter);


        if(Constants.TaskStatus.TASK_STATUS_DONE.equals(
                getArguments().getString(Constants.Task.TASK_STATUS)))
            mView.mTaskStatus.setSelection(0);
        else if(Constants.TaskStatus.TASK_STATUS_SLIGHT_DELAYED.equals(
                getArguments().getString(Constants.Task.TASK_STATUS)))
            mView.mTaskStatus.setSelection(1);
        else if(Constants.TaskStatus.TASK_STATUS_OVER_DELAYED.equals(
                getArguments().getString(Constants.Task.TASK_STATUS)))
            mView.mTaskStatus.setSelection(2);
        else if(Constants.TaskStatus.TASK_STATUS_ONPROGRESS.equals(
                getArguments().getString(Constants.Task.TASK_STATUS)))
            mView.mTaskStatus.setSelection(3);
        else if(Constants.TaskStatus.TASK_STATUS_SCHEDULED_NOT_STARTED.equals(
                getArguments().getString(Constants.Task.TASK_STATUS)))
            mView.mTaskStatus.setSelection(4);





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
        mView.mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        mView.mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.toggleSubmit(false);
                final Bundle data = new Bundle();
//              data.putString(USER_REVIEW, getUserReview());
                data.putString(UserReviewView.USER_REVIEW, String.valueOf(mView.mRatingBar.getRating()));
                Log.d("rating--",String.valueOf(mView.mRatingBar.getRating()));
                data.putString(UserReviewView.USER_COMMENT, mView.mComments.getText().toString());
                postReview(data);

                /**
                 * Reviewers Email ID
                 */
                Answers.getInstance().logRating(new RatingEvent()
                        .putRating(new Integer(mView.getUserReview()))
                        .putContentName(((TextView)mView.getView().findViewById(R.id.event_name)).getText().toString())
                        .putContentType("Review Rating")
                        .putContentId("Review"));
            }
        });



    }


    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     * <p>
     * <p>This method will be called after {@link #onCreate(Bundle)} and
     * before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.  The
     * default implementation simply instantiates and returns a {@link Dialog}
     * class.
     * <p>
     * <p><em>Note: DialogFragment own the {@link Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(DialogInterface)}
     * and {@link #onDismiss(DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dlg = super.onCreateDialog(savedInstanceState);
//        dlg.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dlg.setTitle("Did we delight you ?");
        dlg.setCanceledOnTouchOutside(false);
        return dlg;
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
                .putCustomAttribute(Constants.CLASS_NAME, UserReviewDialogPresenter.class.getName()));
    }



    /**
     * function which will post the review to the server
     * @param data the bundle containing all the details about the review
     */
    private void postReview(final Bundle data){
        if(mServerConnection == null){
            mServerConnection = new ServerConnection(mContext);
        }
        final String email = HLPreferenceUtils.obtain().getString("USER");//To be filled based on the user
        final String endPoint = HLCoreLib.readProperty(Constants.APPConfig.review_post)+email;//To be filled when API is ready
        try{
            JSONArray tasks = new JSONArray();

            final JSONObject object = new JSONObject();
            object.put("name", getArguments().get(Constants.Task.TASK_NAME));
            object.put("rating", data.get(UserReviewView.USER_REVIEW));
            Toast.makeText(getActivity(), data.getString(UserReviewView.USER_REVIEW) , Toast.LENGTH_LONG).show();
            object.put("type", data.get(UserReviewView.USER_COMMENT));
            object.put("status", getArguments().get(Constants.Task.TASK_STATUS));

            tasks.put(object);

            ArrayList<HLObject> mTasks = getArguments().getParcelableArrayList(Constants.Task.NAME);

            for(int i=0; i < mTasks.size(); i++){
                HLObject task = mTasks.get(i);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",task.getString(Constants.Task.TASK_NAME));
                jsonObject.put("type",task.getString(Constants.Task.TASK_TYPE));
                jsonObject.put("status",task.getString(Constants.Task.TASK_STATUS));
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
                        Toast.makeText(getContext(),"Your review posted we will keep you posted",Toast.LENGTH_SHORT).show();
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               getDialog().dismiss();
                            }
                        });

                    }else{
                        Toast.makeText(getContext(),"Failed to post your review, please keep trying",Toast.LENGTH_SHORT).show();
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
    }
}
