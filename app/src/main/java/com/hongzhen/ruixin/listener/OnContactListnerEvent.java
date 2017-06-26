package com.hongzhen.ruixin.listener;

/**
 * Created by yuhongzhen on 2017/6/2.
 */

public class OnContactListnerEvent {
    private String mContact;
    private boolean isAdd;

    public OnContactListnerEvent(String mContact, boolean isAdd) {
        this.mContact = mContact;
        this.isAdd = isAdd;
    }
}
