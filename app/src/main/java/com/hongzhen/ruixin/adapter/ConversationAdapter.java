package com.hongzhen.ruixin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.utils.GlideUtils;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;


/**
 * Created by yuhongzhen on 2017/6/5.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<EMConversation> mEMConversationList;
    private Context mContext;

    public ConversationAdapter(Context mContext,List<EMConversation> EMConversationList) {
        this.mContext=mContext;
        mEMConversationList = EMConversationList;

    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        ConversationViewHolder conversationViewHolder = new ConversationViewHolder(inflate);
        return conversationViewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        final EMConversation emConversation = mEMConversationList.get(position);
        //聊天的对方的名称
        String userName = emConversation.getLastMessage().getUserName();
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        EMMessage lastMessage = emConversation.getLastMessage();
        long msgTime = lastMessage.getMsgTime();
        EMTextMessageBody lastMessageBody = (EMTextMessageBody) lastMessage.getBody();
        String lastMessageBodyMessage = lastMessageBody.getMessage();

        holder.mTvMsg.setText(lastMessageBodyMessage);

        BUser contact = RXDbManager.getInstance().getContact(userName);
        LogUtils.i("conversation"+contact.getAvatar());
        //展示昵称和头像
        if (contact != null) {
            holder.mTvUsername.setText(contact.getNick() + "(" + userName + ")");
            GlideUtils.setImageToImageView(mContext,holder.mIvAvatar,contact.getAvatar());
        }else {
            holder.mTvUsername.setText("" + "(" + userName + ")");
        }
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (unreadMsgCount > 99) {
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText("99+");
        } else if (unreadMsgCount > 0) {
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText(unreadMsgCount + "");
        } else {
            holder.mTvUnread.setVisibility(View.GONE);
        }



        //设置条目的点击事件回调
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(emConversation);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEMConversationList == null ? 0 : mEMConversationList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(EMConversation conversation);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUsername;
        TextView mTvTime;
        TextView mTvMsg;
        TextView mTvUnread;
        ImageView mIvAvatar;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mTvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
            mIvAvatar = (ImageView)itemView.findViewById(R.id.iv_avatar);
        }
    }
}
