package com.hl.homelanebuddy.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.hl.hlcorelib.mvp.HLView;
import com.hl.homelanebuddy.R;

/**
 * Created by hl0395 on 15/12/15.
 */
public class LoginView1 implements HLView {

    private View mView;
    public AutoCompleteTextView mAutoCompleteText;
    public Button mLoginBtn;

    /**
     * Return the enclosing view
     *
     * @return return the enclosing view
     */
    @Override
    public View getView() {
        return mView;
    }

    /**
     * To handle the back press
     *
     * @return false if not handled true if handled
     */
    @Override
    public boolean onBackPreseed() {
        return false;
    }

    /**
     * Function which will be triggered when {@link Activity#onRestoreInstanceState(Bundle)}
     * or {@link Fragment#onViewStateRestored(Bundle)}
     *
     * @param savedInstanceState the state which saved on {HLView#onSavedInstanceState}
     */
    @Override
    public void onRecreateInstanceState(Bundle savedInstanceState) {

    }

    /**
     * Function which will be called {@link Activity#onSaveInstanceState(Bundle)}
     * or {@link Fragment#onSaveInstanceState(Bundle)}
     *
     * @param savedInstanceState the state to save the contents
     */
    @Override
    public void onSavedInstanceState(Bundle savedInstanceState) {

    }

    /**
     * Create the view from the id provided
     *
     * @param inflater inflater using which the view shold be inflated
     * @param parent   to which the view to be attached
     */
    @Override
    public void init(LayoutInflater inflater, ViewGroup parent) {
        mView = inflater.inflate(R.layout.activity_login1, parent, false);
//        TextInputLayout inputLayoutName = (TextInputLayout)mView.findViewById(R.id.input_layout_name);
//        inputLayoutName.setHint("Enter your Email ID");
//        inputLayoutName.setHintAnimationEnabled(true);
        mAutoCompleteText = (AutoCompleteTextView) mView.findViewById(R.id.auto_complete_text);
        mLoginBtn = (Button) mView.findViewById(R.id.login_button);
    }

}
