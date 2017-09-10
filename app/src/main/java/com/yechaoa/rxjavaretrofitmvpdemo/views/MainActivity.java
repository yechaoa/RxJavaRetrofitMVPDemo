package com.yechaoa.rxjavaretrofitmvpdemo.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yechaoa.rxjavaretrofitmvpdemo.R;
import com.yechaoa.rxjavaretrofitmvpdemo.bean.Contributor;
import com.yechaoa.rxjavaretrofitmvpdemo.presenter.MainPresenterImpl;
import com.yechaoa.rxjavaretrofitmvpdemo.views.iview.MainView;

import java.util.List;

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

