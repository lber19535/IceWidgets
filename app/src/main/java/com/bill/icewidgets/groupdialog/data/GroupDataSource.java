package com.bill.icewidgets.groupdialog.data;

import com.bill.icewidgets.db.bean.AppItem;

import java.util.List;

/**
 * Created by Bill on 2016/12/16.
 */

public interface GroupDataSource {

    List<AppItem> loadGroupItems(int widgetsId);

    String getGroupName(int widgetsId);

    void setGroupName(int widgetsId, String name);
}
