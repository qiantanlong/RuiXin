package com.hongzhen.ruixin.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by yuhongzhen on 2017/6/8.
 */

public class BUser extends BmobObject {
    private String username;
    private String pwd;
    private String nick;
    private String avatar;
    private String userInfo;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
