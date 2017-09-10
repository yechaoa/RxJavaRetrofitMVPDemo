package com.yechaoa.rxjavaretrofitmvpdemo.presenter;

import android.content.Context;

import com.yechaoa.rxjavaretrofitmvpdemo.bean.Contributor;
import com.yechaoa.rxjavaretrofitmvpdemo.model.MainModelImpl;
import com.yechaoa.rxjavaretrofitmvpdemo.views.iview.MainView;

import java.util.List;


/**
 * Created by yechao on 2017/8/20.
 * Describe : Presenter层，实现OnMainListener接口与Model交互，并与MainView层交互
 * Presenter作为桥梁实现Model层和View层之间的交互
 */
public class MainPresenterImpl implements MainPresenter, MainModelImpl.OnMainListener {

    private MainView mMainView;
    private MainModelImpl mMainModel;
    private Context mContext;

    public MainPresenterImpl(MainView view, Context context) {
        this.mContext = context;
        mMainView = view;
        mMainModel = new MainModelImpl();
    }


    @Override
    public void getData(String userName, String repo) {
        if (mMainView != null) {
            mMainView.showProgress();
        }
        //通过Model去获取数据
        mMainModel.getGitHubData(userName, repo, this, mContext);
    }

    @Override
    public void onError() {
        if (mMainView != null) {
            mMainView.getDataByError();
        }
    }

    @Override
    public void onSuccess() {
        if (mMainView != null) {
            mMainView.getDataBySuccess();
        }
    }

    @Override
    public void onHttpError() {

    }

    @Override
    public void setData(List<Contributor> dataList) {
        if (mMainView != null) {
            mMainView.setDataList(dataList);
        }
    }

}
