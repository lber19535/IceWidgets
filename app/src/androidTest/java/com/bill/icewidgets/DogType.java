package com.bill.icewidgets;

import io.realm.RealmObject;

/**
 * Created by Bill on 2016/11/3.
 */
public class DogType extends RealmObject {

    public RealmField<String> id = new RealmField<>("");

    public RealmField<String> type = new RealmField<>("");
}
