package com.bill.icewidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.bill.icewidgets.R;
import com.bill.icewidgets.db.bean.AppItem;
import com.bill.icewidgets.db.bean.NameIdPair;
import com.bill.icewidgets.ui.ActivityIceGroup;
import com.tencent.bugly.crashreport.BuglyLog;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Implementation of App Widget functionality.
 */
public class IceWidgets extends AppWidgetProvider {

    public static final String LAUNCH_ACTION = "LAUNCH_ACTION";
    public static final String APP_WIDGETS_ID = "APP_WIDGETS_ID";
    public static final int DEFAULT_APP_WIDGETS_ID = 0;
    private Realm realm;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        System.out.println(appWidgetId);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ice_widgets);
        views.setTextViewText(R.id.app_name, widgetText);
        views.setImageViewResource(R.id.app_icon, R.drawable.ic_freezer);

        Intent intent = new Intent(context, ActivityIceGroup.class);
        intent.setAction(LAUNCH_ACTION);
        intent.putExtra(APP_WIDGETS_ID, appWidgetId);
        views.setOnClickPendingIntent(R.id.container, PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        System.out.println("onAppWidgetOptionsChanged");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        realm = getRealm();
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        // add widgetsid
        if (appWidgetIds.length == 1 &&
                realm.where(NameIdPair.class).equalTo("widgetsId", appWidgetIds[0]).findFirst() == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    NameIdPair pair = realm.createObject(NameIdPair.class);
                    pair.setGroupName("");
                    pair.setWidgetsId(appWidgetIds[0]);
                }
            });

        }
        realm.close();
        System.out.println("onUpdate");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        System.out.println("onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        System.out.println("disable");
    }

    private void deleteGroup(AppWidgetManager widgetManager) {
        realm = getRealm();
        // find delete widgetid
        RealmResults<NameIdPair> pairs = realm.where(NameIdPair.class).findAll();
        int widgetsId = -1;
        for (final NameIdPair pair : pairs) {
            AppWidgetProviderInfo widgetInfo = widgetManager.getAppWidgetInfo(pair.getWidgetsId());
            if (widgetInfo == null) {
                widgetsId = pair.getWidgetsId();
            }
        }

        // delete widgetsid
        if (widgetsId != -1) {
            // delete widgetid in nameidpair
            final NameIdPair pair = realm.where(NameIdPair.class).equalTo("widgetsId", widgetsId).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    pair.deleteFromRealm();
                }
            });

            // delete widgetid used in appitems
            final RealmResults<AppItem> appitems = realm.where(AppItem.class).equalTo("widgetsId", widgetsId).findAll();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (AppItem item : appitems) {
                        item.setWidgetsId(0);
                    }
                }
            });
        }
        realm.close();

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        System.out.println("onDeleted");
        // delete relative widgets
        deleteGroup(AppWidgetManager.getInstance(context));
        System.out.println(Arrays.toString(appWidgetIds));
    }

    private Realm getRealm() {

        realm = Realm.getDefaultInstance();
        return realm;
    }
}

