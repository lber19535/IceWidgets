package com.bill.icewidgets.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.components.IceWidgets;
import com.bill.icewidgets.databinding.ActivityAppSelectorBinding;
import com.bill.icewidgets.ui.events.CloseIceGroupEvent;
import com.bill.icewidgets.ui.events.CloseSelectorEvent;
import com.bill.icewidgets.vm.AppSelectorVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ActivityAppSelector extends AppCompatActivity {

    private static final String TAG = "ActivityAppSelector";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private ActivityAppSelectorBinding binding;
    private AppSelectorVM appSelectedVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_selector);

        Intent intent = getIntent();

        appSelectedVM = new AppSelectorVM(binding, intent.getIntExtra(IceWidgets.APP_WIDGETS_ID, IceWidgets.DEFAULT_APP_WIDGETS_ID));
        binding.setVm(appSelectedVM);

        appSelectedVM.loadAppsAsync();
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
    protected void onDestroy() {
        super.onDestroy();
        appSelectedVM.destroy();
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, "msg");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseActivityMessage(CloseSelectorEvent event) {
        finish();
    }


}
