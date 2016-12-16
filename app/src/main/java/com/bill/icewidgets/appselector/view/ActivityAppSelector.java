package com.bill.icewidgets.appselector.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bill.icewidgets.BuildConfig;
import com.bill.icewidgets.IceWidgets;
import com.bill.icewidgets.R;
import com.bill.icewidgets.appselector.AppSelectorContract;
import com.bill.icewidgets.appselector.adapter.AppSelectorAdapter;
import com.bill.icewidgets.appselector.data.AppRepository;
import com.bill.icewidgets.appselector.listener.FilterQueryTextListener;
import com.bill.icewidgets.appselector.listener.OnRVItemClickListener;
import com.bill.icewidgets.appselector.listener.OnRVItemLongClickListener;
import com.bill.icewidgets.appselector.presenter.AppSelectorPresenter;
import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;
import com.bill.icewidgets.appselector.vm.AppSelectorVM;
import com.bill.icewidgets.databinding.ActivityAppSelectorBinding;
import com.bill.icewidgets.common.events.CloseSelectorEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ActivityAppSelector extends AppCompatActivity implements AppSelectorContract.View, OnRVItemLongClickListener, OnRVItemClickListener {

    private static final String TAG = "ActivityAppSelector";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ActivityAppSelectorBinding binding;
    private AppSelectorVM appSelectorVM;
    private AppSelectorContract.Presenter presenter;
    private AppSelectorAdapter adapter;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_selector);


        Intent intent = getIntent();
        presenter = new AppSelectorPresenter(this,
                new AppRepository(this),
                intent.getIntExtra(IceWidgets.APP_WIDGETS_ID,
                        IceWidgets.DEFAULT_APP_WIDGETS_ID));

        appSelectorVM = new AppSelectorVM();
        binding.setVm(appSelectorVM);
        binding.setPresenter(presenter);

        adapter = new AppSelectorAdapter(null);
        queryTextListener = new FilterQueryTextListener(adapter);
        binding.allList.setAdapter(adapter);
        binding.allList.setHasFixedSize(true);
        adapter.setOnClickListener(this);
        adapter.setOnLongClickListener(this);
        binding.allList.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.selector_search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(search);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // add filter
        searchView.setOnQueryTextListener(queryTextListener);

        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                logd("onMenuItemActionExpand: ");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                logd("onMenuItemActionCollapse: ");
                adapter.showAll();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    private void logd(String msg) {
        if (DEBUG)
            Log.d(TAG, "msg");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseActivityMessage(CloseSelectorEvent event) {
        finish();
    }


    @Override
    public void setPresenter(AppSelectorContract.Presenter p) {
        this.presenter = p;
    }

    @Override
    public void onItemClick(View v, int position) {
        AppSelectorItemVM item = adapter.getItem(position);
        presenter.changeFreezeItems(item);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        AppSelectorItemVM item = adapter.getItem(position);
        presenter.changeAddItems(item);
    }

    @Override
    public void stopProgress() {
        appSelectorVM.progressIndeterminate.set(false);
        appSelectorVM.progressVisibility.set(View.GONE);
    }

    @Override
    public void updateList(List<AppSelectorItemVM> items) {
        adapter.replaceAll(items);
    }
}
