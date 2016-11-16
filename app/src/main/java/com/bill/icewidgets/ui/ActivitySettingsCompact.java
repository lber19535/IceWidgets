package com.bill.icewidgets.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class ActivitySettingsCompact extends PreferenceActivity {

    private static final String TAG = "ActivitySettingsCompact";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);

        View inflate = getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup) findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar) inflate.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        /**
         * Because of PreferenceActivity use com.android.internal.R.layout.preference_list_content to set content view, we can not set custom preference
         * layout contains toolbar layout.
         *
         * When we use a single layout for setSupportActionBar method, the toolbar layout will overlay the ListView. PreferenceActivity is ListActivity,
         *  so we can get ListView by getListView method, and set it's top margins to not be covered.
         *
         *
         */
        TypedValue tv = new TypedValue();
        int topMargin = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            logd("onPostCreate resolve attr success");
            topMargin = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            logd("onPostCreate get toolbar height");
        }

        if (getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getListView().getLayoutParams();
            layoutParams.setMargins(0, topMargin, 0, 0);
            getListView().setLayoutParams(layoutParams);
        } else {
            int identifier = getResources().getSystem().getIdentifier("prefs_frame", "id", "android");
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById(identifier).getLayoutParams();
            layoutParams.setMargins(0, topMargin, 0, 0);
            findViewById(identifier).setLayoutParams(layoutParams);
        }

        String initialFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        logd("onPostCreate: initialFragment " + initialFragment);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }
}
