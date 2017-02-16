package com.bill.icewidgets.db.bean;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bill on 2016/10/26.
 */
public class AppItem extends RealmObject {

    /**
     * add + !disable   0001
     * add + disable    0011
     * !add + !disable   0000
     */
    @Ignore
    public static final int ITEM_TYPE_ADD = 1 << 0;          // 0001
    @Ignore
    public static final int ITEM_TYPE_FREEZE = 1 << 1;     // 0010
    @Ignore
    public static final int ITEM_TYPE_NONE = 0;             // 0000

    @Index
    @PrimaryKey
    private String packageName;
    private int widgetsId;
    private boolean isFreezed;
    private int itemType;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getWidgetsId() {
        return widgetsId;
    }

    public void setWidgetsId(int widgetsId) {
        this.widgetsId = widgetsId;
    }

    public boolean isFreezed() {
        return isFreezed;
    }

    public void setFreezed(boolean freezed) {
        isFreezed = freezed;
    }
}
