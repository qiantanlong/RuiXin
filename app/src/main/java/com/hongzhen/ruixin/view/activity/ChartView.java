package com.hongzhen.ruixin.view.activity;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by yuhongzhen on 2017/6/2.
 */

public interface ChartView {
    void onInitChatResult(List<EMMessage> mEMMessageList);
    void onUpdateChatResult(int size);
}
