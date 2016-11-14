package com.bill.icewidgets;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.bill.icewidgets.components.service.ScreenService;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 * Created by Bill on 2016/10/22.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);

    }


}
