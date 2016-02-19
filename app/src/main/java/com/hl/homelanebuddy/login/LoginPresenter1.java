package com.hl.homelanebuddy.login;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
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
import com.hl.hlcorelib.utils.HLPreferenceUtils;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.main.MainPresenter;

import java.util.ArrayList;

public class LoginPresenter1 extends HLCoreActivityPresenter<LoginView1> {


    @Override
    protected void onBindView() {
        super.onBindView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.blue_grey_900));


            int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                        0);
                return;
            }else
                setupAutoComplete();

        }else
            setupAutoComplete();

        mView.mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mView.mAutoCompleteText.getText().toString().length() > 0) {
                    HLPreferenceUtils.obtain().put("USER", mView.mAutoCompleteText.getText().toString());

                    Intent intent = new Intent(LoginPresenter1.this, MainPresenter.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupAutoComplete();

                }
                return;
            }

        }

    }

    private void setupAutoComplete(){
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        ArrayList<String> mAccounts = new ArrayList<>();

        for (Account account : list) {
            if(account.name.contains("@"))
                mAccounts.add(account.name);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginPresenter1.this,
                android.R.layout.simple_dropdown_item_1line, mAccounts);


        mView.mAutoCompleteText.setAdapter(adapter);

    }

    /**
     * Function to show the snack bar
     */
    private void showSnackBar(){
        final Snackbar snackbar = Snackbar.make(mView.mAutoCompleteText, getResources().getString(R.string.internet_connection), Snackbar.LENGTH_LONG);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showSnackBar();
            }
        }).show();

    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * View page
         */
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Current View is LoginView ")
                .putContentType("View Navigation")
                .putContentId("LoginView")
                .putCustomAttribute(Constants.CLASS_NAME, LoginPresenter1.class.getName()));
    }

    @Override
    protected Class<LoginView1> getVuClass() {
        return LoginView1.class;
    }

}
