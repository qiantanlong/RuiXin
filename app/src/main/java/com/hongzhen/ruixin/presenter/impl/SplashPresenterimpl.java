package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.presenter.SplashPresenter;
import com.hongzhen.ruixin.view.activity.SplashView;
import com.hyphenate.chat.EMClient;



/**
 * Created by yuhongzhen on 2017/5/19.
 */

public class SplashPresenterimpl implements SplashPresenter {

    private SplashView mSplashView;

    public SplashPresenterimpl(SplashView mSplashView) {
        this.mSplashView = mSplashView;
    }

    @Override
    public void checkLogin() {
        if (EMClient.getInstance().isLoggedInBefore()&& EMClient.getInstance().isConnected()){
            //登录了
            mSplashView.onCheckLoginned(true);
        }else {
            //没有登录
            mSplashView.onCheckLoginned(false);
        }
    }
}
