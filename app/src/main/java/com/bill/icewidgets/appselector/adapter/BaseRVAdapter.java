package com.bill.icewidgets.appselector.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.appselector.listener.OnRVItemClickListener;
import com.bill.icewidgets.appselector.listener.OnRVItemLongClickListener;

import java.util.HashMap;

/**
 * Created by Bill on 2016/10/25.
 */
public abstract class BaseRVAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "BaseRVAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private HashMap<View, Integer> positionMap = new HashMap<>();

    private OnRVItemClickListener itemClickListener;
    private OnRVItemLongClickListener longClickListener;

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        positionMap.put(holder.itemView, position);
        logd("onBindViewHolder: position " + position);
        logd("onBindViewHolder: item " + holder.itemView);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v, positionMap.get(v));
    }

    @Override
    public boolean onLongClick(View v) {
        longClickListener.onItemLongClick(v, positionMap.get(v));
        return true;
    }

    public void setOnClickListener(OnRVItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setOnLongClickListener(OnRVItemLongClickListener listener) {
        longClickListener = listener;
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);

    }


}
