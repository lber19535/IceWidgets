package com.bill.icewidgets.utils;

import android.databinding.BindingAdapter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

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

    @BindingAdapter({"app_item_type"})
    public static void setDateText(View view, int type) {
        if (DEBUG) {
            Log.d(TAG, "handleAppItemType type " + type);
        }
        if (type != ITEM_TYPE_NONE) {
            if ((type & ITEM_TYPE_FREEZE) != 0) {
                view.setBackgroundResource(R.color.selector_freeze_background);
                return;
            } else if ((type & ITEM_TYPE_ADD) != 0) {
                view.setBackgroundResource(R.color.selector_add_background);
                return;
            }
        }
        view.setBackgroundResource(android.R.color.transparent);
    }
}
