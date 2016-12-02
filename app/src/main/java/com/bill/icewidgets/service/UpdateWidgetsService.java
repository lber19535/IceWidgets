package com.bill.icewidgets.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bill.icewidgets.R;

/**
 * Created by Bill on 2016/12/2.
 */

public class UpdateWidgetsService extends IntentService {

    private static final String TAG = "UpdateWidgetsService";
    private static final boolean DEBUG = true;

    private static final String ACTION_UPDATE_WIDGETS = "com.bill.icewidgets.service.action.UPDATE_WIDGETS";

    private static final String EXTRA_WIDGETS_NAME = "ice.bill.com.icewidgets.extra.WIDGETS_NAME";
    private static final String EXTRA_WIDGETS_ID = "ice.bill.com.icewidgets.extra.WIDGETS_ID";

    public UpdateWidgetsService() {
        super("UpdateWidgetsService");
    }

    public static void updateWidgetsName(Context context, int widgetsId, CharSequence name) {
        logd("update widgets name " + name + " id " + widgetsId);
        Intent intent = new Intent(context, UpdateWidgetsService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        intent.putExtra(EXTRA_WIDGETS_NAME, name);
        intent.putExtra(EXTRA_WIDGETS_ID, widgetsId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_WIDGETS: {
                    handleUpdateWidgetsName(intent.getIntExtra(EXTRA_WIDGETS_ID, -1),
                            intent.getCharSequenceExtra(EXTRA_WIDGETS_NAME));
                    break;
                }
                default:
                    logd(action + " is not defined");
                    break;

            }
        }
    }

    private void handleUpdateWidgetsName(int widgetsId, CharSequence name) {
        if (widgetsId == -1) {
            logd("widgets is lost");
            return;
        }
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.ice_widgets);
        views.setTextViewText(R.id.app_name, name);
        views.setImageViewResource(R.id.app_icon, R.drawable.ic_freezer);
        AppWidgetManager.getInstance(this).updateAppWidget(widgetsId, views);

        logd("AppWidgetManager update widgets");
    }

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
