package com.hongzhen.ruixin.presenter;

/**
 * Created by yuhongzhen on 2017/6/5.
 */

public interface ChatPresenter {
    void onInitChatData(String username);
    void onUpdateChatData(String username);
    void onSendMessage(String username, String msg);
}
