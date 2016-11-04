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
import com.bill.icewidgets.vm.ActivityController;
import com.bill.icewidgets.vm.AppSelectorVM;

public class ActivityAppSelector extends AppCompatActivity implements ActivityController {

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
        appSelectedVM.setActivityController(this);
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

    @Override
    public void finishActivity() {
        finish();
    }

    public static interface OnFinished {
        void finishActivity();
    }
}
