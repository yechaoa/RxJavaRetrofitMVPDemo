# RxJavaRetrofitMVPDemo
MVP+Retrofit+RxJava简单事例

##### 早都想撸一个demo出来总结一下加深理解的，也想搞的深入一点，但是发现越深入反而越不好总结，只好先作罢，所以目前本文只是简单事例（大佬跳过）。

### MVP

>  简化Activity，以接口的方式实现M层和V层的交互，所以在定义接口前一定要先想好业务逻辑，这样接口写起来也比较便捷，不然的话就是写着写着，诶少个接口啊，然后回来又写个接口，这样的情况一次还好，一多就影响开发效率了。

### Retrofit

> 其实是对OkHttp的封装，官网是这样介绍的：A type-safe HTTP client for Android and Java。一个类型安全的用于Android和Java网络请求的客户端。

### RxJava

> 异步、简洁，链式的写法使逻辑看起来更加清晰。

##### 以上只是简单总结，文末附详细链接
本文也是以GitHubApi为例子
![这里写图片描述](http://img.blog.csdn.net/20170910175438399?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWVjaGFvYQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

#### 先加依赖 
```
/*rotrofit*/
compile 'com.squareup.retrofit2:retrofit:2.3.0'
compile 'com.squareup.retrofit2:adapter-rxjava:2.3.0'
compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
compile 'io.reactivex:rxandroid:1.1.0'
compile 'com.squareup.okhttp3:logging-interceptor:3.4.1'
```

#### 1、定义接口（View）

> 先想一下业务逻辑，获取数据然后展示数据，为了更好的用户体验，再加上Progress，联网的请求，然后还得有成功和失败的反馈，差不多先这些。
```
public interface MainView {

    void showProgress();

    void setDataList(List<Contributor> dataList);

    void hideProgress();

    void getDataByError();

    void getDataBySuccess();

    void httpError();

}
```
##### 获取数据我是直接放在点击事件了，应该再写一个getDataList的。

> 然后MainActivity实现MainView，接口方法都有了，就差数据了。View层是通过Presenter与Model层交互的，先写Model拿数据，再写Presenter把二者一连接，诶就好了。

#### 2、获取数据（Model）

> Model就比较简单了，因为只有一个获取数据的方法

```
public interface MainModel {

    void getGitHubData(String userName, String repo, MainModelImpl.OnMainListener listener, Context context);

}
```

```
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
```

> 诶，这里用到了网络请求，所以再来搞一下http

```
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
```

```
public class GitHubService {

    //初始化retrofit
    public static <T> T createRetrofitService(final Class<T> service) {

        //配置okhttp并设置时间和日志信息
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        //关联okhttp并加上rxjava和gson的配置和baseurl
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build();

        return retrofit.create(service);
    }

}

```

> 然后贴一下bean

```
public class Contributor {

    private String login;
    private Integer contributions;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getContributions() {
        return contributions;
    }

    public void setContributions(Integer contributions) {
        this.contributions = contributions;
    }
}
```

#### 3、连接M层与V层（Presenter）

> 也是只有获取数据的方法

```
public interface MainPresenter {

    void getData(String userName, String repo);

}
```

```
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

```

#### 最后再来看一下MainActivity的代码

```
public class MainActivity extends AppCompatActivity implements MainView {

    private String mUserName;
    private String mRepo;
    private TextView mTextView;
    private ProgressDialog pd;
    private MainPresenterImpl mMainPresenterImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName = getResources().getString(R.string.user_name);
        mRepo = getResources().getString(R.string.repo);

        mTextView = findViewById(R.id.text);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取MainPresenter，并通过MainPresenter获取数据
                mMainPresenterImpl = new MainPresenterImpl(MainActivity.this, MainActivity.this);
                mMainPresenterImpl.getData(mUserName, mRepo);
            }
        });

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("请求中");
    }

    @Override
    public void showProgress() {
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    @Override
    public void setDataList(List<Contributor> dataList) {
        mTextView.setText(dataList.toString());
    }

    @Override
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    public void getDataByError() {
        hideProgress();
        Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getDataBySuccess() {
        hideProgress();
        Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void httpError() {
        hideProgress();
    }

}

```

#### 现在MainActivity看起来就比较清晰了

#### 按照这个流程走下来是没有问题的，用到什么写什么，可能你一开始就把http给配置好了，这个是没有什么问题的，但是一开始就写太多东西的话，后面实际用到的时候如果不是一开始想的那样，又得重新写或者改了，这是影响效率的，所以何不一步一步脚踏实地呢对吧。
<br>
#### Demo地址：[https://github.com/yechaoa/RxJavaRetrofitMVPDemo](https://github.com/yechaoa/RxJavaRetrofitMVPDemo)
<br>
#### 相关资料
#### MVP：[Android MVP模式实战](http://blog.csdn.net/yechaoa/article/details/73695607)
#### Retrofit：[Retrofit2.0使用详解](http://blog.csdn.net/yechaoa/article/details/77434307)
#### RxJava：[ RxJava图文详解，可以说是很全了。](http://blog.csdn.net/yechaoa/article/details/77045214)
