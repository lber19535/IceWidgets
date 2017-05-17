package com.bill.icewidgets.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.bill.icewidgets.R;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.service.tools.CallbackCommand;
import com.bill.icewidgets.utils.ToastUtils;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Bill on 2016/11/14.
 */
public class AppService extends CountDownService {

    private static final String TAG = "AppService";
    private static final boolean DEBUG = true;

    private static final String ACTION_NOTIFY_FREEZE = "com.bill.icewidgets.service.action.NOTIFY_FREEZE";
    private static final String ACTION_LAUNCH_APPS = "com.bill.icewidgets.action.LAUNCH_APPS";
    public static final String ACTION_FREEZE_APPS = "com.bill.icewidgets.action.FREEZE_APPS";
    public static final String ACTION_UNFREEZE_APPS = "com.bill.icewidgets.action.UNFREEZE_APPS";
    private static final String ACTION_FREEZE_GROUP = "com.bill.icewidgets.action.FREEZE_GROUP";

    private static final String EXTRA_IS_FREEZE = "com.bill.icewidgets.service.extra.IS_FREEZE";
    public static final String EXTRA_PACKAGES = "com.bill.com.icewidgets.extra.PACKAGES";
    private static final String EXTRA_WIDGETS_ID = "com.bill.com.icewidgets.extra.WIDGETS_ID";


    private static final int INVALID_WIDGETS_ID = -1;

