package com.bill.icewidgets.groupdialog.data;

import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Bill on 2016/12/16.
 */

public class GroupRepository implements GroupDataSource {



    @Override
    public List<AppItem> loadGroupItems(int widgetsId) {
        Realm realm = Realm.getDefaultInstance();

        List<AppItem> items = realm.copyFromRealm(realm
                .where(AppItem.class)
                .equalTo("widgetsId", widgetsId)
                .findAll());

        realm.close();
        return items;
    }

    @Override
    public String getGroupName(int widgetsId) {
        Realm realm = Realm.getDefaultInstance();

        NameIdPair pair = realm.copyFromRealm(realm
                .where(NameIdPair.class)
                .equalTo("widgetsId", widgetsId)
                .findFirst());

        realm.close();
        return pair.getGroupName();
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
