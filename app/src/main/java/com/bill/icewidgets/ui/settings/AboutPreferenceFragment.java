package com.bill.icewidgets.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.bill.icewidgets.R;
import com.tencent.bugly.beta.Beta;

/**
 * Created by Bill on 2016/11/14.
 */

public class AboutPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);


    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_about_version_key))) {
            Beta.checkUpgrade();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
