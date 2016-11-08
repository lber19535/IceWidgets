package com.bill.icewidgets.ui.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.databinding.AppSelectorItemBinding;
import com.bill.icewidgets.vm.AppSelectorItemVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorAdapter extends BaseRVAdapter<AppSelectorViewHolder> implements Filterable {
    private static final String TAG = "AppSelectorAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private List<AppSelectorItemVM> vms;
    private AppNameFilter filter;

    public AppSelectorAdapter(List<AppSelectorItemVM> itemModels) {
        this.vms = itemModels;
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

    private class AppNameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence appName) {
            FilterResults filterResults = new FilterResults();
            List<AppSelectorItemVM> results = new ArrayList<>();
            List<AppSelectorItemVM> oringinal = new ArrayList<>(vms);
            for (AppSelectorItemVM item : vms) {
                if (item.label.get().contains(appName)) {
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
