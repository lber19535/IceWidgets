package com.bill.icewidgets.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.components.IceWidgets;
import com.bill.icewidgets.databinding.ActivityIceGroupBinding;
import com.bill.icewidgets.ui.events.CloseIceGroupEvent;
import com.bill.icewidgets.vm.AppGroupVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by Bill on 2016/10/21.
 */

public class ActivityIceGroup extends AppCompatActivity {

    private static final String TAG = "ActivityIceGroup";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ActivityIceGroupBinding iceGroupBinding;
    private AppGroupVM appGroupVM;
    private int widgetsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iceGroupBinding = DataBindingUtil.setContentView(this, R.layout.activity_ice_group);

        Intent intent = getIntent();

        if (intent.getAction().equals(IceWidgets.LAUNCH_ACTION)) {

            widgetsId = intent.getIntExtra(IceWidgets.APP_WIDGETS_ID, IceWidgets.DEFAULT_APP_WIDGETS_ID);

            if (widgetsId == 0) {
                logd("APP_WIDGETS_ID is 0");
                finish();
            } else {
                appGroupVM = new AppGroupVM(iceGroupBinding, widgetsId);
                iceGroupBinding.setVm(appGroupVM);
                logd("APP_WIDGETS_ID is " + widgetsId);

            }
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appGroupVM.loadGroup(widgetsId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appGroupVM != null)
            appGroupVM.destroy();
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseActivityMessage(CloseIceGroupEvent event) {
        finish();
    }
}
