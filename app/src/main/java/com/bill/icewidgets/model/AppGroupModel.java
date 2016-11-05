package com.bill.icewidgets.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.bill.icewidgets.App;
import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.vm.AppItemVM;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by Bill on 2016/10/22.
 */

public class AppGroupModel {

    private static final String TAG = "AppGroupModel";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private int widgetsId;

    public AppGroupModel(int widgetsId) {
        this.widgetsId = widgetsId;
    }

    public List<AppItemVM> loadAppGroupInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<AppItem> appItems = realm.where(AppItem.class).equalTo("widgetsId", widgetsId).findAll();
        List<AppItemVM> items = new ArrayList<>();
        for (AppItem item : appItems) {
            if (DEBUG) {
                Log.d(TAG, "loadAppGroupInfo: item package name " + item.getPackageName());
                Log.d(TAG, "loadAppGroupInfo: item package name " + item.getWidgetsId());
                Log.d(TAG, "loadAppGroupInfo: item package name " + item.isFreezed());
                Log.d(TAG, "loadAppGroupInfo: item package name " + item.getItemType());
            }

            AppItemVM model = new AppItemVM();
            try {
                ApplicationInfo info = pm.getApplicationInfo(item.getPackageName(), PackageManager.GET_META_DATA);
                model.appIcon.set(info.loadIcon(pm));
                model.appName.set(info.loadLabel(pm).toString());
                model.isFrozen.set(!info.enabled);
                model.needFreeze.set((item.getItemType() & AppItem.ITEM_TYPE_FREEZE) != 0);
                model.setPackageName(item.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            items.add(model);
        }
        return items;
    }
}
