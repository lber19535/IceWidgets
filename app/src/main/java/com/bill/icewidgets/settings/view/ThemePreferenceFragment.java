package com.bill.icewidgets.settings.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.bill.icewidgets.R;
import com.bill.icewidgets.service.UpdateWidgetsService;

/**
 * Created by Bill on 2016/11/14.
 */

public class ThemePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String PREF_NAME = "pref_theme";

    public static final String WIDGETS_LAYOUT_NORMAL = "Normal";
    public static final String WIDGETS_LAYOUT_MIDDLE = "Middle";
    public static final String WIDGETS_LAYOUT_LARGE = "Large";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_theme);

        getPreferenceManager().setSharedPreferencesName(PREF_NAME);

        initPref();
    }

    public void initPref() {
        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        String widgetsLayoutKey = getResources().getString(R.string.widgets_layout_key);
        String widgetsLayout = sp.getString(widgetsLayoutKey, WIDGETS_LAYOUT_NORMAL);
        ListPreference widgetsLayoutPref = (ListPreference) findPreference(widgetsLayoutKey);
        widgetsLayoutPref.setValue(widgetsLayout);
        widgetsLayoutPref.setOnPreferenceChangeListener(this);


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.widgets_layout_key))) {
            UpdateWidgetsService.updateWidgetsLayout(getActivity());
        }

        return true;
    }
}
