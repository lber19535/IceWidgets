package com.bill.icewidgets.components.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;

import java.util.concurrent.Callable;

import bolts.Task;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_ADD;
import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_FREEZE;

/**
 * Created by Bill on 2016/11/14.
 */

public class AutoFreezeService extends JobService {

    private static final String TAG = "AutoFreezeService";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static int DELAY_AUTO_JOB_ID = 1;

    private static final String ACTION_DELAY_AUTO_FREEZE = "com.bill.icewidgets.DELAY_AUTO_FREEZE";
    private static final String ACTION_STOP_DELAY_AUTO_FREEZE = "com.bill.icewidgets.STOP_DELAY_AUTO_FREEZE";

    private static final String EXTRAS_TIME = "com.bill.icewidgets.STOP_DELAY_AUTO_FREEZE";

    public static void startDelayAutoFreeze(Context context, long time) {
        Intent intent = new Intent(context, AutoFreezeService.class);
        intent.setAction(ACTION_DELAY_AUTO_FREEZE);
        intent.putExtra(EXTRAS_TIME, time);
        context.startService(intent);
    }

    public static void stopDelayAutoFreeze(Context context) {
        Intent intent = new Intent(context, AutoFreezeService.class);
        intent.setAction(ACTION_STOP_DELAY_AUTO_FREEZE);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_DELAY_AUTO_FREEZE: {
                long time = intent.getLongExtra(EXTRAS_TIME, 0);
                handleStartDelayAutoFreeze(time);
                break;
            }
            case ACTION_STOP_DELAY_AUTO_FREEZE:
                handleStopDelayAutoFreeze();
                break;
            default:
                logd("onStartCommand: unknown action " + action);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void handleStartDelayAutoFreeze(long time) {
        Log.d(TAG, "handleStartDelayAutoFreeze: start delay time is " + time);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(DELAY_AUTO_JOB_ID++, new ComponentName(getPackageName(), AutoFreezeService.class.getName()));
        builder.setOverrideDeadline(time);
//        builder.setRequiresDeviceIdle(true);
        int code = scheduler.schedule(builder.build());
        switch (code) {
            case JobScheduler.RESULT_SUCCESS:
                logd("job schedule success");
                break;
            case JobScheduler.RESULT_FAILURE:
                logd("job schedule failure");
        }
    }

    private void handleStopDelayAutoFreeze() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancelAll();
        logd("handleStopDelayAutoFreeze: job canceled");
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<AppItem> all = realm.where(AppItem.class)
                        .beginGroup()
                        .equalTo("isFreezed", false)
                        .equalTo("itemType", ITEM_TYPE_FREEZE | ITEM_TYPE_ADD)
                        .endGroup()
                        .findAll();

                CharSequence[] pkgs = new CharSequence[all.size()];
                for (int i = 0; i < all.size(); i++) {
                    pkgs[i] = all.get(i).getPackageName();
                    logd("need freeze pkg " + pkgs[i]);
                }

                FreezeService.startFreezeApps(AutoFreezeService.this, pkgs);
                jobFinished(params, false);
                return null;
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: job stop");
        return false;
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
