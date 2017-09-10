package com.yechaoa.rxjavaretrofitmvpdemo.model;

import android.content.Context;

/**
 * Created by yechao on 2017/8/20.
 * Describe :
 */
public interface MainModel {

    void getGitHubData(String userName, String repo, MainModelImpl.OnMainListener listener, Context context);

}
