package com.bill.icewidgets.vm;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.IceWidgets;
import com.bill.icewidgets.R;
import com.bill.icewidgets.appselector.view.ActivityAppSelector;
import com.bill.icewidgets.common.events.CloseIceGroupEvent;
import com.bill.icewidgets.databinding.ActivityIceGroupBinding;
import com.bill.icewidgets.databinding.AppItemBinding;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.model.AppGroupManager;
import com.bill.icewidgets.model.AppGroupModel;
import com.bill.icewidgets.service.AppService;
import com.bill.icewidgets.service.UpdateWidgetsService;
import com.bill.icewidgets.settings.ActivitySettings;
import com.bill.icewidgets.utils.AppStatusUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


/**
 * Created by Bill on 2016/10/22.
 */

public class AppGroupVM extends Observable.OnPropertyChangedCallback implements VM {

    private static final String TAG = "AppGroupVM";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ActivityIceGroupBinding iceGroupBinding;
    private AppGroupModel appGroupModel;
    private final int widgetsId;
    private List<AppItemVM> itemVMs;

    public final ObservableBoolean isEmpty = new ObservableBoolean();
    public final ObservableField<String> groupName = new ObservableField<>();

    private Realm realm;

    public AppGroupVM(ActivityIceGroupBinding iceGroupBinding, int widgetsId) {
        this.iceGroupBinding = iceGroupBinding;
        this.widgetsId = widgetsId;

        groupName.addOnPropertyChangedCallback(this);
        realm = Realm.getDefaultInstance();

    }

    public void loadGroup(int widgetsId) {
        NameIdPair pair = realm.where(NameIdPair.class).equalTo("widgetsId", widgetsId).findFirst();
        if (pair.getGroupName().isEmpty()) {
            groupName.set("");
        } else {
            groupName.set(pair.getGroupName());
        }

        appGroupModel = AppGroupManager.getGroupModel(widgetsId);

        itemVMs = appGroupModel.loadAppGroupInfo(iceGroupBinding.getRoot().getContext());

        if (DEBUG) {
            for (AppItemVM vm : itemVMs) {
                Log.d(TAG, "loadGroup: packageName " + vm.getPackageName());
                Log.d(TAG, "loadGroup: need freeze " + vm.needFreeze.get());
                Log.d(TAG, "loadGroup: isFrozen " + vm.isFrozen.get());
            }
        }

        int columnCount = iceGroupBinding.appContainer.getColumnCount();
        iceGroupBinding.appContainer.removeAllViews();

        if (itemVMs.isEmpty())
            isEmpty.set(true);
        else
            isEmpty.set(false);

        for (int i = 1; i <= itemVMs.size(); i++) {
            AppItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(iceGroupBinding.appContainer.getContext()), R.layout.app_item, iceGroupBinding.appContainer, false);
            // change item margin
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) binding.getRoot().getLayoutParams();
            Resources res = iceGroupBinding.getRoot().getResources();
            int padding = res.getDimensionPixelSize(R.dimen.app_item_padding);

            if (i % columnCount == 1) {
                params.setMargins(0, padding, padding, padding);
            } else if (i % columnCount == 0) {
                params.setMargins(padding, padding, 0, padding);
            } else {
                params.setMargins(padding, padding, padding, padding);
            }
            binding.getRoot().setLayoutParams(params);
            AppItemVM itemVM = itemVMs.get(i - 1);
            binding.setVm(itemVM);

            iceGroupBinding.appContainer.addView(binding.getRoot());
        }
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

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    @Override
    public void destroy() {
        groupName.removeOnPropertyChangedCallback(this);
        realm.close();
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int id = widgetsId;
                NameIdPair widgetsId = realm.where(NameIdPair.class).equalTo("widgetsId", id).findFirst();
                widgetsId.setGroupName(groupName.get());
            }
        });
    }

    public void onFreezeClick(View v) {
        freezeApp();
    }

    private void freezeApp() {

        AppStatusUtils.setAppItemsFreezed();
        ArrayList<CharSequence> pkgs = new ArrayList<>();
        for (AppItemVM vm : itemVMs) {
            if (vm.needFreeze.get()) {
                pkgs.add(vm.getPackageName());
            }
        }
        AppService.startFreezeApps(iceGroupBinding.getRoot().getContext(), pkgs.toArray(new CharSequence[pkgs.size()]));

        closeWindow(null);
    }

    public void closeWindow(View v) {

        UpdateWidgetsService.updateWidgetsName(iceGroupBinding.title.getContext(), widgetsId, iceGroupBinding.title.getText());
        EventBus.getDefault().post(new CloseIceGroupEvent());
    }


}
