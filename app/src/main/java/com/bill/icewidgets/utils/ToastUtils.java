package com.bill.icewidgets.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by Bill on 2017/5/17.
 */

public class ToastUtils {

    public static void longToast(Context ctx, CharSequence text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public static void longToast(Context ctx, @StringRes int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_LONG).show();
    }
}
