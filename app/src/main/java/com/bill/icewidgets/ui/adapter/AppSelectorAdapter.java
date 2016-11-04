package com.bill.icewidgets.ui.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.R;
import com.bill.icewidgets.databinding.AppSelectorItemBinding;
import com.bill.icewidgets.vm.AppSelectorItemVM;

import java.util.List;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorAdapter extends BaseRVAdapter<AppSelectorViewHolder> {
    private static final String TAG = "AppSelectorAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private List<AppSelectorItemVM> vms;

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

    @Override
    public int getItemCount() {
        return vms.size();
    }

}
