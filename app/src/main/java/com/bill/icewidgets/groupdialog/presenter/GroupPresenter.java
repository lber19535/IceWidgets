package com.bill.icewidgets.groupdialog.presenter;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.groupdialog.GroupContract;
import com.bill.icewidgets.groupdialog.data.GroupRepository;
import com.bill.icewidgets.groupdialog.vm.AppItemVM;

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

    public GroupPresenter(GroupContract.View view, GroupRepository groupRepository, int widgetsId) {
        this.view = view;
        this.groupRepository = groupRepository;
        this.widgetsId = widgetsId;
    }

    public void loadGroup() {
        if (widgetsId <= 0)
            return;

        view.bindGroupName(groupRepository.getGroupName(widgetsId));

        List<AppItem> appItems = groupRepository.loadGroupItems(widgetsId);

        view.bindGroupItems(appItems);

        if (DEBUG) {
            for (AppItem item : appItems) {
                Log.d(TAG, "loadGroup: packageName " + item.getPackageName());
                Log.d(TAG, "loadGroup: item type " + item.getItemType());
                Log.d(TAG, "loadGroup: isFrozen " + item.isFreezed());
            }
        }


    }

    @Override
    public void start() {
        loadGroup();
    }

    @Override
    public void destroy() {

    }
}
