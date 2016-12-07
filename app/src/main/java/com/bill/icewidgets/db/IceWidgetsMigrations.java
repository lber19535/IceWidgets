package com.bill.icewidgets.db;

import com.tencent.bugly.crashreport.CrashReport;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Reference https://realm.io/docs/java/latest/#migrations
 *
 * Created by Bill on 2016/12/7.
 */

public class IceWidgetsMigrations implements RealmMigration {

    /**
     * First version
     * <p>
     * {@link com.bill.icewidgets.db.bean.AppGroupInfo} <p>
     * {@link com.bill.icewidgets.db.bean.AppIcon} <p>
     * {@link com.bill.icewidgets.db.bean.AppItem} <p>
     * {@link com.bill.icewidgets.db.bean.NameIdPair} <p>
     */
    public static final long VERSION_1 = 1;    // current version
    public static final long VERSION_2 = 2;


    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        //
        if (oldVersion == 1) {

        } else if (oldVersion == 2) {

        } else {
            CrashReport.postCatchedException(new Throwable("real migration failed, old version is " + oldVersion + "new version is " + newVersion));
        }
    }
}
