package com.hl.homelanebuddy.main.review;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.RatingEvent;
import com.hl.hlcorelib.mvp.HLView;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEventDispatcher;
import com.hl.homelanebuddy.R;

/**
 * Created by hl0204 on 3/2/16.
 */
public class UserReviewView implements HLView {


    public static final String SUBMIT_CLICK_EVENT = "SUBMIT_CLICK_EVENT";

    public static final String USER_REVIEW  = "userReview";
    public static final String USER_COMMENT = "userComment";

    private View mView;
    RatingBar mRatingBar;
    TextView mRateText;
    CheckedTextView mSendFeedback;
    EditText mComments;

    @Override
    public void init(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        mView = layoutInflater.inflate(R.layout.user_review_view_layout, viewGroup, false);
        mRatingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
        mRateText = (TextView) mView.findViewById(R.id.rate);
        mComments = (EditText) mView.findViewById(R.id.comment_input);

        mView.findViewById(R.id.post_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSubmit(false);
                final Bundle data = new Bundle();
//                data.putString(USER_REVIEW, getUserReview());
                data.putString(USER_REVIEW, String.valueOf(mRatingBar.getRating()));
                data.putString(USER_COMMENT, ((TextView)mView.findViewById(R.id.comment_input)).getText().toString());
                final HLCoreEvent event = new HLCoreEvent(SUBMIT_CLICK_EVENT, data);
                HLEventDispatcher.acquire().dispatchEvent(event);

                /**
                 * Reviewers Email ID
                 */
                Answers.getInstance().logRating(new RatingEvent()
                        .putRating(new Integer(getUserReview()))
                        .putContentName(((TextView)mView.findViewById(R.id.event_name)).getText().toString())
                        .putContentType("Review Rating")
                        .putContentId("Review"));
            }
        });

        mSendFeedback = (CheckedTextView) mView.findViewById(R.id.enable_comments);
    }

    /**
     * Function to change the color of stars wrt to rating
     * @param rating rating that user gave
     */

    public void setStarColor(Float rating){
        Drawable drawable = mRatingBar.getProgressDrawable();
        if (rating <= 1.0){
            drawable.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_ATOP);
            mRateText.setText(mRatingBar.getContext().getString(R.string.hated_it));
        }else if (rating <= 2.0){
            drawable.setColorFilter(Color.parseColor("#FF8000"), PorterDuff.Mode.SRC_ATOP);
            mRateText.setText(mView.getContext().getString(R.string.disliked_it));
        }else if (rating <= 3.0){
            drawable.setColorFilter(Color.parseColor("#FFBF00"), PorterDuff.Mode.SRC_ATOP);
            mRateText.setText(mRatingBar.getContext().getString(R.string.it_is_ok));
        }else if (rating <= 4.0){
            drawable.setColorFilter(Color.parseColor("#BFFF00"), PorterDuff.Mode.SRC_ATOP);
            mRateText.setText(mRatingBar.getContext().getString(R.string.liked_it));
        }else if (rating <= 5.0){
            drawable.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_ATOP);
            mRateText.setText(mRatingBar.getContext().getString(R.string.loved_it));
        }
    }



    /**
     * function which will set the task name
     *
     * @param name the name which needs to be set
     */
    public void setTaskName(final String name){
        ((TextView)mView.findViewById(R.id.event_name)).setText(name);
    }

    /**
     * function return the user review
     *
     * @return the review user selected
     */
    public String getUserReview(){
        int id = ((RadioGroup)mView.findViewById(R.id.radio_group)).getCheckedRadioButtonId();
        return (id == R.id.very_good_button) ? "10" : (id == R.id.good_button) ? "5" :
                "1";
    }

    /**
     * function disable or enable the submit button
     *
     * @param value the value to be set
     */
    public void toggleSubmit(boolean value){
        mView.findViewById(R.id.post_button).setEnabled(value);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public boolean onBackPreseed() {
        return false;
    }

    @Override
    public void onSavedInstanceState(Bundle bundle) {

    }

    @Override
    public void onRecreateInstanceState(Bundle bundle) {

    }

}
