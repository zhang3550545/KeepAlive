package com.yundoku.keepalive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yundoku.keepalive.service.DeaomService;

public class DeaomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("zhang", "DeaomReceiver --> onReceive--> action: " + intent.getAction());
        DeaomService.startDeaomService(context);
    }
}
