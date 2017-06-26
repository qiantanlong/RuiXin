package com.hongzhen.ruixin.view.activity;


import com.hongzhen.ruixin.bmob.BUser;

import java.util.List;



/**
 * Created by yuhongzhen on 2017/6/2.
 */

public interface AddFriendView {
    void onSearchUserResult(List<BUser> listUsersBMOB, List<BUser> listUsersDB);
    void onAddFriendResult(String userName, boolean success, String msg);
}
