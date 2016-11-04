package com.bill.icewidgets.vm;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.bill.icewidgets.App;
import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.components.service.FreezeService;
import com.bill.icewidgets.db.bean.AppItem;

import io.realm.Realm;

/**
 * Created by Bill on 2016/10/22.
 */

public class AppItemVM {

    private static final String TAG = "AppItemVM";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public final ObservableField<String> appName = new ObservableField<>();
    public final ObservableField<Drawable> appIcon = new ObservableField<>();
    public final ObservableBoolean isFrozen = new ObservableBoolean();
    public final ObservableBoolean needFreeze = new ObservableBoolean();

    private String packageName = "";
    private ActivityController controller;

    public String getPackageName() {
        return packageName;
    }

    public void launchApp(View v) {
        launchApp(v.getContext());
    }

    public void launchApp(Context context) {
        // reset app state in db
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AppItem item = realm.where(AppItem.class).equalTo("packageName", AppItemVM.this.packageName).findFirst();
                if (item != null)
                    item.setFreezed(false);
            }
        });
        // launch app
        FreezeService.launchApp(context, packageName);
        controller.finishActivity();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setActivityController(ActivityController controller) {
        this.controller = controller;
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static Drawable handleNeedFreeze(boolean needfreeze) {
        Resources res = App.getAppCtx().getResources();
        if (!needfreeze)
            return res.getDrawable(R.drawable.ic_ac_unit_green);
        else
            return res.getDrawable(R.drawable.ic_ac_unit_white);
    }
}
