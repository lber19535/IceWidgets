package com.bill.icewidgets.appselector.adapter;

import android.databinding.BindingAdapter;
import android.view.View;

import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by Bill on 2016/12/9.
 */

public class ViewBindingAdapter {

    @BindingAdapter({"matProg_progressIndeterminate"})
    public static void progressIndeterminate(View view, boolean spin) {
        ProgressWheel progressWheel = (ProgressWheel) view;
        if (spin && !progressWheel.isSpinning()) {
            progressWheel.spin();
        } else {
            progressWheel.stopSpinning();
        }
    }
}
