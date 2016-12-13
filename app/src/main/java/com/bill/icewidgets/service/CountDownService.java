package com.bill.icewidgets.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * Copy IntentService and make small modification.
 * <p>
 * Add count down timer for auto stop service. The default count
 * down time is 30s, you can overwrite the countDownTime method
 * to change this value.
 * <p>
 * Created by Bill on 2016/12/5.
 */

public abstract class CountDownService extends Service {

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private boolean mRedelivery;
    private CountDownTimer timer;
    private static final int DEFAULT_COUNTDOWN_TIME = 30 * 1000;

    private final class ServiceTimer extends CountDownTimer {

        private Service service;

        public ServiceTimer(Service service, long millisInFuture) {
            super(millisInFuture, millisInFuture);
            this.service = service;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // empty
        }

        @Override
        public void onFinish() {
            service.stopSelf();
        }
    }

    protected int countDownTime() {
        return DEFAULT_COUNTDOWN_TIME;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
        }
    }


    public CountDownService(String name) {
        super();
        mName = name;
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("CountDownService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        timer = new ServiceTimer(this, countDownTime());
        timer.start();

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        timer.cancel();
        timer.start();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }


    @WorkerThread
    protected abstract void onHandleIntent(@Nullable Intent intent);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
