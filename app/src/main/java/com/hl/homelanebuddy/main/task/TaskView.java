package com.hl.homelanebuddy.main.task;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
    ScrollView mScrollView;
    HLProgressView mProgressView;
    RelativeLayout mRelativeLayout;
    TextView mErrorText;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FloatingActionsMenu menuMultipleActions;

    View actionA, actionB;

    FloatingActionButton actionC, actionD, actionE;


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
        mRelativeLayout = (RelativeLayout) mView.findViewById(R.id.relative_layout);
        mTaskList = (RecyclerView) mView.findViewById(R.id.task_list);
        mProgressView = (HLProgressView) mView.findViewById(R.id.progress_view);

        mScrollView = (ScrollView) mView.findViewById(R.id.scroll_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe);

        mErrorText = (TextView) mView.findViewById(R.id.error_display);
        menuMultipleActions = (FloatingActionsMenu) mView.findViewById(R.id.multiple_actions);
        actionA = (View) mView.findViewById(R.id.action_a);
        actionB = (View) mView.findViewById(R.id.action_b);
        actionC = new FloatingActionButton(mView.getContext());
        actionD = new FloatingActionButton(mView.getContext());
        actionE = new FloatingActionButton(mView.getContext());

        mTaskList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mTaskList.getContext());
        mTaskList.setLayoutManager(mLayoutManager);

//        mView.findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:18001024663"));
//                mView.getContext().startActivity(intent);
//
//            }
//        });

        actionC.setTitle("HL Helpline-Toll free");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCallEvent && !mEmailEvent) {
                    Toast.makeText(mView.getContext(), "Call event", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mView.getContext(), "Email event", Toast.LENGTH_SHORT).show();

                }


            }
        });


        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(View.GONE);
                actionA.setVisibility(View.VISIBLE);
                mCallEvent = true;
                mEmailEvent = false;


                if (!mCallFlag) {

                    mCallFlag = true;
                    actionC.setVisibility(View.VISIBLE);
                    actionD.setVisibility(View.VISIBLE);
                    actionE.setVisibility(View.VISIBLE);

                    actionC.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));
                    actionD.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));
                    actionE.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));


                    actionC.setImageResource(R.drawable.ic_call_white_24dp);

                    actionD.setImageResource(R.drawable.ic_call_white_24dp);

                    actionE.setImageResource(R.drawable.ic_call_white_24dp);


                    menuMultipleActions.addButton(actionC);
                    menuMultipleActions.addButton(actionD);
                    menuMultipleActions.addButton(actionE);

                } else {
                    actionC.setVisibility(View.VISIBLE);
                    actionD.setVisibility(View.VISIBLE);
                    actionE.setVisibility(View.VISIBLE);


                    actionC.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));
                    actionD.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));
                    actionE.setColorNormal(mView.getContext().getResources().getColor(R.color.blue));


                    actionC.setImageResource(R.drawable.ic_call_white_24dp);

                    actionD.setImageResource(R.drawable.ic_call_white_24dp);

                    actionE.setImageResource(R.drawable.ic_call_white_24dp);


                }






          /*      actionA.setVisibility(actionA.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                menuMultipleActions.addButton(actionC);
                menuMultipleActions.addButton(actionD);
                menuMultipleActions.addButton(actionE);

                actionA.setVisibility(actionC.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                actionC.setVisibility(View.GONE);
                actionD.setVisibility(View.GONE);
                actionE.setVisibility(View.GONE);
*/

            }
        });


        actionD.setTitle("HL CSR Team");
        actionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * For call made to CSR Team
                 */
                Answers.getInstance().logCustom(new CustomEvent("Customer Care Help ")
                        .putCustomAttribute("Call", "Made a call")
                        .putCustomAttribute("To","HL CSR Team"));

                /**
                 * For Email to CSR Team
                 */
                Answers.getInstance().logCustom(new CustomEvent("Customer Care Help ")
                        .putCustomAttribute("Email", "Mail to HL")
                        .putCustomAttribute("To","HL CSR Team"));
//                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        actionE.setTitle("HL Design Team");
        actionE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * For call made to Design Team
                 */
                Answers.getInstance().logCustom(new CustomEvent("Customer Care Help ")
                        .putCustomAttribute("Call", "Made a call")
                        .putCustomAttribute("To","HL Design Team"));

                /**
                 * For Email to Design Team
                 */
                Answers.getInstance().logCustom(new CustomEvent("Customer Care Help ")
                        .putCustomAttribute("Email", "Mail to HL")
                        .putCustomAttribute("To","HL Design Team"));

//                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });


        menuMultipleActions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Log.d("TAG", "onMenuExpanded");
                actionB.setVisibility(View.VISIBLE);
                actionA.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                Log.d("TAG", "onMenuCollapsed");


                actionC.setVisibility(View.GONE);
                actionD.setVisibility(View.GONE);
                actionE.setVisibility(View.GONE);
            }
        });

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionA.setVisibility(View.GONE);
                actionB.setVisibility(View.VISIBLE);

                mCallEvent = false;
                mEmailEvent = true;

                if (!mCallFlag) {
                    mCallFlag = true;

                    actionC.setVisibility(View.VISIBLE);
                    actionD.setVisibility(View.VISIBLE);
                    actionE.setVisibility(View.VISIBLE);

                    actionC.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));
                    actionD.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));
                    actionE.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));


                    actionC.setImageResource(R.drawable.ic_email_white_24dp);
                    actionD.setImageResource(R.drawable.ic_email_white_24dp);
                    actionE.setImageResource(R.drawable.ic_email_white_24dp);


                    menuMultipleActions.addButton(actionC);
                    menuMultipleActions.addButton(actionD);
                    menuMultipleActions.addButton(actionE);
                } else {
                    actionC.setVisibility(View.VISIBLE);
                    actionD.setVisibility(View.VISIBLE);
                    actionE.setVisibility(View.VISIBLE);

                    actionC.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));
                    actionD.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));
                    actionE.setColorNormal(mView.getContext().getResources().getColor(R.color.orange));


                    actionC.setImageResource(R.drawable.ic_email_white_24dp);

                    actionD.setImageResource(R.drawable.ic_email_white_24dp);

                    actionE.setImageResource(R.drawable.ic_email_white_24dp);

                }


            }
        });


    }

    boolean mCallFlag = false, mCallEvent = false, mEmailEvent = false;


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
