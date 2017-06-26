package com.hongzhen.ruixin.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;



/**
 * Created by yuhongzhen on 2017/6/5.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<EMMessage> mEMMessageList;

    public ChatAdapter(List<EMMessage> EMMessageList) {
        mEMMessageList = EMMessageList;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage emMessage = mEMMessageList.get(position);
        //direct共有两个值，接受和发送，用来区分消息的类型
        //接收的消息类型是0，发送的类型为1
        return emMessage.direct()== EMMessage.Direct.RECEIVE?0:1;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据消息类型，加载不同的item布局
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receiver, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send, parent, false);
        }
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EMMessage emMessage = mEMMessageList.get(position);//获取消息
        long msgTime = emMessage.getMsgTime();//获取消息时间
        EMTextMessageBody msgBody = (EMTextMessageBody) emMessage.getBody();//获取消息主体
        String message = msgBody.getMessage();
        holder.mTvMsg.setText(message);//设置消息内容到控件上
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));//使用环信工具类将时间转换格式并展示
        if (position==0){
            //设置时间的显示，第一条消息，显示时间
            holder.mTvTime.setVisibility(View.VISIBLE);
        }else{
            EMMessage preMessage = mEMMessageList.get(position - 1);
            long preMsgTime = preMessage.getMsgTime();
            //环信工具类，判断两天消息的间隔，确定是否显示时间
            if (DateUtils.isCloseEnough(msgTime,preMsgTime)){
                holder.mTvTime.setVisibility(View.GONE);
            }else{
                holder.mTvTime.setVisibility(View.VISIBLE);
            }
        }
        //如果是发送的消息，需要显示发送状态的进度
        if (emMessage.direct()== EMMessage.Direct.SEND){
            switch (emMessage.status()) {
                case INPROGRESS:
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.drawable.msg_state_animation);
                    AnimationDrawable drawable = (AnimationDrawable) holder.mIvState.getDrawable();
                    if (drawable.isRunning()){
                        drawable.stop();
                    }
                    drawable.start();
                    break;
                case SUCCESS:
                    holder.mIvState.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.mipmap.msg_error);
                    break;
            }
        }


    }

    @Override
    public int getItemCount() {
        return mEMMessageList==null?0:mEMMessageList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTime;
        TextView mTvMsg;
        ImageView mIvState;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mIvState = (ImageView) itemView.findViewById(R.id.iv_state);
        }
    }
}
