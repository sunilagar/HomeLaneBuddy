package com.hl.homelanebuddy.main;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEventDispatcher;
import com.hl.hlcorelib.mvp.presenters.HLCoreActivityPresenter;
import com.hl.hlcorelib.utils.HLFragmentUtils;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.login.LoginPresenter;
import com.hl.homelanebuddy.main.task.TaskPresenter;
import com.hl.homelanebuddy.views.CircleImageView;

public class MainPresenter extends HLCoreActivityPresenter<MainView> implements
        NavigationView.OnNavigationItemSelectedListener{


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onBindView() {
        super.onBindView();

        setSupportActionBar(mView.mToolbar);
        mView.mToolbar.setTitle("Mobile Phoenix");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mView.mDrawerLayout, mView.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mView.mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        setLeftNavigationView();

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        HLFragmentUtils.HLFragmentTransaction transaction =
                new HLFragmentUtils.HLFragmentTransaction();
        transaction.isRoot = true;
        transaction.mFrameId = R.id.fragment_frame;
        transaction.mFragmentClass = TaskPresenter.class;
        push(transaction);


    }

    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected Class<MainView> getVuClass() {
        return MainView.class;
    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Function to initialize the left sliding navigation view
     */
    private void setLeftNavigationView() {
        mView.mLeftNavigationView.setNavigationItemSelectedListener(this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, mView.mLeftNavigationView);
        CircleImageView imageView = (CircleImageView) headerView.findViewById(R.id.imageView);
        TextView userName = (TextView) headerView.findViewById(R.id.customer_name);
        TextView userEmail = (TextView) headerView.findViewById(R.id.customer_email);

        userName.setText(LoginPresenter.mGoogleAccount.getDisplayName());
        userEmail.setText(LoginPresenter.mGoogleAccount.getEmail());

        if (LoginPresenter.mGoogleAccount.getPhotoUrl() != null)
            imageView.loadImageURL(LoginPresenter.mGoogleAccount.getPhotoUrl().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            signOut();
            return false;
        }
        if (id == R.id.action_refresh) {

            HLCoreEvent event = new HLCoreEvent("Refresh",null);
            HLEventDispatcher.acquire().dispatchEvent(event);

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to signout from the google login
     */
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(MainPresenter.this, LoginPresenter.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
