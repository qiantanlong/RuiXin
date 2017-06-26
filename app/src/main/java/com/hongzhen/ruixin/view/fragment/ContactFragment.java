package com.hongzhen.ruixin.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.ContactAdapter;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.listener.OnContactListnerEvent;
import com.hongzhen.ruixin.main.MainActivity;
import com.hongzhen.ruixin.modle.InviteMessage;
import com.hongzhen.ruixin.presenter.ContactPresenter;
import com.hongzhen.ruixin.presenter.impl.ContactPresenterImpl;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ToastUtils;
import com.hongzhen.ruixin.view.activity.ChartActivity;
import com.hongzhen.ruixin.view.activity.NewFriendActivity;
import com.hongzhen.ruixin.view.widget.ContactLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends BaseFragment implements ContactView, SwipeRefreshLayout.OnRefreshListener {
    private ContactPresenter contactPresenter;
    private ContactLayout mContactLayout;
    private ContactAdapter adapterContact;
    public TextView mTvUnread;
    private View mInflate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mContactLayout = (ContactLayout) view.findViewById(R.id.contactLayout);
        LayoutInflater from = LayoutInflater.from(getActivity());
        mInflate = from.inflate(R.layout.lists_item_contact_list_header, null);
        contactPresenter = new ContactPresenterImpl(this);
        contactPresenter.initContacts();
        mContactLayout.setOnRefreshListener(this);

    }

    @Override
    public void onInitContact(List<BUser> contactsList) {
        LogUtils.i("好友列表"+contactsList.size());
        adapterContact = new ContactAdapter(getContext(),contactsList);
        mContactLayout.setAdapter(adapterContact);
        mTvUnread = (TextView) mInflate.findViewById(R.id.tv_unread);
        mInflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        MainActivity activity = (MainActivity) getActivity();
        if (activity.mIsUnReadMeg){
            mTvUnread.setVisibility(View.VISIBLE);
        }else {
            mTvUnread.setVisibility(View.GONE);
        }
        adapterContact.addHeadView(mInflate);
        adapterContact.setOnItemLongClickListner(new ContactAdapter.onItemLongClickListner() {
            @Override
            public void onItemLongClick(final String username,String nickname, int position) {
                Snackbar.make(mContactLayout, "确定要和" + username+"解除好友关系吗？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactPresenter.onDeleteContact(username);
                    }
                }).show();
            }
        });
        adapterContact.setOnItemClickListner(new ContactAdapter.onItemClickListner() {
            @Override
            public void onItemClick(String username,String nickname, int position) {
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("nickname",nickname);
                getActivity().startActivity(intent);
            }
        });
        adapterContact.setOnHeaderViewClickListener(new ContactAdapter.onHeaderViewClickListener() {
            @Override
            public void onHeaderViewClick(int id) {
                if (id==ContactAdapter.HEADER_NEW_FRIEND){
                    startActivity(new Intent(getActivity(), NewFriendActivity.class));
                }else {
                    ToastUtils.showToast(getContext(),"群聊");
                }
            }
        });
    }

    @Override
    public void onUpdatecontact(boolean success, String msg) {
        adapterContact.notifyDataSetChanged();
        mContactLayout.setRefreshing(false);
    }

    @Override
    public void onDeleteContact(String contact, boolean success, String msg) {
        //删除好友后，环信有删除好友回调，已经处理UI，这里不需要更新UI
        if (success) {
            ToastUtils.showToast(getActivity(), "删除成功！");
        } else {
            ToastUtils.showToast(getActivity(), "删除失败，稍后重试！");
        }
    }


    @Override
    public void onRefresh() {
        contactPresenter.updateContacts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnContactListnerEvent onContactUpdateEvent) {
        contactPresenter.updateContacts();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InviteMessage message){
        LogUtils.i("收到好友请求");
        contactPresenter.updateContacts();
    }
}
