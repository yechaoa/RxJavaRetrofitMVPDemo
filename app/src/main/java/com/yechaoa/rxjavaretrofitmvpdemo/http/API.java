package com.yechaoa.rxjavaretrofitmvpdemo.http;

import com.yechaoa.rxjavaretrofitmvpdemo.bean.Contributor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by yechao on 2017/8/20.
 * Describe : 接口的管理类
 */
public class API {

    public interface GitHubApi {
        //请求头 key value的形式
        @Headers({
                "name:request header"
        })
        @GET("repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> contributorsBySimpleGetCall(@Path("owner") String owner, @Path("repo") String repo);

        //@Path：URL占位符，用于替换和动态更新,相应的参数必须使用相同的字符串被@Path进行注释，就是调用这个方法时动态传的参数
        @GET("repos/{owner}/{repo}/contributors")
        Observable<List<Contributor>> contributorsByRxJava(@Path("owner") String owner, @Path("repo") String repo);
    }

}
