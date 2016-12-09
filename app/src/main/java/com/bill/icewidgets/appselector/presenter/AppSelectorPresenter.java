package com.bill.icewidgets.appselector.presenter;

import android.view.View;

import com.bill.icewidgets.IceWidgets;
import com.bill.icewidgets.appselector.AppSelectorContract;
import com.bill.icewidgets.appselector.data.AppDataSource;
import com.bill.icewidgets.appselector.data.AppRepository;
import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;
import com.bill.icewidgets.common.events.CloseSelectorEvent;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.service.FreezeService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;

/**
 * Created by Bill on 2016/12/9.
 */

public class AppSelectorPresenter implements AppSelectorContract.Presenter {

    private AppSelectorContract.View view;
    private AppDataSource repository;

    private List<AppSelectorItemVM> items;
    private List<AppSelectorItemVM> freezeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> unfreezeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> removeItemsVms = new ArrayList<>();
    private List<AppSelectorItemVM> addItemsVms = new ArrayList<>();
    private int widgetsId;


    public AppSelectorPresenter(AppSelectorContract.View view, AppDataSource repository, int widgetsId) {
        this.view = view;
        this.widgetsId = widgetsId;
        this.repository = repository;
    }

    @Override
    public void start() {
        loadAppsAsync();
    }

    private void loadAppsAsync() {
        Task.callInBackground(new Callable<List<AppSelectorItemVM>>() {
            @Override
            public List<AppSelectorItemVM> call() throws Exception {
                return repository.loadApp();
            }
        }).continueWith(new Continuation<List<AppSelectorItemVM>, Void>() {
            @Override
            public Void then(Task<List<AppSelectorItemVM>> task) throws Exception {
                items = task.getResult();

                view.updateList(items);

                // stop progress
                view.stopProgress();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    @Override
    public void destroy() {
        Task.cancelled();
    }

    /**
     * save or remove selected item for freeze
     *
     * @param item current selected item's view model
     */
    @Override
    public void changeFreezeItems(AppSelectorItemVM item) {
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
    @Override
    public void changeAddItems(AppSelectorItemVM item) {
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

    @Override
    public void postSaveFreeze(View v) {
        Realm realm = Realm.getDefaultInstance();
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

            FreezeService.startFreezeApps(v.getContext(), pkgNames.toArray(new CharSequence[pkgNames.size()]));
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
                FreezeService.startUnfreezeApps(v.getContext(), pkgNames.toArray(new CharSequence[pkgNames.size()]));
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
        realm.close();

        EventBus.getDefault().post(new CloseSelectorEvent());
    }


}
