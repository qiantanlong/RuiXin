package com.hongzhen.ruixin.presenter.impl;

import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.UserDao;
import com.hongzhen.ruixin.presenter.MyInfoPresenter;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.view.activity.MyInfoView;
import com.hyphenate.chat.EMClient;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yuhongzhen on 2017/6/14.
 */

public class MyInfoPresenterImpl implements MyInfoPresenter {
    private MyInfoView mMyInfoView;

    public MyInfoPresenterImpl(MyInfoView myInfoView) {
        mMyInfoView = myInfoView;
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

    @Override
    public void onGetAvatorToSp() {
        BmobQuery<BUser> bUserBmobQuery = new BmobQuery<>();
        bUserBmobQuery.addWhereEqualTo(UserDao.COLUMN_NAME_ID,EMClient.getInstance().getCurrentUser())
                .findObjects(new FindListener<BUser>() {
                    @Override
                    public void done(List<BUser> list, BmobException e) {
                        String avatar = list.get(0).getAvatar();
                        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
                        mMyInfoView.onUpdateAvatorResult();
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
                mMyInfoView.onUpdateAvatorResult();
            }
        });
    }

}
