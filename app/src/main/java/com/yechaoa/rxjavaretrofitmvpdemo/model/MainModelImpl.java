package com.yechaoa.rxjavaretrofitmvpdemo.model;

import android.content.Context;
import android.util.Log;

import com.yechaoa.rxjavaretrofitmvpdemo.bean.Contributor;
import com.yechaoa.rxjavaretrofitmvpdemo.http.API;
import com.yechaoa.rxjavaretrofitmvpdemo.http.GitHubService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yechao on 2017/8/20.
 * Describe : Model层，通过OnMainListener与MainPresenterImpl交互
 */
public class MainModelImpl implements MainModel {

    @Override
    public void getGitHubData(String userName, String repo, final OnMainListener listener, Context context) {

        //rxjava方式
        CompositeSubscription mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(
                GitHubService.createRetrofitService(API.GitHubApi.class).contributorsByRxJava(userName, repo)//传两个参数到接口
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Contributor>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                listener.onError();
                            }

                            @Override
                            public void onNext(List<Contributor> contributors) {
                                for (Contributor c : contributors) {
                                    Log.d("TAG", "login:" + c.getLogin() + "  contributions:" + c.getContributions());
                                }
                                listener.onSuccess();
                                listener.setData(contributors);
                            }
                        }));

        //一般请求
//        API.GitHubApi gitHubApi = GitHubService.createRetrofitService(API.GitHubApi.class);
//        Call<List<Contributor>> call = gitHubApi.contributorsBySimpleGetCall(userName, repo);
//        call.enqueue(new Callback<List<Contributor>>() {
//            @Override
//            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
//                List<Contributor> contributorList = response.body();
//                for (Contributor c : contributorList){
//                    Log.d("TAG", "login:" + c.getLogin() + "  contributions:" + c.getContributions());
//                }
//                listener.onSuccess();
//                listener.setData(contributorList);
//            }
//
//            @Override
//            public void onFailure(Call<List<Contributor>> call, Throwable t) {
//                listener.onError();
//            }
//        });

    }

    public interface OnMainListener {
        void onError();

        void onSuccess();

        void onHttpError();

        void setData(List<Contributor> dataList);
    }

}
