package com.bill.icewidgets.utils;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.db.bean.AppItem;

import io.realm.Realm;

/**
 * Created by Bill on 2016/11/4.
 */
public class AppStatusUtils {
    private static final String TAG = "AppStatusUtils";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * set the appitem isfreezed value to true
     *
     * @param pkgNames
     */
    public static void setAppItemsFreezed(final CharSequence... pkgNames) {
        changeFreezedStatus(true, pkgNames);
    }

    /**
     * set the appitem isfreezed value to false
     *
     * @param pkgNames
     */
    public static void setAppItemsNotFreezed(CharSequence... pkgNames) {
        changeFreezedStatus(false, pkgNames);
    }

    private static void changeFreezedStatus(final boolean status, final CharSequence... pkgNames) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (CharSequence pkgName : pkgNames) {
                    AppItem appItem = realm.where(AppItem.class).equalTo("packageName", pkgName.toString()).findFirst();
                    appItem.setFreezed(status);
                }
            }
        });
    }

}
