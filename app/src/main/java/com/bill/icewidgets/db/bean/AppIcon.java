package com.bill.icewidgets.db.bean;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Bill on 2016/12/2.
 */

public class AppIcon extends RealmObject {

    @Ignore
    public static final String COLUMN_WIDGETS_ID = "widgetsId";
    @Ignore
    public static final String COLUMN_ICON_TYPE = "iconType";
    @Ignore
    public static final int ICON_TYPE_ORIGINAL = 1;
    @Ignore
    public static final int ICON_TYPE_PKG_GEN = 2;
    @Ignore
    public static final int ICON_TYPE_CUSTOMIZE = 3;

    private int widgetsId;
    private int iconType = ICON_TYPE_ORIGINAL;

    public int getWidgetsId() {
        return widgetsId;
    }

    public void setWidgetsId(int widgetsId) {
        this.widgetsId = widgetsId;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }
}
