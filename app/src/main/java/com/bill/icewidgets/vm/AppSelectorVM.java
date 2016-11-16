package com.bill.icewidgets.vm;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.components.IceWidgets;
import com.bill.icewidgets.components.service.FreezeService;
import com.bill.icewidgets.databinding.ActivityAppSelectorBinding;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.model.AppSelectorModel;
import com.bill.icewidgets.ui.adapter.AppSelectorAdapter;
import com.bill.icewidgets.ui.events.CloseSelectorEvent;
import com.bill.icewidgets.ui.listener.OnRVItemClickListener;
import com.bill.icewidgets.ui.listener.OnRVItemLongClickListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorVM implements VM, OnRVItemLongClickListener, OnRVItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = "AppSelectorVM";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ActivityAppSelectorBinding binding;
    private AppSelectorModel model;
    private Realm realm;
    private List<AppSelectorItemVM> items;
    private List<AppSelectorItemVM> freezeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> unfreezeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> removeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> addItemsVms = new ArrayList<>();
    private int widgetsId;

    private AppSelectorAdapter adapter;

    public final ObservableInt progressVisibility = new ObservableInt(View.VISIBLE);
    public final ObservableBoolean progressIndeterminate = new ObservableBoolean(true);

    public AppSelectorVM(ActivityAppSelectorBinding binding, int widgetsId) {
        this.binding = binding;
        this.model = new AppSelectorModel();
        this.realm = Realm.getDefaultInstance();
        this.widgetsId = widgetsId;
    }

    public void loadAppsAsync() {

        Task.callInBackground(new Callable<List<AppSelectorItemVM>>() {
            @Override
            public List<AppSelectorItemVM> call() throws Exception {
                return model.loadApp(binding.getRoot().getContext());
            }
        }).continueWith(new Continuation<List<AppSelectorItemVM>, Void>() {
            @Override
            public Void then(Task<List<AppSelectorItemVM>> task) throws Exception {
                items = task.getResult();
                if (adapter != null) {
                    adapter.replaceAll(items);
                } else {
                    adapter = new AppSelectorAdapter(items);
                }
                binding.allList.setAdapter(adapter);
                binding.allList.setHasFixedSize(true);
                adapter.setOnClickListener(AppSelectorVM.this);
                adapter.setOnLongClickListener(AppSelectorVM.this);
                binding.allList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                // stop progress
                progressIndeterminate.set(false);
                progressVisibility.set(View.GONE);
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }

    public void showAllItems() {
        adapter.showAll();
    }

    public void onFabClick(View v) {

        if (!freezeItemsVms.isEmpty()) {
            final ArrayList<CharSequence> pkgNames = new ArrayList<>();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (AppSelectorItemVM model : freezeItemsVms) {
                        AppItem item = realm.where(AppItem.class).equalTo("packageName", model.pkgName.get()).findFirst();
                        item.setWidgetsId(widgetsId);
                        item.setItemType(model.type.get());

                        pkgNames.add(item.getPackageName());

                    }
                }
            });

            FreezeService.startFreezeApps(binding.getRoot().getContext(), pkgNames.toArray(new CharSequence[pkgNames.size()]));
        }

        if (!unfreezeItemsVms.isEmpty()) {
            final ArrayList<CharSequence> pkgNames = new ArrayList<>();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (AppSelectorItemVM model : unfreezeItemsVms) {
                        if (!model.disable.get()) {
                            continue;
                        }
                        AppItem item = realm.where(AppItem.class).equalTo("packageName", model.pkgName.get()).findFirst();
                        item.setWidgetsId(IceWidgets.DEFAULT_APP_WIDGETS_ID);
                        item.setItemType(model.type.get());

                        pkgNames.add(item.getPackageName());

                    }
                }
            });
            if (!pkgNames.isEmpty()) {
                FreezeService.startUnfreezeApps(binding.getRoot().getContext(), pkgNames.toArray(new CharSequence[pkgNames.size()]));
            }
        }

        if (!addItemsVms.isEmpty()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (AppSelectorItemVM vm : addItemsVms) {
                        AppItem item = realm.where(AppItem.class).equalTo("packageName", vm.pkgName.get()).findFirst();
                        item.setWidgetsId(widgetsId);
                        item.setItemType(vm.type.get());
                    }
                }
            });
        }

        if (!removeItemsVms.isEmpty()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (AppSelectorItemVM vm : removeItemsVms) {
                        AppItem item = realm.where(AppItem.class).equalTo("packageName", vm.pkgName.get()).findFirst();
                        item.setWidgetsId(IceWidgets.DEFAULT_APP_WIDGETS_ID);
                        item.setItemType(vm.type.get());
                    }
                }
            });
        }

        EventBus.getDefault().post(new CloseSelectorEvent());
    }

    @Override
    public void destroy() {
        Task.cancelled();
    }

    @Override
    public void onItemClick(View v, final int position) {
        AppSelectorItemVM item = adapter.getItems().get(position);

        changeFreezeItems(item);

    }

    @Override
    public void onItemLongClick(View v, int position) {
        AppSelectorItemVM item = adapter.getItems().get(position);

        changeAddItems(item);

    }

    /**
     * save or remove selected item for freeze
     *
     * @param item current selected item's view model
     */
    private void changeFreezeItems(AppSelectorItemVM item) {
        if (item.type.get() == AppItem.ITEM_TYPE_NONE) {
            item.type.set(AppItem.ITEM_TYPE_ADD | AppItem.ITEM_TYPE_FREEZE);
            freezeItemsVms.add(item);
            unfreezeItemsVms.remove(item);
        } else if ((item.type.get() & AppItem.ITEM_TYPE_FREEZE) != 0) {
            item.type.set(AppItem.ITEM_TYPE_NONE);
            freezeItemsVms.remove(item);
            unfreezeItemsVms.add(item);
        }
    }

    /**
     * save or remove selected item for add
     *
     * @param item current selected item's view model
     */
    private void changeAddItems(AppSelectorItemVM item) {
        if (item.type.get() == AppItem.ITEM_TYPE_NONE) {
            item.type.set(AppItem.ITEM_TYPE_ADD);
            addItemsVms.add(item);
            removeItemsVms.remove(item);
        } else if ((item.type.get() & AppItem.ITEM_TYPE_ADD) != 0) {
            item.type.set(AppItem.ITEM_TYPE_NONE);
            addItemsVms.remove(item);
            removeItemsVms.add(item);
        }
    }

    @BindingAdapter({"matProg_progressIndeterminate"})
    public static void progressIndeterminate(View view, boolean spin) {
        ProgressWheel progressWheel = (ProgressWheel) view;
        if (spin && !progressWheel.isSpinning()) {
            progressWheel.spin();
        } else {
            progressWheel.stopSpinning();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: query " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: newText is " + newText);
        adapter.getFilter().filter(newText);
        return true;
    }

}
