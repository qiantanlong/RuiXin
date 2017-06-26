package com.hongzhen.ruixin.eventbus;

/**
 * Created by yuhongzhen on 2017/6/13.
 */

public class EBNickName {
    private String nickNameToSP;

    public EBNickName(String nickNameToSP) {
        this.nickNameToSP = nickNameToSP;
    }

    public String getNickNameToSP() {
        return nickNameToSP;
    }

    public void setNickNameToSP(String nickNameToSP) {
        this.nickNameToSP = nickNameToSP;
    }
}
