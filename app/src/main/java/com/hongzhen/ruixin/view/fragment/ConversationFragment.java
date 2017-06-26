package com.hongzhen.ruixin.view.fragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.ConversationAdapter;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.eventbus.BMOBData;
import com.hongzhen.ruixin.main.MainActivity;
import com.hongzhen.ruixin.presenter.ConversationPresenter;
import com.hongzhen.ruixin.presenter.impl.ConversationPresenterImpl;
import com.hongzhen.ruixin.view.activity.ChartActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends BaseFragment implements ConversationView, View.OnClickListener, ConversationAdapter.OnItemClickListener {
    private ConversationPresenter mConversationPresenter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private ConversationAdapter mConversationAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);//注册EventBus
        mConversationPresenter=new ConversationPresenterImpl(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFab.setOnClickListener(this);
        mConversationPresenter.initConversation();//初始化会话数据
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mConversationAdapter=null;//解决当这个fragment不被释放，加载数据出错问题
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        MainActivity activity = (MainActivity) getActivity();
//        activity.showToast("收到信消息："+message.getBody().toString());
        mConversationPresenter.initConversation();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BMOBData data){
       if (mConversationAdapter!=null){
           mConversationAdapter.notifyDataSetChanged();
       }
    }

    @Override
    public void onInitConversationResult(List<EMConversation> mEMConversationList) {
        if (mConversationAdapter ==null){
            mConversationAdapter = new ConversationAdapter(getContext(),mEMConversationList);
            mRecyclerView.setAdapter(mConversationAdapter);
            mConversationAdapter.setOnItemClickListener(this);
        }else{
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
//将所有的会话全部比较为已读
        ObjectAnimator.ofFloat(mFab,"rotation",0,360).setDuration(1000).start();
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        mConversationPresenter.initConversation();//重新获取会话数据
        MainActivity activity = (MainActivity) getActivity();
        activity.updateUnreadCount();
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(EMConversation conversation) {
        String userName = conversation.getLastMessage().getUserName();
        MainActivity activity = (MainActivity) getActivity();
        Intent intent = new Intent(activity, ChartActivity.class);
        BUser bUser = RXDbManager.getInstance().getContact(userName);
        intent.putExtra("username",userName);
        intent.putExtra("nickname",bUser.getNick());
        activity.startActivity(intent);
    }
}
