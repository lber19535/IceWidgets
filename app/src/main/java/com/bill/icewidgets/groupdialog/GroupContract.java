package com.bill.icewidgets.groupdialog;

import com.bill.icewidgets.BasePresenter;
import com.bill.icewidgets.BaseView;
import com.bill.icewidgets.db.bean.AppItem;

import java.util.List;

/**
 * Created by Bill on 2016/12/14.
 */

public interface GroupContract {

    interface View extends BaseView<GroupContract.Presenter> {
        void bindGroupItems(List<AppItem> items);
        void bindGroupName(String name);
    }

    interface Presenter extends BasePresenter {


    }
}
