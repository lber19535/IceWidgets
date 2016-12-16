package com.bill.icewidgets.groupdialog.vm;

import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.IceWidgets;
import com.bill.icewidgets.appselector.view.ActivityAppSelector;
import com.bill.icewidgets.common.events.CloseIceGroupEvent;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.service.AppService;
import com.bill.icewidgets.settings.ActivitySettings;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;


/**
 * Created by Bill on 2016/10/22.
 */

public class AppGroupVM extends Observable.OnPropertyChangedCallback {

    private static final String TAG = "AppGroupVM";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public final ObservableBoolean isEmpty = new ObservableBoolean();
    public final ObservableField<String> groupName = new ObservableField<>();
    private final int widgetsId;

    public AppGroupVM(int widgetsId) {
        this.widgetsId = widgetsId;
        groupName.addOnPropertyChangedCallback(this);
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int id = widgetsId;
                NameIdPair widgetsId = realm.where(NameIdPair.class).equalTo("widgetsId", id).findFirst();
                widgetsId.setGroupName(groupName.get());
            }
        });
        realm.close();
    }

    public void onClickAdd(View v) {
        Intent intent = new Intent(v.getContext(), ActivityAppSelector.class);
        intent.putExtra(IceWidgets.APP_WIDGETS_ID, widgetsId);
        v.getContext().startActivity(intent);

    }

    public void onClickSettings(View v) {
        Intent intent = new Intent(v.getContext(), ActivitySettings.class);
        v.getContext().startActivity(intent);
    }

    public void onFreezeClick(View v) {
        freezeApp(v.getContext());
    }

    private void freezeApp(Context context) {

        AppService.freezeGroup(context, widgetsId);
        // close dialog
        EventBus.getDefault().post(new CloseIceGroupEvent());
    }

    public void closeWindow(View v){
        EventBus.getDefault().post(new CloseIceGroupEvent());
    }
}
