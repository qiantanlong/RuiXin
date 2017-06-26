package com.hongzhen.ruixin.utils;

import android.text.TextUtils;

/**
 * Created by yuhongzhen on 2017/5/20.
 */

public class StringUtils {
    public static boolean checkUserName(String username){
        if (TextUtils.isEmpty(username)){
            return false;
        }
        return username.matches("^((13[0-9])|(14[57])|(15[^4,\\D])|(17[01678])|(18[0-9]))\\d{8}$");
    }
    public static boolean checkPwd(String pwd){
        if (TextUtils.isEmpty(pwd)){
            return false;
        }
        return pwd.matches("^[a-zA-Z0-9]{6,19}$");
    }
    public static  String getInitial(String contact){
        if (TextUtils.isEmpty(contact)){
            return contact;
        }else {
            return contact.substring(0,1).toUpperCase();
        }
    }
}
