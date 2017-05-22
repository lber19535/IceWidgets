package com.bill.icewidgets.groupdialog.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.IceWidgets;
import com.bill.icewidgets.R;
import com.bill.icewidgets.common.events.CloseIceGroupEvent;

import com.bill.icewidgets.databinding.ActivityIceGroupBinding;
import com.bill.icewidgets.databinding.AppItemBinding;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.groupdialog.GroupContract;
import com.bill.icewidgets.groupdialog.data.GroupRepository;
import com.bill.icewidgets.groupdialog.presenter.GroupPresenter;
import com.bill.icewidgets.groupdialog.utils.AppItemConverter;
import com.bill.icewidgets.groupdialog.vm.AppGroupVM;
import com.bill.icewidgets.groupdialog.vm.AppItemVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by Bill on 2016/10/21.
 */

public class ActivityIceGroup extends AppCompatActivity implements GroupContract.View {

    private static final String TAG = "ActivityIceGroup";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ActivityIceGroupBinding iceGroupBinding;
    private GroupContract.Presenter presenter;
    private AppGroupVM appGroupVM;
    private int widgetsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iceGroupBinding = DataBindingUtil.setContentView(this, R.layout.activity_ice_group);

        Intent intent = getIntent();

        if (intent.getAction().equals(IceWidgets.LAUNCH_ACTION)) {

            widgetsId = intent.getIntExtra(IceWidgets.APP_WIDGETS_ID, IceWidgets.DEFAULT_APP_WIDGETS_ID);

            presenter = new GroupPresenter(this, new GroupRepository(), widgetsId);

            if (widgetsId == 0) {
                logd("APP_WIDGETS_ID is 0");
                finish();
            } else {
                appGroupVM = new AppGroupVM(widgetsId);
                iceGroupBinding.setVm(appGroupVM);
//                iceGroupBinding.setPresenter(presenter);
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
        if (presenter != null)
            presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.destroy();
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseActivityMessage(CloseIceGroupEvent event) {
        finish();
    }

    @Override
    public void setPresenter(GroupContract.Presenter p) {
        this.presenter = p;
    }

    @Override
    public void bindGroupItems(List<AppItem> items) {
        int columnCount = iceGroupBinding.appContainer.getColumnCount();
        iceGroupBinding.appContainer.removeAllViews();

        if (items.isEmpty())
            appGroupVM.isEmpty.set(true);
        else
            appGroupVM.isEmpty.set(false);

        for (int i = 1; i <= items.size(); i++) {
            AppItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(iceGroupBinding.appContainer.getContext()), R.layout.app_item, iceGroupBinding.appContainer, false);
            // change item margin
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) binding.getRoot().getLayoutParams();

            int padding = getResources().getDimensionPixelSize(R.dimen.app_item_padding);

            if (i % columnCount == 1) {
                params.setMargins(0, padding, padding, padding);
            } else if (i % columnCount == 0) {
                params.setMargins(padding, padding, 0, padding);
            } else {
                params.setMargins(padding, padding, padding, padding);
            }
            binding.getRoot().setLayoutParams(params);
            AppItemVM itemVM = AppItemConverter.item2VM(getPackageManager(), items.get(i - 1));
            binding.setVm(itemVM);

            iceGroupBinding.appContainer.addView(binding.getRoot());
        }
    }

    @Override
    public void bindGroupName(String name) {
        appGroupVM.groupName.set(name);
    }

    @Override
    public Context getCtx() {
        return this;
    }


}
