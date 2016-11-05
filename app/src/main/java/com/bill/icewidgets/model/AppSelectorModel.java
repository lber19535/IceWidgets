package com.bill.icewidgets.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bill.icewidgets.App;
import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.utils.AppInfoUtils;
import com.bill.icewidgets.vm.AppSelectorItemVM;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorModel {
    private static final String TAG = "AppSelectorModel";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private Realm realm;

    public List<AppSelectorItemVM> loadApp(Context context) {
        realm = Realm.getDefaultInstance();
        final PackageManager pm = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = AppInfoUtils.getAppInfos(pm);

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
}
