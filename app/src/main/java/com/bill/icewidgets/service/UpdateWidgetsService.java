package com.bill.icewidgets.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bill.icewidgets.R;
import com.bill.icewidgets.db.bean.AppIcon;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.ui.ActivityIceGroup;
import com.bill.icewidgets.ui.settings.ThemePreferenceFragment;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.bill.icewidgets.IceWidgets.APP_WIDGETS_ID;
import static com.bill.icewidgets.IceWidgets.LAUNCH_ACTION;

/**
 * Created by Bill on 2016/12/2.
 */

public class UpdateWidgetsService extends IntentService {

    private static final String TAG = "UpdateWidgetsService";
    private static final boolean DEBUG = true;

    private static final String ACTION_UPDATE_WIDGETS = "com.bill.icewidgets.service.action.UPDATE_WIDGETS";
    private static final String ACTION_UPDATE_WIDGETS_LAYOUT = "com.bill.icewidgets.service.action.UPDATE_WIDGETS_LAYOUT";

    private static final String EXTRA_WIDGETS_NAME = "ice.bill.com.icewidgets.extra.WIDGETS_NAME";
    private static final String EXTRA_WIDGETS_ID = "ice.bill.com.icewidgets.extra.WIDGETS_ID";

    public UpdateWidgetsService() {
        super("UpdateWidgetsService");
    }

    public static void updateWidgetsName(Context context, int widgetsId, @NonNull CharSequence name) {
        logd("update widgets name " + name + " id " + widgetsId);
        Intent intent = new Intent(context, UpdateWidgetsService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        intent.putExtra(EXTRA_WIDGETS_NAME, name);
        intent.putExtra(EXTRA_WIDGETS_ID, widgetsId);
        context.startService(intent);
    }

    public static void updateWidgetsLayout(Context context) {
        logd("update widgets widgetsIds ");
        Intent intent = new Intent(context, UpdateWidgetsService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS_LAYOUT);

        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_WIDGETS: {
                    handleUpdateWidgets(intent.getIntExtra(EXTRA_WIDGETS_ID, -1),
                            intent.getCharSequenceExtra(EXTRA_WIDGETS_NAME));
                    break;
                }
                case ACTION_UPDATE_WIDGETS_LAYOUT: {
                    handleUpdateWidgetsLayout();
                    break;
                }
                default:
                    logd(action + " is not defined");
                    break;

            }
        }
    }

    private void handleUpdateWidgetsLayout() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<NameIdPair> all = realm.where(NameIdPair.class).findAll();

        for (NameIdPair pair : all) {
            handleUpdateWidgets(pair.getWidgetsId(), pair.getGroupName());
        }

        realm.close();
    }


    private void handleUpdateWidgets(int widgetsId, CharSequence name) {
        RemoteViews views = getRemoteView();
        updateGroupName(views, name);
        updateIcon(views, widgetsId);

        Intent intent = new Intent(this, ActivityIceGroup.class);
        intent.setAction(LAUNCH_ACTION);
        intent.putExtra(APP_WIDGETS_ID, widgetsId);
        views.setOnClickPendingIntent(R.id.container, PendingIntent.getActivity(this, widgetsId, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        AppWidgetManager.getInstance(this).updateAppWidget(widgetsId, views);

        logd("AppWidgetManager update widgets");
    }

    private void updateIcon(RemoteViews view, int widgetsId) {
        Realm realm = Realm.getDefaultInstance();
        AppIcon appIcon = realm.where(AppIcon.class).equalTo(AppIcon.COLUMN_WIDGETS_ID, widgetsId).findFirst();
        int iconType = AppIcon.ICON_TYPE_ORIGINAL;

        if (appIcon != null)
            iconType = appIcon.getIconType();

        switch (iconType) {
            case AppIcon.ICON_TYPE_ORIGINAL:
                view.setImageViewResource(R.id.app_icon, R.drawable.ic_freezer);
                break;
            case AppIcon.ICON_TYPE_PKG_GEN:
                break;
            case AppIcon.ICON_TYPE_CUSTOMIZE:
                break;
            default:
                break;
        }
        realm.close();

    }

    private void updateGroupName(RemoteViews view, CharSequence name) {
        view.setTextViewText(R.id.app_name, name);
    }

    private RemoteViews getRemoteView() {
        SharedPreferences sp = getSharedPreferences(ThemePreferenceFragment.PREF_NAME, MODE_PRIVATE);
        String layout = sp.getString(getString(R.string.widgets_layout_key), getString(R.string.widgets_layout_def_value));
        int layoutResId = 0;
        switch (layout) {
            case ThemePreferenceFragment.WIDGETS_LAYOUT_MIDDLE:
                layoutResId = R.layout.ice_widgets_middle;
                break;
            case ThemePreferenceFragment.WIDGETS_LAYOUT_LARGE:
                layoutResId = R.layout.ice_widgets_large;
                break;
            case ThemePreferenceFragment.WIDGETS_LAYOUT_NORMAL:
                layoutResId = R.layout.ice_widgets;
            default:
                break;
        }

        RemoteViews views = new RemoteViews(getPackageName(), layoutResId);

        return views;
    }

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
