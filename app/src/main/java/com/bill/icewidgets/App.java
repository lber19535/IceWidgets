package com.bill.icewidgets;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.bill.icewidgets.db.IceWidgetsMigrations;
import com.bill.icewidgets.service.ScreenService;
import com.oasisfeng.condom.CondomContext;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Bill on 2016/10/22.
 */

public class App extends Application {

    private static String CONDOM_BUGLY_TAG = "Bugly";

    @Override
    public void onCreate() {
        super.onCreate();

        // realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(IceWidgetsMigrations.VERSION_2)
                .migration(new IceWidgetsMigrations())
                .build();
        Realm.setDefaultConfiguration(config);

        // vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // start screen listener service
        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);

        // init bugly
        if (BuildConfig.DEBUG) {
            // debug do not use bugly
        } else if (BuildConfig.PREVIEW) {
            CrashReport.initCrashReport(CondomContext.wrap(this, CONDOM_BUGLY_TAG), "eae17414d4", true);
            CrashReport.setIsDevelopmentDevice(this, true);
        } else {
            CrashReport.initCrashReport(CondomContext.wrap(this, CONDOM_BUGLY_TAG), "eae17414d4", false);
        }
    }


}
