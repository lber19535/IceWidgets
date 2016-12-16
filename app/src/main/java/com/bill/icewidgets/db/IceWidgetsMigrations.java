package com.bill.icewidgets.db;

import com.tencent.bugly.crashreport.CrashReport;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Reference https://realm.io/docs/java/latest/#migrations
 * <p>
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
     * com.bill.icewidgets.model.RealmString <p>
     */
    public static final long VERSION_1 = 1;    // current version
    /**
     * First version
     * <p>
     * {@link com.bill.icewidgets.db.bean.AppGroupInfo} <p>
     * {@link com.bill.icewidgets.db.bean.AppIcon} <p>
     * {@link com.bill.icewidgets.db.bean.AppItem} <p>
     * {@link com.bill.icewidgets.db.bean.NameIdPair} <p>
     * {@link com.bill.icewidgets.db.bean.RealmString} <p>
     */
    public static final long VERSION_2 = 1;


    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        // move RealmString to db package
        if (oldVersion == 1) {
//            schema.remove("com.bill.icewidgets.model.RealmString");
            schema.create("RealmString")
                    .addField("val", String.class);

            schema.create("AppIcon")
                    .addField("widgetsId",Integer.class)
                    .addField("iconType",Integer.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            oldVersion++;
        }

        if (oldVersion != newVersion) {
            CrashReport.postCatchedException(new Throwable("real migration failed, old version is " + oldVersion + "new version is " + newVersion));
        }
    }
}
