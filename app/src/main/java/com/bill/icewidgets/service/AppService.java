package com.bill.icewidgets.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bill.icewidgets.db.bean.AppItem;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 *
 */
public class AppService extends IntentService {

    private static final String TAG = "AppService";
    private static final boolean DEBUG = true;

    private static final String ACTION_NOTIFY_FREEZE = "com.bill.icewidgets.service.action.NOTIFY_FREEZE";
    private static final String ACTION_LAUNCH_APPS = "com.bill.icewidgets.action.LAUNCH_APPS";

    private static final String EXTRA_IS_FREEZE = "com.bill.icewidgets.service.extra.IS_FREEZE";
    private static final String EXTRA_PACKAGES = "ice.bill.com.icewidgets.extra.PACKAGES";

    public AppService() {
        super("AppService");
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

    public static void launchApp(Context context, CharSequence packageName) {
        Intent intent = new Intent(context, FreezeService.class);
        intent.setAction(ACTION_LAUNCH_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
                default:
                    logd(action + " is not defined");
                    break;

            }
        }
    }


    private void handleNotifyFreeze(final boolean isFreeze, final CharSequence... packageNames) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (CharSequence name : packageNames) {
                    AppItem item = realm.where(AppItem.class).equalTo("packageName", name.toString()).findFirst();
                    item.setFreezed(isFreeze);
                }
            }
        });
    }

    private void handleLaunchApp(final CharSequence packageName) {
        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);

                logd("unfreeze pkname " + packageName);
                String cmdstr = "pm enable " + packageName;

                Command cmd = new Command(0, cmdstr) {
                    @Override
                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        logd("handleUnfreezeApp output " + line);
                    }

                    @Override
                    public void commandTerminated(int id, String reason) {
                        super.commandTerminated(id, reason);
                        logd("handleFreezeApp terminated");
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void commandCompleted(int id, int exitcode) {
                        super.commandCompleted(id, exitcode);
                        logd("handleUnfreezeApp complete");
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        launchApp(packageName.toString());
                    }
                };

                shell.add(cmd);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootDeniedException e) {
                e.printStackTrace();
            }
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

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }


}
