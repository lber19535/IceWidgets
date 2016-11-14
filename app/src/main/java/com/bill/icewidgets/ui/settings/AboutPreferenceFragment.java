package com.bill.icewidgets.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.bill.icewidgets.R;

/**
 * Created by Bill on 2016/11/14.
 */

public class AboutPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

    }
}
