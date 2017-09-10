package com.yechaoa.rxjavaretrofitmvpdemo.views.iview;

import com.yechaoa.rxjavaretrofitmvpdemo.bean.Contributor;

import java.util.List;


/**
 * Created by yechao on 2017/8/20.
 * Describe : View层
 */
public interface MainView {

    void showProgress();

    void setDataList(List<Contributor> dataList);

    void hideProgress();

    void getDataByError();

    void getDataBySuccess();

    void httpError();

}
