package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.presenter.ConversationPresenter;
import com.hongzhen.ruixin.view.fragment.ConversationView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Created by yuhongzhen on 2017/6/4.
 */

public class ConversationPresenterImpl implements ConversationPresenter {
    private ConversationView mConversationView;
    private List<EMConversation> mEMConversationList = new ArrayList<>();

    public ConversationPresenterImpl(ConversationView conversationView) {
        mConversationView = conversationView;
    }

    @Override
    public void initConversation() {
        /**
         * 获取当前用户全部的会话
         */
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        //添加到会话集合中
        mEMConversationList.clear();
        mEMConversationList.addAll(allConversations.values());
        /**
         * 排序，最近的时间在最上面(时间的倒序)
         * 回传到View层
         */
        Collections.sort(mEMConversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                return (int) (o2.getLastMessage().getMsgTime()-o1.getLastMessage().getMsgTime());
            }
        });
        //将数据回传给view展示
        mConversationView.onInitConversationResult(mEMConversationList);

    }
}
