package com.bill.icewidgets.settings.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.bill.icewidgets.R;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

/**
 * Created by Bill on 2016/11/14.
 */

public class AboutPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        init();

    }

    private void init() {
        Preference preference = findPreference(getString(R.string.pref_about_version_key));
        preference.setTitle(getString(R.string.pref_about_version_title) + " " + getString(R.string.app_version_name));
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
