package com.hongzhen.ruixin.modle;


import com.hongzhen.ruixin.utils.EaseCommonUtils;

/**
 * Created by yuhongzhen on 2017/6/7.
 */

public class EaseUser {
    private String userName;
    private String pwd;
    private String nickName;
    private String avatar;
    private String userInfo;
    //首字母
    protected String initialLetter;

    public EaseUser(String userName) {
        this.userName = userName;
    }
    public String getInitialLetter() {
        if(initialLetter == null){
            EaseCommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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
