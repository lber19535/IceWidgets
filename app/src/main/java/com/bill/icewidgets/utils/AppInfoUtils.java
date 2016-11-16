package com.bill.icewidgets.utils;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.GET_DISABLED_COMPONENTS;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.MATCH_DISABLED_COMPONENTS;

/**
 * Created by Bill on 2016/10/22.
 */

public class AppInfoUtils {

    private static final String TAG = "AppInfoUtils";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static List<ApplicationInfo> getAppInfos(PackageManager pm) {
        logd("method getAppInfos");
        List<ApplicationInfo> infos = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            resolveInfos = pm.queryIntentActivities(intent, GET_META_DATA | MATCH_DISABLED_COMPONENTS);
        }else {
            resolveInfos = pm.queryIntentActivities(intent, GET_META_DATA | GET_DISABLED_COMPONENTS);
        }

        for (int i = 0; i < resolveInfos.size(); i++) {
            infos.add(resolveInfos.get(i).activityInfo.applicationInfo);
        }

        if (DEBUG) {
            ArrayList<ApplicationInfo> disableApps = new ArrayList<>();
            ArrayList<ApplicationInfo> enableApps = new ArrayList<>();
            for (int i = 0; i < resolveInfos.size(); i++) {
                ApplicationInfo info = resolveInfos.get(i).activityInfo.applicationInfo;
                if (info.enabled) {
                    enableApps.add(info);
                } else {
                    disableApps.add(info);
                }
            }
            logd("disable app list:");
            for (ApplicationInfo info : disableApps) {
                logd("package name is " + info.packageName);
            }
            logd("enable app list:");
            for (ApplicationInfo info : enableApps) {
                logd("package name is " + info.packageName);
            }
        }
        return infos;
    }


    public static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
