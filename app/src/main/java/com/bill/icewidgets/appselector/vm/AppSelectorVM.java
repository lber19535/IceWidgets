package com.bill.icewidgets.appselector.vm;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;

/**
 * Created by Bill on 2016/12/9.
 */

public class AppSelectorVM {

    public final ObservableInt progressVisibility = new ObservableInt(View.VISIBLE);
    public final ObservableBoolean progressIndeterminate = new ObservableBoolean(true);
}
