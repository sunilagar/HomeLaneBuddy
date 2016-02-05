package com.hl.homelanebuddy;

import com.crashlytics.android.Crashlytics;
import com.hl.hlcorelib.HLApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Created by hl0395 on 4/2/16.
 */
public class HomeLaneBuddy extends HLApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

    }
}
