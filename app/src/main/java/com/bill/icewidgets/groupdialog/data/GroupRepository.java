package com.bill.icewidgets.groupdialog.data;

import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Bill on 2016/12/16.
 */

public class GroupRepository implements GroupDataSource {



    @Override
    public List<AppItem> loadGroupItems(int widgetsId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<AppItem> appItems = realm.where(AppItem.class).equalTo("widgetsId", widgetsId).findAll();
        List<AppItem> items = new ArrayList<>(appItems);
        realm.close();
        return items;
    }

    @Override
    public String getGroupName(int widgetsId) {
        Realm realm = Realm.getDefaultInstance();
        final NameIdPair pair = realm.where(NameIdPair.class).equalTo("widgetsId", widgetsId).findFirst();
        String name = pair.getGroupName();
        realm.close();
        return name;
    }

    @Override
    public void setGroupName(final int widgetsId, final String name) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                NameIdPair pair = realm.where(NameIdPair.class).equalTo("widgetsId", widgetsId).findFirst();
                pair.setGroupName(name);
            }
        });
        realm.close();
    }


}
