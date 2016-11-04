package com.bill.icewidgets.db.bean;

import com.bill.icewidgets.model.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Bill on 2016/10/21.
 */

public class AppGroupInfo extends RealmObject {

    private int appWidgetsId = 0;

    private RealmList<RealmString> packageNames = new RealmList<>();

    public AppGroupInfo() {
    }

    public RealmList<RealmString> getPackageNames() {
        return packageNames;
    }

    public int getAppWidgetsId() {
        return appWidgetsId;
    }

    public void setAppWidgetsId(int appWidgetsId) {
        this.appWidgetsId = appWidgetsId;
    }

}
