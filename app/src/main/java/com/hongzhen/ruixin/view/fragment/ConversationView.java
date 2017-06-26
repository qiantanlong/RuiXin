package com.hongzhen.ruixin.view.fragment;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * Created by yuhongzhen on 2017/6/4.
 */

public interface ConversationView {
    void onInitConversationResult(List<EMConversation> mEMConversationList);
}
