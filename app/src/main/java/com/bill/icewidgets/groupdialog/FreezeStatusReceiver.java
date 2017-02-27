package com.bill.icewidgets.groupdialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.service.AppService;

import java.util.Arrays;
import java.util.List;

/**
 * Bind AppItems to freeze app status
 * <p>
 * ACTION is
 * <p>
 * Created by Bill on 2017/2/16.
 */

public class FreezeStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "FreezeStatusReceiver";

    private List<AppItem> items;

    public FreezeStatusReceiver(List<AppItem> items) {
        this.items = items;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        CharSequence[] pkgs = intent.getCharSequenceArrayExtra(AppService.EXTRA_PACKAGES);
        List<CharSequence> pkgsList = Arrays.asList(pkgs);
        if (action.equals(AppService.ACTION_FREEZE_APPS)) {
            for (AppItem item : items) {
                if (pkgsList.contains(item.getPackageName())) {
                    item.setFreezed(true);
                }
            }
        } else if (action.equals(AppService.ACTION_UNFREEZE_APPS)) {
            for (AppItem item : items) {
                if (pkgsList.contains(item.getPackageName())) {
                    item.setFreezed(false);
                }
            }
        }
    }
}
