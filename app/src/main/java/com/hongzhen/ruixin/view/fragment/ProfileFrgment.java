package com.hongzhen.ruixin.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.eventbus.EBAvator;
import com.hongzhen.ruixin.eventbus.EBNickName;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.GlideUtils;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.view.activity.MyInfoActivity;
import com.hongzhen.ruixin.view.activity.SettingActivity;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by yuhongzhen on 2017/6/11.
 */

public class ProfileFrgment extends BaseFragment implements ProfileView {
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_rxid)
    TextView mTvRxid;
    @BindView(R.id.re_myinfo)
    RelativeLayout mReMyinfo;
    @BindView(R.id.iv_find_password)
    ImageView mIvFindPassword;
    @BindView(R.id.re_find_password)
    RelativeLayout mReFindPassword;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;
    @BindView(R.id.re_setting)
    RelativeLayout mReSetting;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String currentUser = EMClient.getInstance().getCurrentUser();
        String currentUserNick = PreferenceManager.getInstance().getCurrentUserNick();
        LogUtils.i("profile-oncreate"+currentUserNick);
        mTvName.setText(currentUserNick);
        mTvRxid.setText(currentUser);
        GlideUtils.setImageToImageView(getContext(),mIvAvatar,PreferenceManager.getInstance().getCurrentUserAvatar());


    }

    @Override
    public void onLoginOut(String username, boolean success, String msg) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EBNickName nickName) {
        //异步获取nickname，成功后更新UI
        String currentUserNick = PreferenceManager.getInstance().getCurrentUserNick();
        LogUtils.i("profile-onEvent"+currentUserNick);
        mTvName.setText(currentUserNick);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EBAvator avator) {
        //更新头像
        GlideUtils.setImageToImageView(getContext(),mIvAvatar,PreferenceManager.getInstance().getCurrentUserAvatar());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.re_myinfo, R.id.re_setting, R.id.re_find_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.re_myinfo:
                startActivity(new Intent(getActivity(), MyInfoActivity.class));
                break;
            case R.id.re_find_password:
                break;
            case R.id.re_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }
}
