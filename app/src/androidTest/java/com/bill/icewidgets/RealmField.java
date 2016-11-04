package com.bill.icewidgets;

import io.realm.RealmObject;

/**
 * Created by Bill on 2016/11/3.
 */
public class RealmField<T> extends RealmObject {

    public T val;

    public RealmField() {
    }

    public RealmField(T val) {
        this.val = val;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
