package com.bill.icewidgets.appselector.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.databinding.AppSelectorItemBinding;
import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorAdapter extends BaseRVAdapter<AppSelectorViewHolder> implements Filterable {
    private static final String TAG = "AppSelectorAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private List<AppSelectorItemVM> mOriginalValues;
    private List<AppSelectorItemVM> vms;
    private AppNameFilter filter;

    public AppSelectorAdapter(List<AppSelectorItemVM> itemModels) {
        if (itemModels == null) {
            this.vms = new ArrayList<>();
        } else {
            this.vms = itemModels;
        }
    }

    @Override
    public AppSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppSelectorItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.app_selector_item, parent, false);
        AppSelectorViewHolder holder = new AppSelectorViewHolder(binding.getRoot());
        holder.setBinding(binding);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AppSelectorViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        AppSelectorItemVM vm = vms.get(position);
        holder.getBinding().setVm(vm);

    }

    public void replaceAll(List<AppSelectorItemVM> items) {
        this.vms = items;

        if (mOriginalValues != null && !mOriginalValues.isEmpty())
            this.mOriginalValues.clear();
    }

    public void showAll() {
        if (mOriginalValues != null && !mOriginalValues.isEmpty())
            vms = mOriginalValues;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vms.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppNameFilter();
        }
        return filter;
    }

    public List<AppSelectorItemVM> getItems() {
        return vms;
    }

    public AppSelectorItemVM getItem(int index) {
        return vms.get(index);
    }


    private class AppNameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence appName) {
            FilterResults filterResults = new FilterResults();
            List<AppSelectorItemVM> results = new ArrayList<>();
            if (mOriginalValues == null || mOriginalValues.isEmpty()) {
                mOriginalValues = new ArrayList<>(vms);
            }
            for (AppSelectorItemVM item : mOriginalValues) {
                String lowLabel = item.label.get().toLowerCase();
                String lowAppName = appName.toString().toLowerCase();
                if (lowLabel.contains(lowAppName)) {
                    results.add(item);
                }
            }
            filterResults.count = results.size();
            filterResults.values = results;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            vms = (List<AppSelectorItemVM>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}
