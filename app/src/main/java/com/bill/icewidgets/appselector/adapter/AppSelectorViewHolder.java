package com.bill.icewidgets.appselector.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.databinding.AppSelectorItemBinding;

/**
 * Created by Bill on 2016/10/23.
 */
public class AppSelectorViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "AppSelectorViewHolder";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private AppSelectorItemBinding binding;

    public AppSelectorViewHolder(View itemView) {
        super(itemView);
    }

    public AppSelectorItemBinding getBinding() {
        return binding;
    }

    public void setBinding(AppSelectorItemBinding binding) {
        this.binding = binding;
    }

}
