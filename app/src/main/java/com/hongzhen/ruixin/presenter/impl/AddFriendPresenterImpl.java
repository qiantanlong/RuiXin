package com.hongzhen.ruixin.presenter.impl;

import android.util.Log;

import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.modle.EaseUser;
import com.hongzhen.ruixin.presenter.AddFriendPresenter;
import com.hongzhen.ruixin.utils.ThreadUtils;
import com.hongzhen.ruixin.view.activity.AddFriendView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by yuhongzhen on 2017/6/2.
 */

public class AddFriendPresenterImpl implements AddFriendPresenter {
    private AddFriendView mAddFriendView;

    public AddFriendPresenterImpl(AddFriendView addFriendView) {
        mAddFriendView = addFriendView;
    }

    @Override
    public void onSearchUser(String keyWord) {
        BmobQuery<BUser> userDaoBmobQuery = new BmobQuery<>();
        userDaoBmobQuery.setLimit(20);
        userDaoBmobQuery.addWhereEqualTo("username",keyWord)
                .findObjects(new FindListener<BUser>() {
                    @Override
                    public void done(List<BUser> list, BmobException e) {
                        for(int i=0;i<list.size();i++){
                            Log.i("usr",list.get(i).getUsername());
                        }
                        if (list != null) {
                            Map<String, EaseUser> contactList = RXDbManager.getInstance().getContactList();
                            mAddFriendView.onSearchUserResult(list, RXDbManager.getInstance().getUserNameList());
                        }
                    }
                });
    }

    @Override
    public void onAddFriend(final String contact) {

            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().addContact(contact, "请求添加");
                        ThreadUtils.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                mAddFriendView.onAddFriendResult(contact,true,null);
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        ThreadUtils.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                mAddFriendView.onAddFriendResult(contact,false,e.getMessage());
                            }
                        });
                    }

                }
            });

    }
}
