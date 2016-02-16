package com.hl.homelanebuddy.main.task;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hl.hlcorelib.HLProgressInterface;
import com.hl.hlcorelib.mvp.HLView;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.views.HLProgressView;

/**
 * Created by hl0395 on 3/2/16.
 */
public class TaskView implements HLView, HLProgressInterface {

    private View mView;
    RecyclerView mTaskList;

    HLProgressView mProgressView;

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
        mView = inflater.inflate(R.layout.task_layout, parent, false);
        mTaskList = (RecyclerView) mView.findViewById(R.id.task_list);
        mProgressView = (HLProgressView) mView.findViewById(R.id.progress_view);
        mTaskList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mTaskList.getContext());
        mTaskList.setLayoutManager(mLayoutManager);

        mView.findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18001024663"));
                mView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public void showProgress() {
        mProgressView.showProgress();
        mTaskList.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(String s) {

    }

    @Override
    public void showError(String error) {
        mProgressView.showError(error);
    }

    @Override
    public void hideProgress() {
        mProgressView.hideProgress();
        mTaskList.setVisibility(View.VISIBLE);
    }
}
