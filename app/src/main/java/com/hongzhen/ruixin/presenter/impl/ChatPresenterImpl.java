package com.hongzhen.ruixin.presenter.impl;


import com.hongzhen.ruixin.listener.CallBackListener;
import com.hongzhen.ruixin.presenter.ChatPresenter;
import com.hongzhen.ruixin.view.activity.ChartView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by yuhongzhen on 2017/6/5.
 */

public class ChatPresenterImpl implements ChatPresenter {
    private ChartView mChartView;
    private List<EMMessage> mEMMessageList = new ArrayList<>();

    public ChatPresenterImpl(ChartView chartView) {
        mChartView = chartView;
    }

    @Override
    public void onInitChatData(String username) {
        //获取消息，并更新UI
        getChatData(username);
        mChartView.onInitChatResult(mEMMessageList);
    }

    @Override
    public void onUpdateChatData(String username) {
        //获取消息，并更新UI
        getChatData(username);
        mChartView.onUpdateChatResult(mEMMessageList.size());
    }


    private void getChatData(String username) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation!=null){
            conversation.markAllMessagesAsRead();//设置全部消息已读
            //如果与好友username有聊天过，获取最近20条展示
            EMMessage lastMessage = conversation.getLastMessage();
            int count=19;
            if (mEMMessageList.size()>=19){
                count=mEMMessageList.size();
            }
            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), count);
            Collections.reverse(emMessages);//倒序排列
            //清空消息集合，添加新的消息集合
            mEMMessageList.clear();
            mEMMessageList.add(lastMessage);
            mEMMessageList.addAll(emMessages);
            Collections.reverse(mEMMessageList);
        }else {
            //防止数据集合旧数据展示错误
            mEMMessageList.clear();
        }
    }



    @Override
    public void onSendMessage(String username, String msg) {
        //封装消息
        EMMessage txtSendMessage = EMMessage.createTxtSendMessage(msg, username);
        txtSendMessage.setStatus(EMMessage.Status.INPROGRESS);
        mEMMessageList.add(txtSendMessage);
        mChartView.onUpdateChatResult(mEMMessageList.size());//更新消息集合的UI展示
        //添加发送消息的回调，进行线程封装
        txtSendMessage.setMessageStatusCallback(new CallBackListener() {
            @Override
            public void onMainSuccess() {
                mChartView.onUpdateChatResult(mEMMessageList.size());
            }
            @Override
            public void onMainError(int i, String s) {
                mChartView.onUpdateChatResult(mEMMessageList.size());
            }
        });
        EMClient.getInstance().chatManager().sendMessage(txtSendMessage);
    }
}
