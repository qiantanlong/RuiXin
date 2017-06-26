package com.hongzhen.ruixin.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.ChatAdapter;
import com.hongzhen.ruixin.presenter.impl.ChatPresenterImpl;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChartActivity extends BaseActivity implements ChartView {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_msg)
    EditText mEtMsg;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    private String mUsername;
    private ChatPresenterImpl mChatPresenter;
    private ChatAdapter mAdapterChat;
    private String mNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarColor();//设置状态栏颜色
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mBtnSend.setEnabled(false);
        //接受从通讯录传递的用户数据
        mUsername = getIntent().getStringExtra("username");
        mNickname = getIntent().getStringExtra("nickname");
        if (TextUtils.isEmpty(mUsername)) {
            showToast("跟鬼聊呀，请携带username参数！");
            finish();
            return;
        }
        //设置标题
        mTvTitle.setText("与" + mNickname + "聊天中");

        mEtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    mBtnSend.setEnabled(false);
                } else {
                    mBtnSend.setEnabled(true);
                }
            }
        });
        //获取业务层引用,初始化聊天数据
        mChatPresenter = new ChatPresenterImpl(this);
        mChatPresenter.onInitChatData(mUsername);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message) {
        //当收到信消息的时候
        /*
         *  判断当前这个消息是不是正在聊天的用户给我发的
         *  如果是，让ChatPresenter 更新数据
         *
         */
        String from = message.getFrom();
        if (from.equals(mUsername)) {
            mChatPresenter.onUpdateChatData(mUsername);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onInitChatResult(List<EMMessage> mEMMessageList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapterChat = new ChatAdapter(mEMMessageList);
        mRecyclerView.setAdapter(mAdapterChat);
        if (mEMMessageList.size() != 0) {
            mRecyclerView.scrollToPosition(mEMMessageList.size() - 1);
        }
    }

    @Override
    public void onUpdateChatResult(int size) {
        //当第一发消息给好友时，更新消息界面UI
        EMMessage ok = EMMessage.createTxtSendMessage("ok", EMClient.getInstance().getCurrentUser());
        EventBus.getDefault().post(ok);
        mAdapterChat.notifyDataSetChanged();
        if (size != 0) {
            mRecyclerView.smoothScrollToPosition(size - 1);
        }
    }


    @OnClick({R.id.btn_send,R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String msg = mEtMsg.getText().toString();
                mChatPresenter.onSendMessage(mUsername, msg);
                mEtMsg.getText().clear();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }


}
