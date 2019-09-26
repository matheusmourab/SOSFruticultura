package com.google.codelabs.mdc.java.shrine.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import io.branch.referral.Branch;

public class ShrineApplication extends Application {
    private static ShrineApplication instance;
    private static Context appContext;

    public static ShrineApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Branch.getAutoInstance(this);
        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}