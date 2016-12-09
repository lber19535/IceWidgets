package com.bill.icewidgets.appselector.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.utils.AppInfoUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Bill on 2016/12/9.
 */

public class AppRepository implements AppDataSource {

    private Context context;
    private Realm realm;

    public AppRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<AppSelectorItemVM> loadApp() {
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
}
