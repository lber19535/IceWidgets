package com.bill.icewidgets.utils;

import android.content.Intent;
import android.databinding.BindingConversion;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.bill.icewidgets.BuildConfig;

/**
 * Created by Bill on 2016/10/31.
 */
public class DatabindingConversion {
    private static final String TAG = "DatabindingConversion";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @BindingConversion
    public static ColorDrawable convertColorToDrawable(int color) {
        logd("convertColorToDrawable color " + color);
        return new ColorDrawable(color);
    }

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
