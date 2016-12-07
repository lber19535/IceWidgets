package com.bill.icewidgets.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.ui.settings.ServicePreferenceFragment;

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
        return null;
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
                case Intent.ACTION_SCREEN_OFF: {
                    SharedPreferences pref = context.getSharedPreferences(ServicePreferenceFragment.PREF_NAME, MODE_PRIVATE);
                    if (pref.getBoolean(getString(R.string.pref_service_auto_freeze_key), false)) {
                        logd("pref auto freeze enabled");
                        AutoFreezeService.startDelayAutoFreeze(context, getDelayTime(context));
                    } else {
                        logd("pref auto freeze disabled");
                    }
                    break;
                }
                case Intent.ACTION_SCREEN_ON:
                    AutoFreezeService.stopDelayAutoFreeze(context);
                    break;
                default:
                    break;
            }
        }
    }

    private long getDelayTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ServicePreferenceFragment.PREF_NAME, MODE_PRIVATE);
        String time = pref.getString(getString(R.string.pref_service_auto_freeze_time_key), "0");
        logd("pref time is " + time);
        return Long.valueOf(time);
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
