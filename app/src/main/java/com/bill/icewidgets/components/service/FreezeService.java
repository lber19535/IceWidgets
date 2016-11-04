package com.bill.icewidgets.components.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bill.icewidgets.App;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FreezeService extends Service {

    private static final String TAG = "FreezeService";
    private static final boolean DEBUG = true;

    private static final String ACTION_FREEZE_APPS = "ice.bill.com.icewidgets.action.FREEZE_APPS";
    private static final String ACTION_UNFREEZE_APPS = "ice.bill.com.icewidgets.action.UNFREEZE_APPS";
    private static final String ACTION_LAUNCH_APPS = "ice.bill.com.icewidgets.action.LAUNCH_APPS";

    private static final String EXTRA_PACKAGES = "ice.bill.com.icewidgets.extra.PACKAGES";

    public FreezeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            logd("onStartCommand: action is " + action);
            switch (action) {
                case ACTION_FREEZE_APPS:
                    handleFreezeApp(intent.getCharSequenceArrayExtra(EXTRA_PACKAGES));
                    break;
                case ACTION_UNFREEZE_APPS:
                    handleUnfreezeApp(intent.getCharSequenceArrayExtra(EXTRA_PACKAGES));
                    break;
                case ACTION_LAUNCH_APPS:
                    handleLaunchApp(intent.getCharSequenceExtra(EXTRA_PACKAGES));
                    break;
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startFreezeApps(Context context, CharSequence... packageNames) {
        Intent intent = new Intent(context, FreezeService.class);
        intent.setAction(ACTION_FREEZE_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageNames);
        context.startService(intent);
    }

    public static void startUnfreezeApps(Context context, CharSequence... packageNames) {
        Intent intent = new Intent(context, FreezeService.class);
        intent.setAction(ACTION_UNFREEZE_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageNames);
        context.startService(intent);
    }

    public static void launchApp(Context context, CharSequence packageName) {
        Intent intent = new Intent(context, FreezeService.class);
        intent.setAction(ACTION_LAUNCH_APPS);
        intent.putExtra(EXTRA_PACKAGES, packageName);
        context.startService(intent);
    }

    private void handleFreezeApp(CharSequence... packageNames) {
        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);
                String[] cmds = new String[packageNames.length];

                for (int i = 0; i < packageNames.length; i++) {
                    logd("add freeze cmd for " + packageNames[i]);
                    cmds[i] = "pm disable " + packageNames[i];
                }
                Command cmd = new Command(0, cmds) {
                    @Override
                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        logd("handleFreezeApp output " + line);
                    }

                    @Override
                    public void commandTerminated(int id, String reason) {
                        super.commandTerminated(id, reason);
                        logd("handleFreezeApp terminated");
                    }

                    @Override
                    public void commandCompleted(int id, int exitcode) {
                        super.commandCompleted(id, exitcode);
                        logd("handleFreezeApp complete");
                        try {
                            RootTools.closeAllShells();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private void handleUnfreezeApp(CharSequence... packageNames) {
        if (RootTools.isRootAvailable()) {
            try {
                final Shell shell = RootTools.getShell(true);
                String[] cmds = new String[packageNames.length];

                for (int i = 0; i < packageNames.length; i++) {
                    logd("unfreeze pkname " + packageNames[i]);
                    cmds[i] = "pm enable " + packageNames[i];
                }
                Command cmd = new Command(0, cmds) {
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
        logd("launch package " + packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logd("unbind");
        return super.onUnbind(intent);
    }
}
