package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.UserDao;
import com.hongzhen.ruixin.presenter.RegistPresenter;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ThreadUtils;
import com.hongzhen.ruixin.view.activity.RegistView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by yuhongzhen on 2017/5/19.
 */

public class RegistPresenterImpl implements RegistPresenter {
    private RegistView mRegistView;

    public RegistPresenterImpl(RegistView mRegistView) {
        this.mRegistView = mRegistView;
    }

    @Override
    public void regist(final String username, final String pwd,final String nickname) {
        final BUser bUser = new BUser();
        bUser.setNick(nickname);
        bUser.setUsername(username);
        bUser.setPwd(pwd);
        bUser.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    //提交到服务器成功，进行环信注册
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username,pwd);
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onRegist(username,pwd,nickname,true,"ok");
                                    }
                                });
                            } catch (final HyphenateException e1) {
                                e1.printStackTrace();
                                bUser.delete();
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onRegist(username,pwd,nickname,false,e1.getMessage());
                                    }
                                });
                            }
                        }
                    });
                }else {
                    mRegistView.onRegist(username,pwd,nickname,false,e.getMessage());
                }
            }
        });
    }

    @Override
    public void onSaveToBMOB(File file) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    LogUtils.i("file-bmob-url"+bmobFile.getUrl());
                    String currentUser = EMClient.getInstance().getCurrentUser();
                    PreferenceManager.getInstance().setCurrentUserAvatar(bmobFile.getUrl());//保存到SP
                    getObjectId(currentUser,bmobFile.getUrl(),PreferenceManager.getInstance().getCurrentUserNick());

                }else {
                    LogUtils.i("bmob-file" + e.toString());
                }
            }

        });
    }
    private void getObjectId(final String currentUser, final String url, final String nickname) {
        BmobQuery<BUser> bUserBmobQuery = new BmobQuery<>();
        bUserBmobQuery.addWhereEqualTo(UserDao.COLUMN_NAME_ID,currentUser)
                .findObjects(new FindListener<BUser>() {
                    @Override
                    public void done(List<BUser> list, BmobException e) {
                        BUser bUser = list.get(0);
                        String objectId = bUser.getObjectId();
                        setImgToBMOB(objectId,currentUser,url,nickname);
                    }
                });
    }
    private void setImgToBMOB(String objectId,String currentUser, String url, String nickname){
        BUser bUser = new BUser();
        bUser.setUsername(currentUser);
        bUser.setAvatar(url);
        bUser.setNick(nickname);
        bUser.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                //保存成功，更新UI
            }
        });
    }
}
