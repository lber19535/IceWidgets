package com.bill.icewidgets.components.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;

/**
 * Created by Bill on 2016/11/14.
 */

public class ScreenService extends Service {

    private static final String TAG = "ScreenService";
    private static final boolean DEBUG = BuildConfig.DEBUG;


    private ScreenBroadcastReceiver receiver = new ScreenBroadcastReceiver();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, screenStateFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            logd(intent.getAction());
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                    AutoFreezeService.startDelayAutoFreeze(context, getDelayTime(context));
                    break;
                case Intent.ACTION_SCREEN_ON:
                    AutoFreezeService.stopDelayAutoFreeze(context);
                    break;
                default:
                    break;
            }
        }
    }

    private long getDelayTime(Context context) {
        return 1000;
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
