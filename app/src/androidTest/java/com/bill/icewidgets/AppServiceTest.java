package com.bill.icewidgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;

import com.bill.icewidgets.service.AppService;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppServiceTest {

    private boolean RECEIVE_FLAG = false;
    private Object lock = new Object();

    private String testAppPkg = "com.android.contacts";

    @Test
    public void testFreeze() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        RECEIVE_FLAG = false;
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RECEIVE_FLAG = true;
                assertEquals(true, RECEIVE_FLAG);
                synchronized (lock) {
                    lock.notify();
                }
            }
        };
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, new IntentFilter(AppService.ACTION_FREEZE_APPS));

        AppService.startFreezeApps(ctx, testAppPkg);

        synchronized (lock) {
            lock.wait(10000);
        }

        if (!RECEIVE_FLAG) {
            assertTrue(RECEIVE_FLAG);
            return;
        }

        ApplicationInfo applicationInfo = ctx.getPackageManager().getApplicationInfo(testAppPkg, 0);
        assertEquals(RECEIVE_FLAG, !applicationInfo.enabled);
    }

    @Test
    public void testUnfreeze() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        RECEIVE_FLAG = false;
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RECEIVE_FLAG = true;
                assertEquals(true, RECEIVE_FLAG);
                synchronized (lock) {
                    lock.notify();
                }
            }
        };
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, new IntentFilter(AppService.ACTION_UNFREEZE_APPS));

        AppService.startUnfreezeApps(ctx, "com.android.contacts");

        synchronized (lock) {
            lock.wait(10000);
        }

        if (!RECEIVE_FLAG) {
            assertTrue(RECEIVE_FLAG);
        }

        ApplicationInfo applicationInfo = ctx.getPackageManager().getApplicationInfo(testAppPkg, 0);
        assertEquals(RECEIVE_FLAG, applicationInfo.enabled);
    }

}
