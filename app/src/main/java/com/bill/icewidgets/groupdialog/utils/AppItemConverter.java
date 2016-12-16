package com.bill.icewidgets.groupdialog.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.groupdialog.vm.AppItemVM;

/**
 * Created by Bill on 2016/12/16.
 */

public class AppItemConverter {

    private static final String TAG = "AppItemConverter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static AppItemVM item2VM(PackageManager pm, AppItem item) {
        if (DEBUG) {
            Log.d(TAG, "loadAppGroupInfo: item package name " + item.getPackageName());
            Log.d(TAG, "loadAppGroupInfo: item widgets id " + item.getWidgetsId());
            Log.d(TAG, "loadAppGroupInfo: item is freeze " + item.isFreezed());
            Log.d(TAG, "loadAppGroupInfo: item type " + item.getItemType());
        }

        AppItemVM itemVM = new AppItemVM();
        try {
            ApplicationInfo info = pm.getApplicationInfo(item.getPackageName(), PackageManager.GET_META_DATA);
            itemVM.appIcon.set(info.loadIcon(pm));
            itemVM.appName.set(info.loadLabel(pm).toString());
            itemVM.isFrozen.set(!info.enabled);
            itemVM.needFreeze.set((item.getItemType() & AppItem.ITEM_TYPE_FREEZE) != 0);
            itemVM.setPackageName(item.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return itemVM;
    }
}
