package com.bill.icewidgets.service.tools;

import android.util.Log;

import com.stericson.RootShell.execution.Command;

/**
 * Created by Bill on 2016/12/12.
 */

public class CallbackCommand extends Command {

    private static final String TAG = "CallbackCommand";
    private static final boolean DEBUG = true;

    private Callback callback;

    public CallbackCommand(int id, Callback callback, String... command) {
        super(id, command);

        this.callback = callback;

    }

    @Override
    public void commandTerminated(int id, String reason) {
        super.commandTerminated(id, reason);
        logd("commandTerminated: " + reason);
    }

    @Override
    public void commandCompleted(int id, int exitcode) {
        super.commandCompleted(id, exitcode);
        logd("commandCompleted: " + exitcode);
    }

    @Override
    public void commandOutput(int id, String line) {
        super.commandOutput(id, line);
        logd("commandOutput: " + line);
        callback.onCallback(line);
    }

    private static void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public interface Callback {
        void onCallback(String line);
    }
}
