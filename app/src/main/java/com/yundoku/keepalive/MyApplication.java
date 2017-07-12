package com.yundoku.keepalive;

import android.app.Application;

import com.yundoku.keepalive.service.DeaomService;


/**
 * Created by Widsom Zhang on 2017/7/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeaomService.startDeaomService(getApplicationContext());
    }
}
