package com.bill.icewidgets.utils;

import android.util.Log;

import com.bill.icewidgets.App;
import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;

import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_ADD;
import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_FREEZE;
import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_NONE;


/**
 * Created by Bill on 2016/10/31.
 */
public class AppItemTypeConverter {
    private static final String TAG = "AppItemTypeConverter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * @param type
     * @return
     */
    public static int handleAppItemType(int type) {
        if (DEBUG) {
            Log.d(TAG, "handleAppItemType type " + type);
        }

        if (type != ITEM_TYPE_NONE) {
            if ((type & ITEM_TYPE_FREEZE) != 0) {
                return App.getAppCtx().getResources().getColor(R.color.selector_freeze_background);
            } else if ((type & ITEM_TYPE_ADD) != 0) {
                return App.getAppCtx().getResources().getColor(R.color.selector_add_background);
            }
        }
        return App.getAppCtx().getResources().getColor(android.R.color.transparent);

    }
}
