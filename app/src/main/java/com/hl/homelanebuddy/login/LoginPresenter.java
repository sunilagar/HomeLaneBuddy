package com.hl.homelanebuddy.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hl.hlcorelib.mvp.presenters.HLCoreActivityPresenter;
import com.hl.hlcorelib.utils.HLNetworkUtils;
import com.hl.homelanebuddy.main.MainPresenter;
import com.hl.homelanebuddy.R;

public class LoginPresenter extends HLCoreActivityPresenter<LoginView> implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 9001;

    public GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    public static GoogleSignInAccount mGoogleAccount;

    @Override
    protected void onBindView() {
        super.onBindView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.blue_grey_900));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mView.mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mView.mSignInButton.setScopes(gso.getScopeArray());

        mView.mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HLNetworkUtils.isNetworkAvailable(LoginPresenter.this))
                    signIn();
                else
                    showSnackBar();
            }
        });

    }

    /**
     * Function to show the snack bar
     */
    private void showSnackBar(){
        final Snackbar snackbar = Snackbar.make(mView.mSignInButton, "Please check your internet connection.", Snackbar.LENGTH_LONG);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HLNetworkUtils.isNetworkAvailable(LoginPresenter.this)) {
                    signIn();
                }else
                    showSnackBar();
            }
        }).show();

    }

    @Override
    protected Class<LoginView> getVuClass() {
        return LoginView.class;
    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Function to handle after logging with google
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            mGoogleAccount = result.getSignInAccount();
            mView.mStatusTextView.setText(getString(R.string.signed_in_fmt, mGoogleAccount.getDisplayName()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    /**
     * Function after login with google button is clicked
     */
    private void signIn() {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        //Strat another Activity Here
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        Answers.getInstance().logCustom(new CustomEvent("Login")
                                .putCustomAttribute("Customer",mGoogleAccount.getEmail() ));

                    default:
                        break;
                }
                return false;
            }
        });
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                       updateUI(false);
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed:" + connectionResult);
    }

    /**
     * Function to show the progress Dialog
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    /**
     * Function to hide the progress dialog
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * Function to update the UI after login result
     */
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            Intent intent = new Intent(LoginPresenter.this, MainPresenter.class);
            startActivity(intent);
            finish();
        } else {
            if(mView != null)
            mView.mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }



}
