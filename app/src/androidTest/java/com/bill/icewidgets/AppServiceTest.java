package com.bill.icewidgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.ObjectArrays;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bill.icewidgets.service.AppService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class AppServiceTest {

    private boolean freeze = false;
    private Object lock = new Object();

    @Test
    public void testFunc() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intent.getAction().equals(AppService.ACTION_FREEZE_APPS);
                freeze = true;
                assertEquals(true, freeze);
                synchronized (lock) {
                    lock.notify();
                }
            }
        };
        ctx.registerReceiver(receiver, new IntentFilter(AppService.ACTION_FREEZE_APPS));

        AppService.startFreezeApps(ctx, "com.android.contacts");

        synchronized (lock) {
            lock.wait(10000);
        }

        if (!freeze) {
            assertTrue(freeze);
        }


    }

}
