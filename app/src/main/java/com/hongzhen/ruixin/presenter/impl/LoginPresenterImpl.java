package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.eventbus.EBNickName;
import com.hongzhen.ruixin.modle.EaseUser;
import com.hongzhen.ruixin.presenter.LoginPresenter;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ThreadUtils;
import com.hongzhen.ruixin.view.activity.LoginView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by yuhongzhen on 2017/5/20.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView loginView;
    private BUser mBUser;


    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void login(final String usename, final String pwd) {
        RXDbManager.getInstance().closeDB();//关闭数据库
        PreferenceManager.getInstance().removeCurrentUserInfo();//清楚SP中数据
        if (EMClient.getInstance().isLoggedInBefore()){
            EMClient.getInstance().logout(true);
        }
            EMClient.getInstance().login(usename, pwd, new EMCallBack() {
                @Override
                public void onSuccess() {
                    PreferenceManager.getInstance().setCurrentUserName(usename);//保存环信ID到SP
                    //获取环信服务器上当前用户的好友列表
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<String> allContactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                                getUserFromBMOBtoDB(allContactsFromServer);
                                getCurrentUserInfoFromBMOB(usename);//从bmob服务器获取当前用户信息
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            loginView.onLogin(usename, pwd, true, null);
                        }
                    });

                }

                @Override
                public void onError(int i, final String s) {
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            loginView.onLogin(usename, pwd, false, s);
                        }
                    });


                }

                @Override
                public void onProgress(int i, String s) {

                }
            });

    }

    private void getCurrentUserInfoFromBMOB(final String username) {
        LogUtils.i("f-bmob-user" + username);
        BmobQuery<BUser> bUserBmobQuery = new BmobQuery<>();
        bUserBmobQuery.setLimit(5);
        bUserBmobQuery.addWhereEqualTo("username", username)
                .findObjects(new FindListener<BUser>() {
                    @Override
                    public void done(List<BUser> list, BmobException e) {
                        if (list != null && list.size() != 0) {
                            BUser user = list.get(0);
                            LogUtils.i("bmob" + user.getUsername() + "--" + user.getNick());
                            PreferenceManager.getInstance().setCurrentUserNick(user.getNick());
                            PreferenceManager.getInstance().setCurrentUserAvatar(user.getAvatar());
                            LogUtils.i("sp" + user.getUsername() + "--" + user.getNick());
                            EventBus.getDefault().post(new EBNickName("ok"));
                        }

                    }
                });

    }

    /**
     * 从BMOB服务器查询用户的详细信息，保存到本地数据库
     */
    private void getUserFromBMOBtoDB(List<String> allContactsFromServer) {
        for (int i = 0; i < allContactsFromServer.size(); i++) {
            synchronized (this) {
                BmobQuery<BUser> userDaoBmobQuery = new BmobQuery<>();
                userDaoBmobQuery.setLimit(5);
                userDaoBmobQuery.addWhereEqualTo("username", allContactsFromServer.get(i))
                        .findObjects(new FindListener<BUser>() {
                            @Override
                            public void done(List<BUser> list, BmobException e) {
                                if (list != null && list.size() != 0) {
                                    mBUser = list.get(0);
                                    EaseUser easeUser = new EaseUser(mBUser.getUsername());
                                    easeUser.setNickName(mBUser.getNick());
                                    easeUser.setPwd(mBUser.getPwd());
                                    RXDbManager.getInstance().saveContact(easeUser);
                                }
                            }
                        });
            }

        }
    }
}
