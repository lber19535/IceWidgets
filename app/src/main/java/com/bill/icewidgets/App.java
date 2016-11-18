package com.bill.icewidgets;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.bill.icewidgets.components.service.ScreenService;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.Bugly;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 * Created by Bill on 2016/10/22.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // realm
        Realm.init(this);
        // debug stetho
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
        }

        // vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // start screen listener service
        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);

        // init bugly
        Bugly.init(getApplicationContext(), "eae17414d4", BuildConfig.DEBUG);

    }


}
