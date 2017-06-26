package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.eventbus.BMOBData;
import com.hongzhen.ruixin.modle.EaseUser;
import com.hongzhen.ruixin.presenter.ContactPresenter;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ThreadUtils;
import com.hongzhen.ruixin.view.fragment.ContactView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by yuhongzhen on 2017/5/23.
 */

public class ContactPresenterImpl implements ContactPresenter {
    private ContactView contactView;
    private List<BUser> contactList = new ArrayList<>();
    private String currentUser;
    private BUser mBUser;
    private List<BUser> mCurrenContacts;

    public ContactPresenterImpl(ContactView contactView) {
        this.contactView = contactView;
    }

    @Override
    public void initContacts() {
        currentUser = EMClient.getInstance().getCurrentUser();
        List<BUser> contactListFromDB = getContactListFromDB();//本地数据库获取好友列表
        LogUtils.i("本地数据库好友列表"+contactListFromDB.size());
        contactList.clear();
        contactList.addAll(contactListFromDB);
        contactView.onInitContact(contactList);//更新UI
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());//获取用户数据BMOB
    }

    private List<BUser> getContactListFromDB() {
        mCurrenContacts = RXDbManager.getInstance().getUserNameList();
        return mCurrenContacts;
    }

    private void updateContactsFromServer(String username) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> allContactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    /*Collections.sort(allContactsFromServer, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    */
                    getUserFromBMOBtoDB(allContactsFromServer);
                    Thread.sleep(500);
                    List<BUser> contactListFromDB = getContactListFromDB();
                    contactList.clear();
                    contactList.addAll(contactListFromDB);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            contactView.onUpdatecontact(true, null);
                        }
                    });


                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            contactView.onUpdatecontact(false, e.getMessage());
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                                    easeUser.setAvatar(mBUser.getAvatar());
                                    LogUtils.i("bmob"+mBUser.getNick()+mBUser.getAvatar()+mBUser.getUsername());
                                    RXDbManager.getInstance().saveContact(easeUser);
                                    EventBus.getDefault().post(new BMOBData());
                                }else {
                                    LogUtils.i("----------------");
                                }
                            }
                        });
            }

        }
    }

    @Override
    public void updateContacts() {
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());
    }

    @Override
    public void onDeleteContact(final String contact) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    afterDelContact(contact, true, null);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    afterDelContact(contact, false, e.toString());
                }
            }
        });
    }

    private void afterDelContact(final String contact, final boolean success, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                contactView.onDeleteContact(contact, success, msg);

            }
        });
    }
}
