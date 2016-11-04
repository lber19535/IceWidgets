package com.bill.icewidgets;

import io.realm.RealmObject;

/**
 * Created by Bill on 2016/11/3.
 */
public class Dog extends RealmObject {

    public RealmField<String> id = new RealmField<>("");

    public RealmField<String> name = new RealmField<>("");

    public DogType type;


}
