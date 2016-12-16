package com.bill.icewidgets.appselector.adapter;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_ADD;
import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_FREEZE;
import static com.bill.icewidgets.db.bean.AppItem.ITEM_TYPE_NONE;

/**
 * Created by Bill on 2016/12/9.
 */

public class ViewBindingAdapter {

    private static final String TAG = "ViewBindingAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @BindingAdapter({"matProg_progressIndeterminate"})
    public static void progressIndeterminate(View view, boolean spin) {
        ProgressWheel progressWheel = (ProgressWheel) view;
        if (spin && !progressWheel.isSpinning()) {
            progressWheel.spin();
        } else {
            progressWheel.stopSpinning();
        }
    }

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
