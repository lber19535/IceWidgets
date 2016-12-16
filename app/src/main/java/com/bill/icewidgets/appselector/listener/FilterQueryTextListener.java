package com.bill.icewidgets.appselector.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;

import com.bill.icewidgets.appselector.adapter.AppSelectorAdapter;

/**
 * Created by Bill on 2016/12/9.
 */

public class FilterQueryTextListener implements SearchView.OnQueryTextListener {
    private AppSelectorAdapter adapter;

    public FilterQueryTextListener(@NonNull AppSelectorAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
}