    public AppService() {
        super(TAG);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * notify db to change the app freeze value
     *
     * @param context
     * @param isFreeze     whether freeze
     * @param packageNames changed package
     */
    public static void notifyAppFreeze(Context context, boolean isFreeze, CharSequence... packageNames) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_NOTIFY_FREEZE);
        intent.putExtra(EXTRA_IS_FREEZE, isFreeze);
        intent.putExtra(EXTRA_PACKAGES, packageNames);
        context.startService(intent);
    }

    /**
     * This method will auto unfreeze and launch the app
     *
     * @param context
     * @param packageName
     */
    public static void launchApp(Context context, CharSequence packageName) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_LAUNCH_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageName);
        context.startService(intent);
    }


    /**
     * Freeze the app in widgetsId
     *
     * @param context
     * @param widgetsId
     */
    public static void freezeGroup(Context context, int widgetsId) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_FREEZE_GROUP);
        intent.putExtra(EXTRA_WIDGETS_ID, widgetsId);
        context.startService(intent);
    }

    /**
     * Start to freeze the apps.
     * <p>
     * This service will send broadcast when freeze action is finish,
     * you can use IntentFilter(ACTION_FREEZE_APPS) to receive it.
     * the Extra name EXTRA_PACKAGES contains an app's package
     * names.
     *
     * @param context
     * @param packageNames
     */
    public static void startFreezeApps(Context context, CharSequence... packageNames) {
        if (isPackagesNameEmpty(packageNames)) {
            return;
        }
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_FREEZE_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageNames);
        context.startService(intent);
    }

    /**
     * Start to unfreeze the apps.
     * <p>
     * This service will send broadcast when unfreeze action is finish,
     * you can use IntentFilter(ACTION_UNFREEZE_APPS) to receive it,
     * the Extra name is EXTRA_PACKAGES contains an app's package
     * names.
     *
     * @param context
     * @param packageNames
     */
    public static void startUnfreezeApps(Context context, CharSequence... packageNames) {
        if (isPackagesNameEmpty(packageNames)) {
            return;
        }
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_UNFREEZE_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageNames);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_NOTIFY_FREEZE: {
                    handleNotifyFreeze(
                            intent.getBooleanExtra(EXTRA_IS_FREEZE, false),
                            intent.getCharSequenceArrayExtra(EXTRA_PACKAGES));
                    break;
                }
                case ACTION_LAUNCH_APPS:
                    handleLaunchApp(intent.getCharSequenceExtra(EXTRA_PACKAGES));
                    break;
                case ACTION_FREEZE_APPS:
                    handleFreezeApp(intent.getCharSequenceArrayExtra(EXTRA_PACKAGES));
                    break;
                case ACTION_UNFREEZE_APPS:
                    handleUnfreezeApp(intent.getCharSequenceArrayExtra(EXTRA_PACKAGES));
                    break;
                case ACTION_FREEZE_GROUP:
                    handleFreezeGroup(intent.getIntExtra(EXTRA_WIDGETS_ID, INVALID_WIDGETS_ID));
                    break;
                default:
                    logd(action + " is not defined");
                    break;

            }
        }
    }

    private void handleFreezeGroup(int widgetsId) {
        if (widgetsId == INVALID_WIDGETS_ID) {
            logd("handleFreezeGroup INVALID_WIDGETS_ID");
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        RealmResults<AppItem> items = realm.where(AppItem.class).beginGroup()
                .equalTo("widgetsId", widgetsId)
                .equalTo("isFreezed", false)
                .equalTo("itemType", AppItem.ITEM_TYPE_ADD | AppItem.ITEM_TYPE_FREEZE)
                .endGroup().findAll();
        
        int size = items.size();
        String[] pkgs = new String[size];
        for (int i = 0; i < size; i++) {
            pkgs[i] = items.get(i).getPackageName();
            if (DEBUG) {
                logd("handleFreezeGroup package name " + pkgs[i]);
            }
        }
        logd("handleFreezeGroup size is " + size);

        handleFreezeApp(pkgs);
        handleNotifyFreeze(true, pkgs);

        realm.close();

    }


    private void handleNotifyFreeze(final boolean isFreeze, final CharSequence... packageNames) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (CharSequence name : packageNames) {
                    AppItem item = realm.where(AppItem.class).equalTo("packageName", name.toString()).findFirst();
                    if (item != null)
                        item.setFreezed(isFreeze);
                }
            }
        });
        realm.close();


        handleNotifyFreezeBroadcast(isFreeze, packageNames);
    }

    private void handleNotifyFreezeBroadcast(final boolean isFreeze, final CharSequence... packageNames) {
        Intent intent = new Intent();
        if (isFreeze) {
            intent.setAction(ACTION_FREEZE_APPS);
            intent.putExtra(EXTRA_PACKAGES, packageNames);
        } else {
            intent.setAction(ACTION_UNFREEZE_APPS);
            intent.putExtra(EXTRA_PACKAGES, packageNames);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleLaunchApp(final CharSequence packageName) {
        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);

                logd("unfreeze pkname " + packageName);
                String cmdstr = "pm enable " + packageName;

                Command cmd = new CallbackCommand(0, new CallbackCommand.Callback() {
                    @Override
                    public void onCallback(String line) {
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        launchApp(packageName.toString());
                    }
                }, cmdstr);

                shell.add(cmd);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootDeniedException e) {
                e.printStackTrace();
                toastRootPermissionDenied();
            }
        } else {
            toastNotRoot();
        }
    }

    private void launchApp(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            logd("package " + packageName + " can not be launched");
            return;
        }
        // notify change app freeze
        AppService.notifyAppFreeze(this, false, packageName);

        logd("launch package " + packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    private void handleFreezeApp(final CharSequence... packageNames) {

        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);
                String[] cmds = new String[packageNames.length];

                for (int i = 0; i < packageNames.length; i++) {
                    logd("add freeze cmd for " + packageNames[i]);
                    cmds[i] = "pm disable " + packageNames[i];
                }

                Command cmd = new CallbackCommand(0, new CallbackCommand.Callback() {
                    @Override
                    public void onCallback(String line) {
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        AppService.notifyAppFreeze(AppService.this, true, packageNames);
                    }
                }, cmds);

                shell.add(cmd);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootDeniedException e) {
                e.printStackTrace();
                toastRootPermissionDenied();
            }
        } else {
            toastNotRoot();
        }
    }

    private void handleUnfreezeApp(final CharSequence... packageNames) {

        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);
                String[] cmds = new String[packageNames.length];

                for (int i = 0; i < packageNames.length; i++) {
                    logd("unfreeze pkname " + packageNames[i]);
                    cmds[i] = "pm enable " + packageNames[i];
                }

                Command cmd = new CallbackCommand(0, new CallbackCommand.Callback() {
                    @Override
                    public void onCallback(String line) {
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        AppService.notifyAppFreeze(AppService.this, false, packageNames);
                    }
                }, cmds);

                shell.add(cmd);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootDeniedException e) {
                e.printStackTrace();
                toastRootPermissionDenied();
            }
        } else {
            toastNotRoot();
        }
    }

    private void toastNotRoot() {
        ToastUtils.longToast(this, R.string.app_service_not_root);
    }

    private void toastRootPermissionDenied() {
        ToastUtils.longToast(this, R.string.app_service_root_deny);
    }

    private static boolean isPackagesNameEmpty(CharSequence... packageNames) {
        if (packageNames.length == 0) {
            logd("package name array is empty");
            return true;
        }
        return false;
    }

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }


}
