package com.hl.homelanebuddy.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.hl.hlcorelib.mvp.presenters.HLCoreActivityPresenter;
import com.hl.homelanebuddy.R;
import com.hl.homelanebuddy.login.LoginPresenter;

public class SplashPresenter extends HLCoreActivityPresenter<SplashView> {

    @Override
    protected void onBindView() {
        super.onBindView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.brick_red_dark));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashPresenter.this, LoginPresenter.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }

    /**
     * Function which return the enclosing view class, this will be used to
     * create the respective view bind it to the Context
     *
     * @return return the enclosed view class
     */

    @Override
    protected Class<SplashView> getVuClass() {
        return SplashView.class;
    }
}
