package com.bill.icewidgets.model;

import android.util.SparseArray;

import com.bill.icewidgets.BuildConfig;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppGroupManager {
    private static final String TAG = "AppGroupManager";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final SparseArray<AppGroupModel> groupModels = new SparseArray<>();

    public static AppGroupModel getGroupModel(int widgetsId) {
        AppGroupModel model = groupModels.get(widgetsId);
        if (model == null) {
            model = new AppGroupModel(widgetsId);
        }
        return model;
    }


}
