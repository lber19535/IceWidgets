package com.bill.icewidgets.vm;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorItemVM implements VM {

    public final ObservableField<String> label = new ObservableField<>("");
    public final ObservableField<Drawable> icon = new ObservableField<>();
    public final ObservableField<String> pkgName = new ObservableField<>("");
    public final ObservableInt type = new ObservableInt();
    public final ObservableField<String> groupName = new ObservableField<>("");
    public final ObservableInt widgetsId = new ObservableInt();
    public final ObservableBoolean disable = new ObservableBoolean();

    @Override
    public void destroy() {

    }
}
