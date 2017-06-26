package com.hongzhen.ruixin.view.fragment;

import com.hongzhen.ruixin.bmob.BUser;

import java.util.List;

/**
 * Created by yuhongzhen on 2017/5/23.
 */

public interface ContactView {
    void onInitContact(List<BUser> contactsList);
    void onUpdatecontact(boolean success, String msg);
    void onDeleteContact(String contact, boolean success, String msg);

}
