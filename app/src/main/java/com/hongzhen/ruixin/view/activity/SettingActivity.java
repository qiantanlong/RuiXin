package com.hongzhen.ruixin.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.presenter.SettingPresenter;
import com.hongzhen.ruixin.presenter.impl.SettingPresenterImpl;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.ToastUtils;
import com.hyphenate.chat.EMClient;

import static com.hongzhen.ruixin.main.RXApplication.getContext;

public class SettingActivity extends BaseActivity implements SettingView {

    private SettingPresenter mSettingPresenter;
    private ProgressDialog mProgressDialog;
    private Button mBtnLoginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSettingPresenter=new SettingPresenterImpl(this);
        mProgressDialog = mProgressDialog;
        mProgressDialog =new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mBtnLoginOut = (Button) findViewById(R.id.btn_login_out);
        mBtnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("正在退出"+ EMClient.getInstance().getCurrentUser());
                mProgressDialog.show();
                mSettingPresenter.loginOut();
            }
        });
        mBtnLoginOut.setText("退出："+ EMClient.getInstance().getCurrentUser());
    }

    @Override
    public void onLoginOut(String username, boolean success, String msg) {
        mProgressDialog.dismiss();
        if (success){
           startActivity(LoginActivity.class,true);

        }else{
            ToastUtils.showToast(getContext(),msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }
}
