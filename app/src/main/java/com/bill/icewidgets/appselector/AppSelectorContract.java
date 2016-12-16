package com.bill.icewidgets.appselector;

import com.bill.icewidgets.BasePresenter;
import com.bill.icewidgets.BaseView;
import com.bill.icewidgets.appselector.vm.AppSelectorItemVM;

import java.util.List;

/**
 * Created by Bill on 2016/12/9.
 */

public interface AppSelectorContract {

    interface View extends BaseView<Presenter>{
        void stopProgress();
        void updateList(List<AppSelectorItemVM> items);
    }

    interface Presenter extends BasePresenter{
        void changeFreezeItems(AppSelectorItemVM item);
        void changeAddItems(AppSelectorItemVM item);
        void postSaveFreeze(android.view.View v);

    }
}
