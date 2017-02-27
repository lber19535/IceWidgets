package com.bill.icewidgets.appselector.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.content.pm.PackageManager.GET_DISABLED_COMPONENTS;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.MATCH_DISABLED_COMPONENTS;

/**
 * Created by Bill on 2016/12/9.
 */

public class AppRepository implements AppDataSource {

    private static final String TAG = "AppRepository";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private Context context;
    private Realm realm;

    public AppRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<AppSelectorItemVM> loadApp() {
        realm = Realm.getDefaultInstance();
        final PackageManager pm = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = getAppInfos(pm);

        final List<AppSelectorItemVM> models = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ApplicationInfo info : installedApplications) {
                    AppSelectorItemVM model = getItemModel(pm, info);
                    models.add(model);
                }
            }
        });
        realm.close();
        return models;
    }

    private AppSelectorItemVM getItemModel(PackageManager pm, ApplicationInfo info) {
        AppItem item = realm.where(AppItem.class).equalTo("packageName", info.packageName).findFirst();
        AppSelectorItemVM model = new AppSelectorItemVM();
        if (item == null) {
            // db bean
            item = realm.createObject(AppItem.class, info.packageName);
            item.setItemType(info.enabled ? AppItem.ITEM_TYPE_NONE : AppItem.ITEM_TYPE_FREEZE);
            item.setFreezed(!info.enabled);
            item.setWidgetsId(0);
        }
        //model
        model.disable.set(!info.enabled);
        model.groupName.set(getGroupName(item.getWidgetsId()));
        model.icon.set(info.loadIcon(pm));
        model.label.set(info.loadLabel(pm).toString());
        model.type.set(item.getItemType());
        model.pkgName.set(item.getPackageName());
        model.widgetsId.set(item.getWidgetsId());
        return model;
    }

    private String getGroupName(int widgetsId) {
        NameIdPair pair = realm.where(NameIdPair.class).equalTo("widgetsId", widgetsId).findFirst();
        return (pair == null || pair.getGroupName().isEmpty()) ? "" : pair.getGroupName();
    }

    private static List<ApplicationInfo> getAppInfos(PackageManager pm) {
        logd("method getAppInfos");
        List<ApplicationInfo> infos = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos;
        resolveInfos = pm.queryIntentActivities(intent, GET_META_DATA | MATCH_DISABLED_COMPONENTS);

        for (int i = 0; i < resolveInfos.size(); i++) {
            ActivityInfo info = resolveInfos.get(i).activityInfo;
            // if activity is alias activity, this value is false
            if (info.enabled) {
                infos.add(resolveInfos.get(i).activityInfo.applicationInfo);
            }
        }

        if (DEBUG) {
            ArrayList<ApplicationInfo> disableApps = new ArrayList<>();
            ArrayList<ApplicationInfo> enableApps = new ArrayList<>();
            for (int i = 0; i < resolveInfos.size(); i++) {
                ApplicationInfo info = resolveInfos.get(i).activityInfo.applicationInfo;
                if (info.enabled) {
                    enableApps.add(info);
                } else {
                    disableApps.add(info);
                }
            }
            logd("disable app list:");
            for (ApplicationInfo info : disableApps) {
                logd("package name is " + info.packageName);
            }
            logd("enable app list:");
            for (ApplicationInfo info : enableApps) {
                logd("package name is " + info.packageName);
            }
        }
        return infos;
    }

    public static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
