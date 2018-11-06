package com.tangxiaopeng.videoeditdemo.presenter.impl;

import android.content.Context;

import com.tangxiaopeng.videoeditdemo.mvpview.MvpUserActivityView;
import com.tangxiaopeng.videoeditdemo.presenter.BasePresenter;


/**
 * MVP,对于逻辑处理模块，应该分离出来，以及需要多次多次使用的方法，提取出来，像登陆注册使用mvp开发，就会发现MVP的高超之处
 * https://segmentfault.com/a/1190000003927200
 */
public class TokenPresenter extends BasePresenter {
    private static String TAG = "USERPRESENTER";
    MvpUserActivityView activityView;
    public TokenPresenter(Context context, MvpUserActivityView activityView) {
        this.activityView = activityView;
        this.context = context;
    }
    @Override
    public void attach(Context context) {
        super.attach(context);
    }

}
