package com.hongzhen.ruixin.db;

import android.content.Context;

/**
 * Created by yuhongzhen on 2017/6/7.
 */

public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_INFO = "userInfo";
    public UserDao(Context context) {
    }

}
