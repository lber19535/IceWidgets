package com.bill.icewidgets.groupdialog.presenter;

import android.content.IntentFilter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.groupdialog.FreezeStatusReceiver;
import com.bill.icewidgets.groupdialog.GroupContract;
import com.bill.icewidgets.groupdialog.data.GroupRepository;
import com.bill.icewidgets.groupdialog.vm.AppItemVM;
import com.bill.icewidgets.service.AppService;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Bill on 2016/12/16.
 */

public class GroupPresenter implements GroupContract.Presenter {

    private static final String TAG = "GroupPresenter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private GroupContract.View view;
    private GroupRepository groupRepository;
    private final int widgetsId;
    private FreezeStatusReceiver freezeStatusReceiver;
    private List<AppItem> appItems;

    public GroupPresenter(GroupContract.View view, GroupRepository groupRepository, int widgetsId) {
        this.view = view;
        this.groupRepository = groupRepository;
        this.widgetsId = widgetsId;
    }

    public void loadGroup() {
        if (widgetsId <= 0)
            return;

        view.bindGroupName(groupRepository.getGroupName(widgetsId));

        appItems = groupRepository.loadGroupItems(widgetsId);

        view.bindGroupItems(appItems);

        if (DEBUG) {
            for (AppItem item : appItems) {
                Log.d(TAG, "loadGroup: packageName " + item.getPackageName());
                Log.d(TAG, "loadGroup: item type " + item.getItemType());
                Log.d(TAG, "loadGroup: isFrozen " + item.isFreezed());
            }
        }
    }

    private void registerFreezeStatusReceiver() {
        if (!appItems.isEmpty()) {
            freezeStatusReceiver = new FreezeStatusReceiver(appItems);
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppService.ACTION_FREEZE_APPS);
            filter.addAction(AppService.ACTION_UNFREEZE_APPS);
            LocalBroadcastManager.getInstance(view.getCtx()).registerReceiver(freezeStatusReceiver, filter);
        }
    }

    @Override
    public void start() {
        loadGroup();
        registerFreezeStatusReceiver();
    }

    @Override
    public void destroy() {
        LocalBroadcastManager.getInstance(view.getCtx()).unregisterReceiver(freezeStatusReceiver);

    }
}
