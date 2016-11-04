package com.bill.icewidgets.db.bean;

import com.bill.icewidgets.BuildConfig;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Bill on 2016/10/24.
 */
public class NameIdPair extends RealmObject {
    @Ignore
    private static final String TAG = "NameIdPair";
    @Ignore
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private int widgetsId;
    private String groupName = "";

    public int getWidgetsId() {
        return widgetsId;
    }

    public void setWidgetsId(int widgetsId) {
        this.widgetsId = widgetsId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
