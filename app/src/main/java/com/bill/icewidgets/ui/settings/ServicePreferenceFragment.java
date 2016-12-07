package com.bill.icewidgets.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.bill.icewidgets.R;

/**
 * Created by Bill on 2016/11/14.
 */

public class ServicePreferenceFragment extends PreferenceFragment {

    public static final String PREF_NAME = "pref_service";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_service);

        getPreferenceManager().setSharedPreferencesName(PREF_NAME);

        initPref();
    }

    public void initPref() {
        SharedPreferences pref = getPreferenceManager().getSharedPreferences();
        // load select value of list and list pref enable status
        ListPreference autoFreezeDelayPreference = (ListPreference) findPreference(getString(R.string.pref_service_auto_freeze_time_key));
        autoFreezeDelayPreference.setValue(pref.getString(getString(R.string.pref_service_auto_freeze_time_key), "0"));
        autoFreezeDelayPreference.setEnabled(pref.getBoolean(getString(R.string.pref_service_auto_freeze_key), false));

        // load auto freeze switcher status
        SwitchPreference autoFreezePreference = (SwitchPreference) findPreference(getString(R.string.pref_service_auto_freeze_key));
        autoFreezePreference.setChecked(pref.getBoolean(getString(R.string.pref_service_auto_freeze_key), false));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_service_auto_freeze_key))) {
            SharedPreferences pref = getPreferenceManager().getSharedPreferences();
            findPreference(getString(R.string.pref_service_auto_freeze_time_key))
                    .setEnabled(pref.getBoolean(getString(R.string.pref_service_auto_freeze_key), false));
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
