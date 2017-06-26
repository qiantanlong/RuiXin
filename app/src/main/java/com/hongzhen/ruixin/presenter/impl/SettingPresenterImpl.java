package com.hongzhen.ruixin.presenter.impl;

import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.listener.EMCallBackListener;
import com.hongzhen.ruixin.presenter.SettingPresenter;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.view.activity.SettingView;
import com.hyphenate.chat.EMClient;

/**
 * Created by yuhongzhen on 2017/6/11.
 */

public class SettingPresenterImpl implements SettingPresenter {
    private SettingView mSettingView;

    public SettingPresenterImpl(SettingView settingView) {
        mSettingView = settingView;
    }

    @Override
    public void loginOut() {

        EMClient.getInstance().logout(true, new EMCallBackListener() {
            @Override
            public void mainOnSuccess() {
                mSettingView.onLoginOut(EMClient.getInstance().getCurrentUser(),true,null);
                RXDbManager.getInstance().closeDB();//退出登录后，关闭db，否则切换用户后数据库无法初始化
                PreferenceManager.getInstance().removeCurrentUserInfo();//清楚SP中数据
            }



            @Override
            public void mainOnError(String s) {
                mSettingView.onLoginOut(EMClient.getInstance().getCurrentUser(),false,"失败:"+s);
            }
        });
    }
}
