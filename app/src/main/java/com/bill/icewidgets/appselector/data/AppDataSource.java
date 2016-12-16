package com.bill.icewidgets.appselector.data;

import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;

import java.util.List;

/**
 * Created by Bill on 2016/12/9.
 */

public interface AppDataSource {

    List<AppSelectorItemVM> loadApp();
}
